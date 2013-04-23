require 'mongo'

connection = Mongo::MongoClient.new("localhost", 27017)
connection.database_names.each do |name|
  if name.start_with? 'm_'
    connection[name].collections.each do |collection|
      collection.remove {} if collection.name != "system.indexes"
    end
  end
end