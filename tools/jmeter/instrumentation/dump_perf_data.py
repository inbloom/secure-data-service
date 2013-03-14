import pymongo
import common
import sys

MAX_ARG_LENGTH = 1024
NANO_TO_MILLI  = 1000000.0

#
# Get any data from the given collection that corresponds to a test run. These are
# identified by the reqid field in the document body. Return a mapping of test name to
# testrun instances.
#
def get_test_runs(col):
    result = {}
    for testrun in col.distinct("body.reqid"):
        req_id_convention = testrun.split(":", 1)
        if len(req_id_convention) == 2:
            testname, instance = req_id_convention
            if instance.startswith("t-"):
                result.setdefault(testname, []).append(testrun)
    return result


#
# Given the start time, end time, and metrics from the apiPerf mongo collection,
# calculate summary statistics for a request. Resulting stats are returned as
# a list of tuples (stat_id, duration, [sub_requests])
#
def get_request_stats(start_time, end_time, refs):
    # callId, timestamp, subcalls
    stack = [(None, None, [])]

    con = pymongo.Connection(mongo_host)
    db = con[common.PERF_DB]
    col = db[common.PERF_STATS_COLLECTION]

    for one_ref in refs:
        q_result = col.find({"body.id" : one_ref})
        for req in q_result:
            b = req['body']
            stats = b['stats']

            for one_stat in stats:
                stat_type, stat_sub_type, stat_id, stat_time, args = one_stat.split(":", 4)

                stat_id = ".".join(stat_id.split(".")[-2:])
                timestamp = int(stat_time)

                args = args.split(":", 2)

                args[0] = None if args[0] == "null" else args

                if stat_type == "e":

                    if stat_sub_type == "s":
                        stack.append((stat_id, timestamp, []))

                    elif stat_sub_type == "e":
                        start_sid, stat_val, sub_requests = stack.pop()
                        if (stat_val == None):
                            # end without a start
                            if len(stack) == 0:
                              stack = [(None, None, [])]
                        else:
                            duration = timestamp - stat_val
                            stack[-1][2].append((start_sid, stat_val - start_time, duration, args, sub_requests))
                    else:
                        raise KeyError("Unknown stat subtype:" + ssub_type)

                elif stype == "m":
                    stack[-1][2].append((stat_id, -1, stat_time, [], []))
                else:
                    raise KeyError("Unknow stat type: %s" % stype)

    print("Stack depth: %d" %(len(stack)))
    print("Stack: %s" % (stack[-1][2],))
    d, d, result = stack[-2]

    return result


#
# Given one or more request statistics, recursively retrieve all the API calls required to
# process the request. Roll all call statistics into the call_stats argument and return it.
#
def get_call_stats(requests, call_stats):
    def recursive_call_stats(stats, call_stack):
        for sid, delta, duration, args, subrequests in stats:
            call_stack.append(sid)
            call_stats.setdefault(tuple(call_stack), []).append(duration)
            recursive_call_stats(subrequests, call_stack)
            call_stack.pop()

    for duration, url, stats in requests:
        recursive_call_stats(stats, [])

    print(call_stats)
    return call_stats


#
# Print out the request information and related statistics.
#
def print_stats(total_duration, stats, indent="", uniq_types={}):
    call_sum = 0
    accumulated = {}
    for oneStat in stats:
        stat_id, stat_delta, stat_duration, stat_args, stat_subrequests = oneStat
        if stat_args:
            for arg in stat_args:
                if arg:
                    arg_type, arg_val = arg.split(":",1)
                    uniq_types.setdefault(arg_type, []).append(len(str(arg_val)))

        args_string = str(stat_args).lstrip("[").strip("]")
        if len(args_string) > 200:
            import pdb; pdb.set_trace()
            args_string = "Number of arguments:" + str(len(stat_args))
        if stat_duration >= 0:
            print "%s%lf: %s" % (indent + "    ", float(stat_duration / NANO_TO_MILLI), stat_id)
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
            print_stats(total_duration, stat_subrequests, indent + "    ", uniq_types)
        else:
            accumulated.setdefault(stat_id, []).append(stat_duration)
        if indent == "":
            call_sum += stat_duration
    if accumulated:
        print indent + "    " + "Accumulated Fast Methods:"
        for k, v in accumulated.iteritems():
            print indent + "    " + "  %s : %s (ct) : %s (ms)" % (k, len(v), sum(v))
    if indent == "":
        print "%5.2f%% of ms accounted for." % (((float(call_sum))/(float(total_duration)))*100.0)


#
# Extract statistics for the provided test run. Collect calls that exceed the
# request threshold for further analysis.
#
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

        stats = get_request_stats(start, end, b['stats'])

        if duration >= req_threshold_ms:
            slow_requests.append((duration, url, stats))

    # return the slow requests according to the threshold
    return (by_response_time, slow_requests)


#
# Print out the list of slow API requests.
#
def print_slow_requests(slow_requests):
    for entry in slow_requests:
        duration, url, stats = entry
        print "[slow] %s: %s" % (duration, url)
        print_stats(duration * NANO_TO_MILLI, stats)
        print "-" * 60



if __name__=="__main__":
    mongo_host = sys.argv[1] if len(sys.argv) > 1 else common.MONGO_HOST
    req_threshold_ms = int(sys.argv[2]) if len(sys.argv) > 2 else 50

    q = {}
    print "Connecting to: %s" % mongo_host
    con = pymongo.Connection(mongo_host)
    db = con[common.PERF_DB]
    col = db[common.PERF_COLLECTION]

    testruns = get_test_runs(col)
    print "Found the following testruns:"
    for k in sorted(testruns.keys()):
        print "    " + k
        for instance in testruns[k]:
            print "     +   ", instance

    call_stats = {}
    for testname, instances in testruns.iteritems():
        for oneTestRun in instances:
            print ("-" * 60)
            all_requests, slow_requests = extract_by_testrun(oneTestRun, col, req_threshold_ms)
            slow_requests.sort()
            print_slow_requests(slow_requests)
            get_call_stats(slow_requests, call_stats)

    if len(call_stats) > 0 :
        acc_call_stats = [(k, len(v), sum(v)) for k,v in call_stats.iteritems() ]
        acc_call_stats.sort(lambda a,b: cmp((a[0], a[2]), (b[0], b[2])))

        print ("-" * 60)
        print "Call statistics:"
        prefix = acc_call_stats[0][0][0]
        for func_id, times, duration in acc_call_stats:
            if func_id[0] != prefix:
                print "-" * 60
                prefix = func_id[0]
            print "%7.2f (ms) : count %5d : average %7.2f (ms) : function %s" % (float(duration / NANO_TO_MILLI), times, float(duration/NANO_TO_MILLI)/times, "->".join(func_id))
