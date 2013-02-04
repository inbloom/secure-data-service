=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'mongo'
require 'json'

def printStats(stats)
  stats=Hash[stats.sort {|a,b| b[1]["time"]<=>a[1]["time"]}]
  stats.each do |name,stat|
    if stat["time"]>0 # ignore entries less than provide ms
      printf "\e[32m%-65s\e[0m \e[31m%11d\e[0m \e[35m%11d sec\e[0m \e[34m%5d ms\e[0m\n",name,stat["calls"],stat["time"]/1000, stat["time"]/stat["calls"]
    end
  end
  printf "%55s\n","***"
end

connection = Mongo::Connection.new("localhost", 27017)
db = connection.db("ingestion_batch_job")
coll = db.collection("newBatchJob")

if ARGV.count<1
  puts "\e[31mNeed to specify id of the job!\e[0m"
  all=coll.find({},{:fields=>{"_id"=>1}})
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
  puts "Total Job time #{totalJobTime} sec"
end
# Record Counts for stage
rcStage={
  "ZipFileProcessor" => 0,  "ControlFilePreProcessor"=>0, "ControlFileProcessor" => 0,  "XmlFileProcessor"=>0,
  "EdFiProcessor" => 0, "TransformationProcessor" => 0,  "PersistenceProcessor"=>0
}

maestroProcessingTime=0
job["stages"].each do |stage|
  if stage["stageName"] == "TransformationProcessor" or stage["stageName"] == "PersistenceProcessor"

    pitProcessingStartTime=stage["startTimestamp"].to_i unless stage["startTimestamp"].to_i>pitProcessingStartTime
    
    # collect stage data from batchJobStage collection
    colStage = db.collection("batchJobStage")
    countOfStages = colStage.find({"jobId" => id, "stageName" => stage["stageName"]}).count.to_s
    puts "Found " + countOfStages + " " + stage["stageName"] + " stage entries in batchJobStage collection"
    
    stageEntries = colStage.find({"jobId" => id, "stageName" => stage["stageName"]}).batch_size(1000)

    stageEntries.each do |entry|
      if entry["metrics"].length > 0

        entry["metrics"].each do |metric|
          pitElapsedPerResource[metric["resourceId"]]=0 unless pitElapsedPerResource[metric["resourceId"]]
          pitElapsedPerResource[metric["resourceId"]]+=entry["elapsedTime"]
          
          rcPerResource[metric["resourceId"]]=0 unless rcPerResource[metric["resourceId"]]
          rcPerResource[metric["resourceId"]]+=metric["recordCount"] unless metric.nil? or metric["recordCount"].nil?
          
          rcStage[stage["stageName"]] += metric["recordCount"].to_i
        end
      end
    end
    
  elsif stage["stageName"]=="JobReportingProcessor"
    # Job reporting
    jobProcessingEndTime = stage["stopTimestamp"].to_i
  else
    # Maestro processing nodes - currently single threaded so add all stage time
    maestroProcessingTime += stage["elapsedTime"].to_i

    # Record counts for Maestro processing nodes
    stage["metrics"].each do |metric|
      rcStage[stage["stageName"]]+=metric["recordCount"]
    end
  end
end

pitProcessingTime = 0
pitElapsedPerResource.each do |key,value|
  rps = "N/A"
  rps = rcPerResource[key] / (value / 1000.0) unless value == 0
  puts "[\e[31m#{rcPerResource[key]}\e[0m] #{key} => \e[32m#{value}\e[0m ms (\e[35m#{rps.round() unless rps=="N/A"}\e[0m rps)"
  pitProcessingTime+=value
end

dbs={}
functions={}
collections={}

writeCount=0
readCount=0
writeTime=0
readTime=0

if !job["executionStats"].nil?
  job["executionStats"].each do |hostName,value|

    value.each do |functionName,innerValue|
      pieces=functionName.split("#")
      dbName = pieces[0]
      functionCall = pieces[1]
      collectionName = pieces[2]

      if functionCall != "getCollection"

        if dbName == "sli"
          if functionCall.include? "update" or functionCall.include? "insert"
            writeCount+=innerValue["left"]
            writeTime+=innerValue["right"]
          else
            readCount+=innerValue["left"]
            readTime+=innerValue["right"]
          end
        end
        
        functionName = dbName+"."+functionCall

        dbs[dbName]={"calls"=>0,"time"=>0} unless dbs[dbName]
        functions[functionName]={"calls"=>0,"time"=>0} unless functions[functionName]
        collections[collectionName]={"calls"=>0,"time"=>0} unless collections[collectionName]

        dbs[dbName]["calls"]+=innerValue["left"]
        dbs[dbName]["time"]+=innerValue["right"]

        functions[functionName]["calls"]+=innerValue["left"]
        functions[functionName]["time"]+=innerValue["right"]

        collections[collectionName]["calls"]+=innerValue["left"]
        collections[collectionName]["time"]+=innerValue["right"]
      end
    end
  end
end

transformedRecordCount = rcStage["TransformationProcessor"]
persistedRecordCount = rcStage["PersistenceProcessor"]
edfiRecordCount = rcStage["EdFiProcessor"]

jobRps = transformedRecordCount / totalJobTime.round()
puts "Total job rps: #{jobRps}"

puts "Edfi record #{edfiRecordCount}"

wallClockForPits = (jobProcessingEndTime-pitProcessingStartTime)
combinedProcessingTime = (maestroProcessingTime + pitProcessingTime)/1000
totalPitProcessingTime = pitProcessingTime/1000

puts "---------------------------"
puts "Total records for Transformation: #{transformedRecordCount}"
puts "Total records for Persistence: #{persistedRecordCount}"
puts "Total pit wall-clock time: #{wallClockForPits}sec"

puts ""
puts "Combined processing time on all nodes: #{combinedProcessingTime} sec"
puts "Total PIT processing time across nodes: #{totalPitProcessingTime} sec"

puts ""
puts "\e[4mTime spent waiting on Mongo operations:\e[0m"

puts ""
printf "\e[32m%-65s\e[0m \e[31m%11s\e[0m \e[35m%11s\e[0m \e[34m%11s\e[0m\n","Name","Calls","Time", "AVG"
puts "------------------------------------------------------------------------------------------------------"
printStats(dbs)
printStats(functions)
printStats(collections)

totalMongoTime=0;
dbs.each_value{|time| totalMongoTime+=time["time"]}

puts "Combined Mongo Calls: \e[35m#{totalMongoTime} ms (#{(totalMongoTime/60000.0).round(2)} min)    \e[0m"
puts "Mongo time as % of total time: \e[35m#{((totalMongoTime/1000.0/combinedProcessingTime)*100).round()}%\e[0m"
printf "Mongo Time per node: \e[35m%d\e[0m mins (nodes: \e[35m%d\e[0m)\n",(totalMongoTime/60000.0).round(2)/(job['executionStats'].size),job['executionStats'].size-1
printf "Average times (read/write): \e[35m%.2f/%.2f\e[0m\n",readTime.to_f/readCount,writeTime.to_f/writeCount
printf "Total sli counts (read/write): \e[35m%d/%d\e[0m  ratio: \e[35m%.2f\e[0m\n",readCount,writeCount,readCount.to_f/writeCount
printf "Total sli times(read/write): \e[35m%d/%d\e[0m ratio: \e[35m%.2f\e[0m\n",readTime,writeTime,readTime.to_f/writeTime

puts ""
puts "Job started: #{jobStart.getlocal}"
if ! jobEnd.nil?
  puts "Job ended: #{jobEnd.getlocal}"
end

pitRPS = (transformedRecordCount / wallClockForPits )

puts "PIT RPS (transformed / pit wall-clock)  \e[35m#{pitRPS}\e[0m"
puts "PIT RPS (persistence / pit wall-clock)  \e[35m#{(persistedRecordCount / wallClockForPits )}\e[0m"

if  !totalJobTime.nil?
  puts "Edfi / job time RPS \e[35m#{edfiRecordCount / totalJobTime.round()}\e[0m"
  puts "Transformed / job time RPS \e[35m#{transformedRecordCount / totalJobTime.round()}\e[0m"
  puts "Total Job time #{totalJobTime} sec"
end

dataSet = id.slice(0, id.index("-"))
puts "PIT #{pitRPS} Job: #{jobRps}  Jobtime: #{(totalJobTime/60).round()} minutes Dataset: #{dataSet}"
puts "ALL DONE"

