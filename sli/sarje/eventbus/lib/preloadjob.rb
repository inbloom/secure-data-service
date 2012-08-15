require 'mongo'
require 'time'
require 'uuid'
require 'set'

class PreloadJob 
    # constants for accessing the fields of a document 
    F_ID        = "_id"
    F_METADATA  = "metaData"
    F_TENANT_ID = "tenantId"
    F_EDORGS    = "edOrgs"

    COLL_EDORG = "educationOrganization" 

    IGNORE_COLLECTIONS = Set.new([
            "system.indexes",
            COLL_EDORG
            ])

    def initialize(mongodb_conn, src_db, tgt_db, tenant_id)
        @connection = mongodb_conn
        @src_db = src_db 
        @tgt_db = tgt_db
        @tenant_id = tenant_id
        @collection_names = @connection.db(@src_db).collection_names.select { |c| !IGNORE_COLLECTIONS.include?(c) }
        @uuid = UUID.new
    end 

    def preload
        # get all input collections and iterate over them 
        src_db = @connection.db(@src_db)
        tgt_db = @connection.db(@tgt_db)

        # get the mapping for edorgs and copy the edorgs collection in the process
        edorgs_map = get_edorgs_map(src_db, tgt_db)

        # iterate over all documents 
        iterate_documents(src_db, @collection_names) do |collname, doc|
            # fix the documentat and write it to the collection in the target db 
            fix_document(doc, edorgs_map)
            tgt_col = tgt_db[collname]
            tgt_col.insert(doc)
        end 
    end 

    private 
    def iterate_documents(db, collection_names)
        collection_names.each do |col_name| 
            puts "Reading #{col_name}"
            col = db[col_name]
            col.find.each do |doc|
                yield col_name, doc 
            end 
        end 
    end 

    def get_edorgs_map(src_db, tgt_db)
        edorgs_map = {}

        # get the new ids for all edorgs 
        iterate_documents(src_db, [COLL_EDORG]) do |ignore, doc| 
            new_id = @uuid.generate 
            edorgs_map[doc[F_ID]] = new_id 
        end

        # fix the edorgs and write the data 
        tgt_col = tgt_db[COLL_EDORG]
        iterate_documents(src_db, [COLL_EDORG]) do |ignore, doc| 
            # fix the document 
            doc[F_ID] = edorgs_map[doc[F_ID]]
            fix_tenant_id(doc)
            fix_edorgs(doc, edorgs_map)

            # write it to the target collection 
            tgt_col.insert(doc)
        end 

        edorgs_map 
    end 

    def fix_document(doc, edorgs_map)
        # fix the _id first and set new tenant_id and edorgs if there are existing ones 
        new_id = @uuid.generate
        doc[F_ID] = new_id 

        # fix the edorgs entry and the tenant_id
        fix_edorgs(doc, edorgs_map)
        fix_tenant_id(doc) 

        # return the new id 
        new_id 
    end 

    def fix_tenant_id(doc)
        doc[F_METADATA][F_TENANT_ID] = @tenant_id  if doc[F_METADATA][F_TENANT_ID]
    end 

    def fix_edorgs(doc, edorgs_map)
        curr_edorgs = doc[F_METADATA][F_EDORGS]
        if curr_edorgs
            doc[F_METADATA][F_EDORGS] = doc[F_METADATA][F_EDORGS].map do |e| 
                new_edorg = edorgs_map[e]
                raise "Unable to adjust edorg id in record with id: #{doc[F_ID]}" if !new_edorg
                new_edorg 
            end 
        end
    end
end

if __FILE__ == $0
    if ARGV.length != 3
        puts "Usage: " + $0 + " TBD"
        exit(1)
    end

    src_db       = ARGV[0]
    tgt_db       = ARGV[1]
    tgt_tenantid = ARGV[2]
    connection = Mongo::Connection.new

    job = PreloadJob.new(connection, src_db, tgt_db, tgt_tenantid)
    job.preload
end

