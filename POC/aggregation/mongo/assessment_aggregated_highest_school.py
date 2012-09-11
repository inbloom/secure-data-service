import pymongo
import sys 

print "Name:", __name__
def run_pipeline(assessment_id, db):
    pipeline = [
      {
        "$match" : { 
            "type" : "school"
        }
      },
      { "$project" : {
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

    for entry in db.command("aggregate", "studentAssessmentAssociation", pipeline=pipeline)["result"]:
       all_scores = [float(x) for x in entry["scores"]]
       entry["highestScore"] = max(all_scores)
       yield entry 

def write_result(target_query, target_var, src_field, collection, db, results): 
    col = db[collection]
    for entry in results: 
        q = dict((k % entry, v % entry) for k,v in id_query.iter_items())
        col.update(q, {"$set" : { target_var : entry[src_field] } } )
        print ""

def main():
    con = pymongo.Connection()
    db = con.sli

    if len(sys.argv) != 3:
      result = set()
      result = set((x["body"]["assessmentId"] for x in db["studentAssessmentAssociation"].find({}, ["body.assessmentId"])))
      for e in result:
        print e
    else:
      assessment_id = sys.argv[1]
      target_collection = sys.argv[2]
      target_query = {"_id" : "%(studentId)s"}
      target_var = "calculatedValues.assessments.%s.HighestEver.ScaleScore" % assessment_id
      src_field = "highestScore"

      # run the pipeline and write the result 
      results = run_pipeline(assessment_id, db)
      write_result(target_query, target_var, src_field, target_collection, db, results)

    # close the connection 
    con.close()

  if __name__=="__main__":
    main() 
