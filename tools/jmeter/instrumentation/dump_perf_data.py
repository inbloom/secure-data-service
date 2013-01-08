import pymongo
import common 

def process_stats(start_time, end_time, stats):
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

def xprocess_stats(stats):
    result = [] 
    thread_ids = set()
    for oneStat in stats: 
        stype, sid, sval = oneStat
        thread_ids.add(sid.split(":",1)[0])
        result.append(tuple(oneStat))
    return thread_ids 

if __name__=="__main__":
    q = {}
    con = pymongo.Connection()
    db = con[common.PERF_DB]
    col = db[common.PERF_COLLECTION]
    q_result = col.find()
    by_req_time = []
    slow_requests = [] 
    uniq_req_ids = {}
    for req in q_result:
        b = req['body']
        url = b['url']
        start = b['startTime']
        end = b['endTime']
        duration = end - start 
        req_id = b.get('reqid', "NO REQUEST ID")
        uniq_req_ids[req_id] = uniq_req_ids.get(req_id, 0) + 1 
        #stats = process_stats(b['stats'])
        by_req_time.append((duration, url))
        if duration > 100:
            stats = process_stats(start, end, b['stats'])
            slow_requests.append((duration, url, stats))

    # by_req_time.sort()
    # for entry in by_req_time:
    #     print entry 

    slow_requests.sort()
    slow_requests.reverse() 
    for entry in slow_requests:
        duration, url, stats = entry
        print "%s: %s" % (duration, url)
        for s in stats:
            func, delta, duration, subrequests = s[:4] 
            print "       %s : %s (%s)" % (duration, func, delta)

    print "Unique request ids:"
    for k,v in uniq_req_ids.iteritems():
        print k,":",v


