require 'mongo'
require 'time'
require 'uuid'


def iterate_documents(db, collection_names)
    collection_names.each do |col_name| 
        puts "Reading #{col_name}"
        col = db[col_name]
        col.find.each do |doc|
            yield col_name, doc 
        end 
    end 
end 

if __FILE__ == $0
    if ARGV.length != 4
        puts "Usage: " + $0 + " TBD"
        exit(1)
    end

    src_db       = ARGV[0]
    tgt_db       = ARGV[1]
    tgt_tenantid = ARGV[2]
    tgt_edorg   = ARGV[3]
    connection = Mongo::Connection.new
    collection_names = ["parent"]
    collection_names = connection.db(src_db).collection_names
    tgt_db = connection.db(tgt_db)
    total_write = 0
    total_start = Time.now
    iterate_documents(connection.db(src_db), collection_names) do |collname, doc|

        # fix the document 

        # write it to the collection 
        start_write = Time.now
        tgt_col = tgt_db[collname]
        tgt_col.insert(doc)
        end_write = Time.now 
        total_write += (end_write - start_write) * 1000
        # done writing 
    end 
    total_end = Time.now 
    total = (total_end - total_start) * 1000
    puts "Total time: #{total}"
    puts "Write time: #{total_write}"
end




