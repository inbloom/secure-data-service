require 'rubygems'
require 'mongo'

def countEach
  db = getDatabase

  totalCount = 0
  db.collection_names.sort.each do |collectionName|
    if (!collectionName.start_with?('system.'))
      coll = db.collection(collectionName)
      count = coll.count
      totalCount += count
      puts "#{collectionName}: #{count}"
    end
  end

  puts "Total records: #{totalCount}"
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

