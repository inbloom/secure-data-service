import pymongo
import common 
import sys 

MAX_ARG_LENGTH = 1024 

def process_stats(start_time, end_time, stats):
    stack = [(None, None, [])]
    for oneStat in stats:
        # stype, ssub_type, sid, sval, args = (oneStat + [None])[:5]
        stype, ssub_type, sid, sval, args = oneStat.split(":", 4)
        sval = int(sval)
        args = None if args == "null" else args 
        sid = ".".join(sid.split(".")[-2:])

        if stype == "e": 
            if ssub_type == "s":
                stack.append((sid, sval, []))
            elif ssub_type == "e":
                start_sid, start_val, sub_requests = stack.pop()
                duration = sval - start_val 
                if duration > 0:
                    stack[-1][2].append((start_sid, start_val-start_time, duration, args, sub_requests))
            else:
                raise KeyError("Unknown stat subtype:" + ssub_type)
        elif stype == "m":
            stack[-1][2].append((sid, -1, sval, [], []))
        else: 
            raise KeyError("Unknow stat type: %s" % stype)
    d, d, result = stack.pop() 
    return result 

def get_testruns(col):
    result = {}
    for testrun in col.distinct("body.reqid"):
        req_id_convention = testrun.split(":", 1)
        if len(req_id_convention) == 2: 
            testname, instance = req_id_convention
            if instance.startswith("t-"):
                result.setdefault(testname, []).append(testrun)
    return result

def print_stats(total_duration, stats, call_threshold_ms, indent="", uniq_types={}):
    call_sum = 0 
    accumulated = {} 
    for oneStat in stats: 
        stat_id, stat_delta, stat_duration, stat_args, stat_subrequests = oneStat
        # if stat_args:
        #     for arg in stat_args:
        #         if arg:
        #             arg_type, arg_val = arg.split(":",1)
        #             uniq_types.setdefault(arg_type, []).append(len(str(arg_val)))

        # args_string = str(stat_args).lstrip("[").strip("]")
        # if len(args_string) > 200: 
        #     import pdb; pdb.set_trace() 
        #     args_string = "Number of arguments:" + str(len(stat_args))
        if stat_duration >= call_threshold_ms: 
            print "%s%s: %s" % (indent + "    ", stat_duration, stat_id)
            if False and stat_args:
                print indent + "    " + "  ---------"
                print indent + "    " + "  | Args: |"
                print indent + "    " + "  ---------"
                for oneArg in stat_args: 
                    arg_type, arg_val = oneArg.split(":", 1)
                    arg_type = arg_type.rsplit(".", 1)[1]
                    arg_str = arg_type + ":" + arg_val
                    if len(arg_str) > MAX_ARG_LENGTH: 
                        arg_str = arg_str[:MAX_ARG_LENGTH] + "..." + ("[length = %s]" % len(arg_str))
                    print indent + "    " + "  " + arg_str
            print_stats(total_duration, stat_subrequests, call_threshold_ms, indent + "    ", uniq_types)
        else:
            accumulated.setdefault(stat_id, []).append(stat_duration)
        if indent == "":
            call_sum += stat_duration
    if accumulated: 
        print indent + "    " + "Accumulated Fast Methods:"
        for k, v in accumulated.iteritems():
            print indent + "    " + "  %s : %s (ct) : %s (ms)" % (k, len(v), sum(v))
    if indent == "":
        print "%5.2f%% of ms accounted for." % ((float(call_sum)/total_duration)*100.0)

def extract_by_testrun(testrun, col, req_threshold_ms):
    by_response_time = []
    slow_requests = [] 

    q_result = col.find({"body.reqid" : testrun})
    for req in q_result:
        b = req['body']
        url = b['url']
        start = b['startTime']
        end = b['endTime']
        duration = end - start 
        req_id = b.get('reqid', "NO REQUEST ID")
        by_response_time.append((duration, url))
        if duration > req_threshold_ms:
            stats = process_stats(start, end, b['stats'])
            slow_requests.append((duration, url, stats))

    # return the slow requests according to the threshold
    return (by_response_time, slow_requests)

def print_slow_requests(slow_requests, call_threshold_ms): 
    for entry in slow_requests:
        duration, url, stats = entry
        print "%s: %s" % (duration, url)
        print_stats(duration, stats, call_threshold_ms)
        print "-" * 60 

def get_call_stats(requests, call_stats):
    def recursive_call_stats(stats, call_stack):
        for sid, delta, duration, args, subrequests in stats:
            call_stack.append(sid)
            call_stats.setdefault(tuple(call_stack), []).append(duration)
            recursive_call_stats(subrequests, call_stack)
            call_stack.pop()

    for duration, url, stats in requests:
        recursive_call_stats(stats, [])

if __name__=="__main__":
    mongo_host = sys.argv[1] if len(sys.argv) > 1 else common.MONGO_HOST
    req_threshold_ms = int(sys.argv[2]) if len(sys.argv) > 2 else 50
    call_threshold_ms = int(sys.argv[3]) if len(sys.argv) > 3 else 10 

    q = {}
    print "Connecting to: %s" % mongo_host
    con = pymongo.Connection(mongo_host)
    db = con[common.PERF_DB]
    col = db[common.PERF_COLLECTION]

    testruns = get_testruns(col)
    print "Found the following testruns:"
    for k in sorted(testruns.keys()): 
        print "    " + k
        for instance in testruns[k]:
            print "        ", instance 

    for testname, instances in testruns.iteritems():
        for oneTestRun in instances:
            print ("-" * 60)
            all_requests, slow_requests = extract_by_testrun(oneTestRun, col, req_threshold_ms)
            slow_requests.sort() 
            print_slow_requests(slow_requests, call_threshold_ms)

    call_stats = {} 
    for testname, instances in testruns.iteritems():
        for oneTestRun in instances:
            all_requests, slow_requests = extract_by_testrun(oneTestRun, col, 0)
            get_call_stats(slow_requests, call_stats)

    acc_call_stats = [(k, len(v), sum(v)) for k,v in call_stats.iteritems() ]
    acc_call_stats.sort(lambda a,b: cmp((a[0], a[2]), (b[0], b[2])))

    print "-------------------"
    print "Call statistics:"
    prefix = acc_call_stats[0][0][0]
    for func_id, times, duration in acc_call_stats:
        if func_id[0] != prefix:
            print "-" * 60 
            prefix = func_id[0]
        print "%9d : %5d : %7.2f : %s" % (duration, times, float(duration)/times, "->".join(func_id))