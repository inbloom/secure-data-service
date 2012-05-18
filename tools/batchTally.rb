require 'mongo'
require 'json'

connection = Mongo::Connection.new("localhost", 27018)
db = connection.db("ingestion_batch_job")
coll = db.collection("newBatchJob")

rec=coll.find("_id"=>"MainControlFile.ctl-15745594-ce18-4a90-9cb9-7cc147e6337d")

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
  puts "[#{rc[key]}] #{key} => #{value}ms"
  sum+=value
end

puts "Tolal time: #{sum} ms"
puts "ALL DONE"