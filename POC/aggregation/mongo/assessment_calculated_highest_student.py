import pymongo
import sys 


def run_pipeline(pipeline, input_collection, db, post_processor = lambda x: x):
    for entry in db.command("aggregate", input_collection, pipeline=pipeline)["result"]: 
        yield post_processor(entry)

def write_result(target_query, target_var, src_field, collection, db, results): 
    col = db[collection]
    for entry in results: 
        q = dict((k % entry, v % entry) for k,v in target_query.iteritems())
        col.update(q, {"$set" : { target_var : entry[src_field] } } )
        print entry 

def do_work(assessment_id, target_collection, db): 
    PIPELINE = [
      {
        "$match" : { 
          "body.assessmentId" : assessment_id, 
          "body.scoreResults.assessmentReportingMethod" : "Scale score" 
        }
      },
      { 
        "$project" : {
          "tenantId"  : "$metaData.tenantId",
          "studentId" : "$body.studentId",
          "result" : "$body.scoreResults.result"
        }
      },
      {
        "$unwind" : "$result",
      },
      {
        "$group" : {
          "_id" : "$studentId",
          "scores" : { "$push" : "$result" }
        }
      }
    ]
    input_collection = "studentAssessmentAssociation"
    target_query = {"_id" : "%(_id)s"}
    target_var = "calculatedValues.assessments.%s.HighestEver.ScaleScore" % assessment_id
    src_field = "highestScore"


    def post_processor(entry):
         all_scores = [float(x) for x in entry["scores"]]
         entry["highestScore"] = max(all_scores)
         return entry

    # run the pipeline and write the result 
    results = run_pipeline(PIPELINE, input_collection, db, post_processor)
    write_result(target_query, target_var, src_field, target_collection, db, results)

    col = db[target_collection]
    total_count = col.find({}).count()

    written_docs = col.find({ target_var  : { "$exists" : True } }, 
                            { "_id" : True, target_var : True })
    found_count = written_docs.count()
    print "%s of %s records in %s contain values" % (found_count, total_count, target_collection)
    for entry in written_docs:
        print entry

def main():
    con = pymongo.Connection()
    db = con.sli

    if len(sys.argv) != 3:
        result = set()
        result = set((x["body"]["assessmentId"] for x in db["studentAssessmentAssociation"].find({}, ["body.assessmentId"])))
        print "Available Assessment IDs:"
        for e in result:
          print e
    else:
        assessment_id = sys.argv[1]
        target_collection = sys.argv[2]
        do_work(assessment_id, target_collection, db)

    # close the connection 
    con.close()

if __name__=="__main__":
    main() 
