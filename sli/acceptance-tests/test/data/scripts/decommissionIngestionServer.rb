require 'rubygems'
require 'mongo'
require 'fileutils'

$server_index=0

if __FILE__ == $0
  unless ARGV.length == 2
      puts "Usage: prompt>ruby " + $0 + " ingestionServerToRemove commaSeperatedListOfAvailableIngestionServers "
      puts "Example: ruby #{$0} ingestServer1 ingestServer2,ingestServer3,ingestServer4"
      exit(1)
  end

  ingestionServerToRemove = ARGV[0]
  $goodServerList = ARGV[1].split(',')
  $serverListSize=$goodServerList.size
end

def getNextServername
  #$server_index = $server_index == @serverListSize ? $server_index = 0 : $server_index+1
  retval = $goodServerList[$server_index]
  $server_index = ($server_index + 1) % $serverListSize
  return retval
end


conn = Mongo::Connection.new('localhost')
db = conn.db('sli')
tenantColl = db.collection('tenant')

tenantsToFix = tenantColl.find("body.landingZone.ingestionServer" => ingestionServerToRemove)

if tenantsToFix == nil or tenantsToFix.count == 0
  puts "No landing zones are assigned ingestion server: #{ingestionServerToRemove}"
  exit(1)
end

tenantsToFix.each do |row|
  row["body"]["landingZone"].each_with_index do |lz, i|
    if lz['ingestionServer'] == ingestionServerToRemove
      tenantColl.update({"_id" => row['_id']}, {"$set" => {"body.landingZone.#{i}.ingestionServer" => getNextServername}})
    end
  end
end







