import pymongo

def run_pipeline(pipeline, input_collection, db, post_processor = lambda x: x):
    for entry in db.command("aggregate", input_collection, pipeline=pipeline)["result"]: 
        yield post_processor(entry)

def write_result(target_query, target_var, src_field, collection, db, results): 
    col = db[collection]
    for entry in results: 
        q = dict((k % entry, v % entry) for k,v in target_query.iteritems())
        col.update(q, {"$set" : { target_var : entry[src_field] } } )
        print entry 


def cond_from_bands(varname, bands):
    varname = "$"+varname
    stack = bands[:]
    last = stack.pop()["rank"]
    while stack: 
        popped = stack.pop() 
        last = {"$cond" : [ { "$lte" : [varname, popped["max"]] }, popped["rank"], last]}
    return last 
