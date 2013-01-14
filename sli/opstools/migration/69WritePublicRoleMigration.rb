require 'mongo'

if ARGV.count < 1
  puts "This script updates the customRole IT Administrator role to include WRITE_PUBLIC"
  puts "Usage: <dbhost:port>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  exit
end

hp = ARGV[0].split(":")

client = Mongo::MongoClient.new(hp[0], hp[1])

client.database_names.each do |dbName|
  if dbName.size() == 40
    puts "updating db: #{dbName}"
    col = client.db(dbName).collection("customRole")
    
    find = {"body.roles.groupTitle"=>"IT Administrator"}
    col.update(find,{"$addToSet"=>{"body.roles.$.rights"=>"WRITE_PUBLIC"}},{:multi=>true})
    
    puts "Updated: #{col.count(find)}"
  end
end