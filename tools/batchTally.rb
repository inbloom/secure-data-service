require 'mongo'
require 'json'

connection = Mongo::Connection.new("nxmongo3.slidev.org", 27017)
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

# pitProcessingStartTime time on a chunk
pitProcessingStartTime=9999999999

# Time the job ended
jobProcessingEndTime=0

#times elapsed
pitElapsedPerResource = {}

# Record Counts
rcPerResource = {}

#total job time
jobStart = job["jobStartTimestamp"]
jobEnd = job["jobStopTimestamp" ]
if ! jobEnd.nil? && !jobStart.nil?  
	totalJobTime = jobEnd-jobStart
end
# Record Counts for stage
rcStage={
  "ZipFileProcessor" => 0,  "ControlFilePreProcessor"=>0, "ControlFileProcessor" => 0,  "XmlFileProcessor"=>0,
  "EdFiProcessor" => 0, "TransformationProcessor" => 0,  "PersistenceProcessor"=>0
}

cpuProcessingTime=0
job["stages"].each do |stage|
  if stage["stageName"] == "TransformationProcessor" or stage["stageName"] == "PersistenceProcessor"
    # Pit nodes
    stage["chunks"].each do |chunk|
      pitProcessingStartTime=chunk["startTimestamp"].to_i unless chunk["startTimestamp"].to_i>pitProcessingStartTime
      chunk["metrics"].each do |metric|

        pitElapsedPerResource[metric["resourceId"]]=0 unless pitElapsedPerResource[metric["resourceId"]]
        pitElapsedPerResource[metric["resourceId"]]+=chunk["elapsedTime"] unless chunk.nil?

        rcPerResource[metric["resourceId"]]=0 unless rcPerResource[metric["resourceId"]]
        rcPerResource[metric["resourceId"]]+=metric["recordCount"] unless metric.nil? or metric["recordCount"].nil?

        rcStage[stage["stageName"]]+=metric["recordCount"]
      end
    end 
  elsif stage["stageName"]=="JobReportingProcessor"
    # Job reporting
    jobProcessingEndTime = stage["chunks"][0]["stopTimestamp"].to_i
  else
    # Maestro processing nodes - currently single threaded so add all stage time
    cpuProcessingTime += stage["chunks"][0]["elapsedTime"].to_i

    # Record counts for Maestro processing nodes
    stage["chunks"].each do |chunk|
      chunk["metrics"].each do |metric|
        rcStage[stage["stageName"]]+=metric["recordCount"]
      end
    end
  end
end

pitElapsedPerResource.each do |key,value|
  rps = "N/A"
  rps = rcPerResource[key] / (value / 1000.0) unless value == 0
  puts "[\e[31m#{rcPerResource[key]}\e[0m] #{key} => \e[32m#{value}\e[0m ms (\e[35m#{rps.round() unless rps=="N/A"}\e[0m rps)"
  cpuProcessingTime+=value
end

executionStats = {}

if !job["executionStats"].nil? 
  job["executionStats"].each do |hostName,value| 
    nodeType = "pit"
    if hostName=="nxmaestro"
      nodeType = "maestro"
    end

    if executionStats[nodeType].nil?
      executionStats[nodeType] = {}
    end

    value.each do |functionName,innerValue|
      if executionStats[nodeType][functionName].nil?
        executionStats[nodeType][functionName] = {}
      end

      executionStats[nodeType][functionName]["calls"] = 0 unless executionStats[nodeType][functionName]["calls"]
      executionStats[nodeType][functionName]["calls"] += innerValue["left"]

      executionStats[nodeType][functionName]["time"] = 0 unless executionStats[nodeType][functionName]["time"]
      executionStats[nodeType][functionName]["time"] += innerValue["right"]
    end
  end
end

transformedRecordCount = rcStage["TransformationProcessor"]
persistedRecordCount = rcStage["PersistenceProcessor"]
edfiRecordCount = rcStage["EdFiProcessor"]
wallClockForPits = (jobProcessingEndTime-pitProcessingStartTime)
combinedProcessingTime = cpuProcessingTime/1000

puts "---------------------------"
puts "Total records for Transformation: #{transformedRecordCount}"
puts "Total records for Persistence: #{persistedRecordCount}"
puts "Total wall-clock time: #{wallClockForPits}sec"

puts ""
puts "Combined processing time on all nodes: #{combinedProcessingTime} sec"
puts "Pit RPS (transformed / wall-clock)  #{(transformedRecordCount / wallClockForPits )}"

puts ""
executionStats.each do |nodeType,functions|
  functions.each do |functionName,stats|
    callStats = stats["calls"]
    timeStats = stats["time"]
    puts "(#{nodeType}) Mongo #{functionName} calls: #{callStats} took #{timeStats/1000} secs (#{timeStats/10/combinedProcessingTime}% of processing time)"
  end
end

#puts "Mongo calls (ALL): #{mongoCalls} took #{mongoTime/1000} secs"

puts ""
puts "Job started: #{jobStart.getlocal}"
if ! jobEnd.nil? 
puts "Job ended: #{jobEnd.getlocal}"
end 
if  !totalJobTime.nil?  
	puts "Total Job time #{totalJobTime} sec"
	puts "Job RPS #{edfiRecordCount / totalJobTime}" 
end

puts "ALL DONE"
