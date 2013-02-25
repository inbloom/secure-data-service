require 'mongo'


$index_70_adds = {
  "applicationAuthorization" => [
    ["body.applicationId", Mongo::ASCENDING],
    ["body.edorgs", Mongo::ASCENDING]
    ],
  "courseTranscript" => [
    ["body.educationOrganizationReference", Mongo::ASCENDING],
    ],
  "gradingPeriod" => [
    ["body.gradingPeriodIdentity.schoolId", Mongo::ASCENDING],
    ],
}


def get_tenant_names(host, port)
  tenant_names = []

  client = Mongo::MongoClient.new(host, port)

  tenants = client.db('sli').collection('tenant').find({}, {:fields => {"body.dbName" => 1}})
  tenants.each { |t|
    tenant_names << t["body"]["dbName"]
  }

  client.close()

  tenant_names
end


def add_indexes(host, port, tenant_name)
  client = Mongo::MongoClient.new(host, port)

  num_added = 1
  $index_70_adds.each  do |coll, coll_idxs|
    coll_idxs.each do |idx|
      idx_name = "idx_70_#{num_added}"
      num_added = num_added + 1

      puts "Adding index: #{tenant_name}:#{coll}.#{idx_name}"
      client.db(tenant_name).collection(coll).create_index([idx], :name => idx_name)
    end
  end

  client.close()
end


if ARGV.count < 2
  puts "This script adds indexes for Release 70. It reads tenants from a mongos, and adds indexes through a mongod"
  puts "Usage: <cluster dbhost:port> <mongod dbhost:port>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  exit
end

cluster_hp = ARGV[0].split(":")
mongod_hp = ARGV[1].split(":")

tenant_names = get_tenant_names(cluster_hp[0], cluster_hp[1])

tenant_names.each do |tn|
  add_indexes(mongod_hp[0], mongod_hp[1], tn)
end
