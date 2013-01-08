import pymongo
import common 

MAX_ARG_LENGTH = 1024 

def xprocess_stats(start_time, end_time, stats):
    result = []
    stack = []
    current = [] 
    sub_requests = [] 
    for oneStat in stats:
        stype, sid, sval = oneStat
        sid = sid.split(":",1)[1].strip() 
        # print (sid, sval)
        if sid.startswith("start:"):
            stack.append((sid, sval))
            sub_requests = []
        elif sid.startswith("end:"):
            if not stack:
                print "-" * 40 
                for x in stats:
                    print x
            start_sid, start_val = stack.pop()
            if not stack:
                duration = sval - start_val 
                if duration > 0: 
                    result.append((start_sid.split(":", 1)[1], start_val-start_time, duration, current, sub_requests))
                current = []
            else: 
                duration = sval - start_val 
                if duration > 0: 
                    current.append((start_sid.split(":", 1)[1], start_val-start_time, duration, sub_requests))
            sub_requests = []
        else:
            if sval > 0: 
                sub_requests.append((sid, sval))
    return result 

def process_stats(start_time, end_time, stats, min_threshold=0):
    stack = [(None, None, [])]
    for oneStat in stats:
        stype, ssub_type, sid, sval, args = (oneStat + [None])[:5]
        if stype == "e": 
            if ssub_type == "start":
                stack.append((sid, sval, []))
            elif ssub_type == "end":
                start_sid, start_val, sub_requests = stack.pop()
                duration = sval - start_val 
                if duration > min_threshold:
                    stack[-1][2].append((start_sid, start_val-start_time, duration, args, sub_requests))
            else:
                raise KeyError("Unknown subtype:" + ssub_type)
    d, d, result = stack.pop() 
    return result 

def xprocess_stats(stats):
    result = [] 
    thread_ids = set()
    for oneStat in stats: 
        stype, sid, sval = oneStat
        thread_ids.add(sid.split(":",1)[0])
        result.append(tuple(oneStat))
    return thread_ids 

def get_testruns(col):
    result = {}
    for testrun in col.distinct("body.reqid"):
        testname, instance = testrun.split(":", 1)
        if instance.startswith("t-"):
            result.setdefault(testname, []).append(testrun)
    return result

def print_stats(stats, indent="", uniq_types={}):
    for oneStat in stats: 
        stat_id, stat_delta, stat_duration, stat_args, stat_subrequests = oneStat
        if stat_args:
            for arg in stat_args:
                if arg:
                    arg_type, arg_val = arg.split(":",1)
                    uniq_types.setdefault(arg_type, []).append(len(str(arg_val)))

        # args_string = str(stat_args).lstrip("[").strip("]")
        # if len(args_string) > 200: 
        #     import pdb; pdb.set_trace() 
        #     args_string = "Number of arguments:" + str(len(stat_args))
        print "%s%s:%s: %s" % (indent + "    ", stat_delta, stat_duration, stat_id)
        if stat_duration > 100: 
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
        print_stats(stat_subrequests, indent + "    ", uniq_types)
    # if indent == "":
    #     print "Unique types:"
    #     for k,v in uniq_types.iteritems():
    #         print k, ":", sorted(v) 

def extract_by_testrun(testrun, col): 
    by_req_time = []
    slow_requests = [] 
    uniq_req_ids = {}

    q_result = col.find({"body.reqid" : testrun})
    for req in q_result:
        b = req['body']
        url = b['url']
        start = b['startTime']
        end = b['endTime']
        duration = end - start 
        req_id = b.get('reqid', "NO REQUEST ID")
        uniq_req_ids[req_id] = uniq_req_ids.get(req_id, 0) + 1 
        by_req_time.append((duration, url))
        if duration > 100:
            # import pdb; pdb.set_trace() 
            stats = process_stats(start, end, b['stats'])
            slow_requests.append((duration, url, stats))

    slow_requests.sort()
    slow_requests.reverse() 
    for entry in slow_requests:
        duration, url, stats = entry
        print "----------------------------------------------"
        print "%s: %s" % (duration, url)
        print_stats(stats)

    # print "Unique request ids:"
    # for k,v in uniq_req_ids.iteritems():
    #     print k,":",v
    # return None 

if __name__=="__main__":
    q = {}
    con = pymongo.Connection()
    db = con[common.PERF_DB]
    col = db[common.PERF_COLLECTION]

    testruns = get_testruns(col)
    for k,v in testruns.iteritems():
        print k, ":", v 

    for testname, instances in testruns.iteritems():
        for oneTestRun in instances:
            print ("-" * 60) + "\n" + ("-" * 60)
            extract_by_testrun(oneTestRun, col)
            #print_stats(stats)



