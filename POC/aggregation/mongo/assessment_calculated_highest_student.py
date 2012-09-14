from aggregatedriver import run_pipeline, write_result

import pymongo
import sys 

def do_work(assessment_id, db): 
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
    target_collection = "student"
    results = run_pipeline(PIPELINE, input_collection, db, post_processor)
    write_result(target_query, target_var, src_field, target_collection, db, results)

    col = db[target_collection]
    total_count = col.find({}).count()

    written_docs = col.find({ target_var  : { "$exists" : True } }, 
                            { "_id" : True, target_var : True })
    found_count = written_docs.count()
    print "%s of %s records in %s contain values" % (found_count, total_count, target_collection)

def main():
    hostname = "localhost" if len(sys.argv) < 2 else sys.argv[1]
    con = pymongo.Connection(hostname, 27017)
    db = con.sli

    if len(sys.argv) < 3:
        result = set()
        result = set((x["body"]["assessmentId"] for x in db["studentAssessmentAssociation"].find({}, ["body.assessmentId"])))
        print "Available Assessment IDs:"
        for e in result:
          print e
    else:
        assessment_id = sys.argv[2]
        do_work(assessment_id, db)

    # close the connection 
    con.close()

if __name__=="__main__":
    main() 
