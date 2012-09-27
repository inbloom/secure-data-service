from aggregatedriver import run_pipeline, write_result

import pymongo
import sys 

def do_work(assessment_id, db): 
    def find_max_score(entry):
        all_scores = [float(x) for x in entry["scores"]]
        entry["highestScore"] = max(all_scores)
        del entry["scores"]
        return entry

    temp_collection = "temp_calculated_highest_score"
    input_collection = "studentAssessmentAssociation"
    target_query = {"_id" : "%(_id)s"}
    target_var = "calculatedValues.assessments.%s.HighestEver.ScaleScore" % assessment_id
    src_field = "highestScore"

    cursor = db[input_collection].find({"body.assessmentId" : assessment_id}, 
                                       {"_id" : 0, "body.studentId" : 1})
    student_ids = list(set((x["body"]["studentId"] for x in cursor if x.get("body", {}).get("studentId", None))))
    print "Found %s students." % len(student_ids)

    BATCH_SIZE = 5000
    batch = 0
    while (batch * BATCH_SIZE) < len(student_ids):
        student_batch = student_ids[batch * BATCH_SIZE:batch * BATCH_SIZE + BATCH_SIZE]
        PIPELINE = [
          {
            "$match" : { 
              "body.assessmentId" : assessment_id, 
              "body.scoreResults.assessmentReportingMethod" : "Scale score",
              "body.studentId" : { "$in" : student_batch }
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

        # # run the pipeline, end if there are no more records 
        # temp_result = [find_max_score(x) for x in db.command("aggregate", input_collection, pipeline=PIPELINE)["result"]]
        # if not temp_result: break
        # db[temp_collection].insert(temp_result)

        # print "Wrote %s records to %s" % (len(temp_result), temp_collection)
        # batch += 1

        target_collection = "student"
        results = [find_max_score(x) 
                   for x in db.command("aggregate", input_collection, pipeline=PIPELINE)["result"]]
        col = db[target_collection]
        for entry in results:
          col.update({"_id" : entry["_id"]}, {"$set" : { target_var : entry[src_field]}})
        batch += 1
        print "%s students processed." % (batch * BATCH_SIZE)

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
        result = set((x["body"]["assessmentId"] for x in db["studentAssessmentAssociation"].find({}, ["body.assessmentId"]) if x.get("body", {}).get("assessmentId", None)))
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
