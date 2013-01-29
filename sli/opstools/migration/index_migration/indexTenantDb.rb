require 'mongo'

def indexTenantDb(host,dbName)
  
  dbConn = Mongo::Connection.new(host)
  db = dbConn.db(dbName)

  indexCount = 0;
  file = File.new(TENANTDB_INDEX_FILE, "r")
    while (line = file.gets)

      tokens = line.chomp.split(',')

      if(line.chr != '#' && tokens.size >= 3)
        collectionName =  tokens[0]

        #convert string to boolean
        if(tokens[1] == "true")
          unique = true
        elsif(tokens[1] == "false")
          unique = false
        end

        keys = Array.new
        for itr in 2 ... tokens.size
          key = Array.new
          key[0] = tokens[itr].split(':').first
          key[1] = tokens[itr].split(':').last.to_i
          keys[itr-2] = key
        end

        indexCount = indexCount + 1
        applyIndex(db,collectionName,unique,keys, indexCount)

      else
        # "Comment or Invalid Line"
      end
    end
    file.close

rescue => err
    puts "Exception: #{err}"
    err
end


def applyIndex(db,collectionName,unique,keys, indexCount)
  collection = db[collectionName]
  collection.create_index(keys, :unique => unique, :name => "idx"+indexCount.to_s)
end

if __FILE__ == $0
  unless ARGV.length == 2
    puts "Usage: " + $0 + " <host> <database name>"
    exit(1)
  end
TENANTDB_INDEX_FILE = File.expand_path("../tenantDB_indexes.txt", __FILE__)
indexTenantDb(ARGV[0], ARGV[1])
end
