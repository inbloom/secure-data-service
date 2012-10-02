from aggregatedriver import cond_from_bands, run_parallel
from instrumentation import instrument
from multiprocessing import Process, Queue 
from Queue import Empty 

import pymongo
import sys 

bands = [
   { "rank" :  0, "description" : "no score",         "abbreviation" : "-", "min" :  0, "max" :  6 },
   { "rank" :  1, "description" : "warning",          "abbreviation" : "W", "min" :  6, "max" : 14 },
   { "rank" :  2, "description" : "below standard",   "abbreviation" : "B", "min" : 15, "max" : 20 },
   { "rank" :  3, "description" : "at standard",      "abbreviation" : "S", "min" : 21, "max" : 27 },
   { "rank" :  4, "description" : "exceeds standard", "abbreviation" : "E", "min" : 28, "max" : 33 }
]
by_rank = dict([(x["rank"], x) for x in bands])

inst = instrument() 

def do_work(assessment_id, db):
    edorg_collection = db["educationOrganization"]
    src_var = "calculatedValues.assessments.%s.HighestEver.ScaleScore" % assessment_id
    target_var = "aggregates.assessments.%s.cut_points" % assessment_id

    def _worker(q):
        try:
          while True:
              print "Queue size: ", q.qsize() 
              school_id = q.get(True, 10) 

              # get all students in the school 
              ssa_collection = db["studentSchoolAssociation"]
              students = [s["body"]["studentId"] 
                          for s  in ssa_collection.find({ "body.schoolId" : school_id }, {"_id" : 0, "body.studentId" : 1})
                          if s.get("body", {}).get("studentId",None)]

              # print "School:   %s" % school_id
              # print "Students: %s" % len(students)

              if students:
                  # assemble a pipeline 
                  cond_stmt = cond_from_bands(src_var, bands)
                  pipeline = [
                    { "$match" : { 
                          "_id" : { "$in" : students }
                      }
                    },
                    { 
                      "$project" : {
                          "band" : cond_stmt
                      }
                    },
                    {
                      "$group" : {
                        "_id" : "$band",
                        "count" : {
                          "$sum" : 1
                        },
                      }
                    }
                  ]

                  result = dict([(by_rank[x["_id"]]["abbreviation"], x["count"]) 
                              for x in db.command("aggregate", "student", pipeline=pipeline)["result"]])
                  edorg_collection.update({"_id" : school_id}, {"$set" : {target_var : result }})
                  # print "Written: ", result 

        except Empty, e:
            pass 

    # run a query over schools 
    N_WORKERS = 10 
    q = Queue() 
    for school in edorg_collection.find({ "type" : "school" }):
        q.put(school["_id"])

    with inst.lap(id="school_aggregation"):
        run_parallel(_worker, (q,), N_WORKERS)

def main():
    hostname = "localhost" if len(sys.argv) < 2 else sys.argv[1]

    con = pymongo.Connection(hostname)
    db = con.sli

    inst.start() 
    if len(sys.argv) < 3:
        result = db["studentAssessmentAssociation"].distinct("body.assessmentId")
        print "Available Assessment IDs:"
        for e in result:
          print e
    else:
        assessment_id = sys.argv[2]
        do_work(assessment_id, db)
    inst.stop()
    inst.print_results() 

    # close the connection 
    con.close()

if __name__=="__main__":
    main() 
