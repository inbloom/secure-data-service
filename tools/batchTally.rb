require 'mongo'
require 'json'

connection = Mongo::Connection.new("nxmongo.slidev.org", 27017)
db = connection.db("ingestion_batch_job")
coll = db.collection("newBatchJob")

rec=coll.find("_id"=>"1x1_run1-4b37b599-9e5c-4619-b9df-4dfb2a9f9c24")

job = rec.to_a[0]

#times elapsed
out = {}

# Record Counts
rc = {}
job["stages"].each do |stage|
  if stage["stageName"] == "TransformationProcessor" or stage["stageName"] == "PersistenceProcessor"
    stage["chunks"].each do |chunk|
      chunk["metrics"].each do |metric|

        out[metric["resourceId"]]=0 unless out[metric["resourceId"]]
        out[metric["resourceId"]]+=chunk["elapsedTime"] unless chunk.nil?

        rc[metric["resourceId"]]=0 unless rc[metric["resourceId"]]
        rc[metric["resourceId"]]+=metric["recordCount"] unless metric.nil? or metric["recordCount"].nil?
      end
    end
  end
end

sum=0
out.each do |key,value|
  rps = "N/A"
  rps = rc[key] / (value / 1000.0) unless value == 0
  puts "[#{rc[key]}] #{key} => #{value}ms (#{rps} rps)"
  sum+=value
end

puts "Tolal time: #{sum} ms"
puts "ALL DONE"
