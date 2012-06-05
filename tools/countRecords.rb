require 'rubygems'
require 'mongo'

def countRecords
  db = getDatabase

  count = 0
  db.collection_names.each do |collectionName|
    if (!collectionName.start_with?('system.'))
      coll = db.collection(collectionName)
      count += coll.count
    end
  end

  puts "Total records: #{count}"
end

def countEach
  db = getDatabase

  db.collection_names.sort.each do |collectionName|
    if (!collectionName.start_with?('system.'))
      coll = db.collection(collectionName)
      count = coll.count
      puts "#{collectionName}: #{count}"
    end
  end
end

def count(collectionName)
  db = getDatabase

  coll = db.collection(collectionName)
  count = coll.count
  puts "#{collectionName}: #{count}"
end

def getDatabase
  host = ARGV[0]
  if (host.nil?)
    host = 'nxmongo3.slidev.org'
  end

  database = ARGV[1]
  if (database.nil?)
    database = 'sli'
  end

  conn = Mongo::Connection.new(host)
  return conn.db(database)
end


countEach

countRecords

#count('section')

#count('student')

