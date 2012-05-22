require 'mongo'
require 'json'

connection = Mongo::Connection.new("nxmongo.slidev.org", 27017)
db = connection.db("ingestion_batch_job")
coll = db.collection("newBatchJob")

if ARGV.count<1
  puts "\e[31mNeed to specify id of the job!\e[0m"
  all=coll.find()
  all.to_a.each do |rec|
    puts rec["_id"]
  end
  exit
end

id=ARGV[0]

rec=coll.find("_id"=>id)

job = rec.to_a[0]

# earliest time on a chunk
earliest=9999999999

# Time the job ended
endTime=0

#times elapsed
out = {}

# Record Counts
rc = {}

# Record Counts for stage
rcStage={"TransformationProcessor" => 0,  "PersistenceProcessor"=>0 }

job["stages"].each do |stage|
  if stage["stageName"] == "TransformationProcessor" or stage["stageName"] == "PersistenceProcessor"
    stage["chunks"].each do |chunk|
      earliest=chunk["startTimestamp"].to_i unless chunk["startTimestamp"].to_i>earliest
      chunk["metrics"].each do |metric|

        out[metric["resourceId"]]=0 unless out[metric["resourceId"]]
        out[metric["resourceId"]]+=chunk["elapsedTime"] unless chunk.nil?

        rc[metric["resourceId"]]=0 unless rc[metric["resourceId"]]
        rc[metric["resourceId"]]+=metric["recordCount"] unless metric.nil? or metric["recordCount"].nil?

        rcStage[stage["stageName"]]+=metric["recordCount"]

      end
    end
  elsif stage["stageName"]=="JobReportingProcessor"
    endTime = stage["chunks"][0]["stopTimestamp"].to_i
  end
end

sum=0
out.each do |key,value|
  rps = "N/A"
  rps = rc[key] / (value / 1000.0) unless value == 0
  puts "[\e[31m#{rc[key]}\e[0m] #{key} => \e[32m#{value}\e[0m ms (\e[35m#{rps.round() unless rps=="N/A"}\e[0m rps)"
  sum+=value
end

mongoCalls=0
mongoTime=0
if !job["executionStats"].nil?
  puts job["executionStats"].inspect 
  job["executionStats"].each do |key,value|
    mongoCalls+=value["left"]
    mongoTime+=value["right"] 
  end
end

transformed = rcStage["TransformationProcessor"]
wallClock = (endTime-earliest)
puts "---------------------------"
puts "Total records for Transformation: #{rcStage["TransformationProcessor"]}"
puts "Total records for Persistence: #{rcStage["PersistenceProcessor"]}"
puts "Total wall-clock time: #{wallClock}sec"
puts "Total time spent (on all nodes): #{sum/1000} sec"
puts "Extrapolated RPS (transformed per total time)  #{(transformed / wallClock )}"
puts "Mongo calls: #{mongoCalls} took #{mongoTime/1000} secs"
puts "ALL DONE"
