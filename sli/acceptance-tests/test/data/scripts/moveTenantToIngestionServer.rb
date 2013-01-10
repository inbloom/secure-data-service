require 'rubygems'
require 'mongo'
require 'fileutils'

if __FILE__ == $0
  unless ARGV.length == 2
      puts "Usage: prompt>ruby " + $0 + " tenantId ingestionServerToAssign"
      puts "Example: ruby #{$0} NY ingestionServer01"
      exit(1)
  end

  tenantId = ARGV[0]
  ingestionServerToAssign = ARGV[1]
end

conn = Mongo::Connection.new('localhost')
db = conn.db('sli')
tenantColl = db.collection('tenant')

tenantToFix = tenantColl.find("body.tenantId" => tenantId)

if tenantToFix == nil or tenantToFix.count == 0
  puts "Tenant #{tenantId} does not exist in the tenant collection in mongo"
  exit(1)
end

if tenantToFix.count != 1
  puts "Tenant #{tenantId} is assigned to 2 different documents, this is bad"
  exit(1)
end

tenantToFix.each do |row|
  row["body"]["landingZone"].each_with_index do |lz, i|
    tenantColl.update({"_id" => row['_id']}, {"$set" => {"body.landingZone.#{i}.ingestionServer" => ingestionServerToAssign}})
  end
end
