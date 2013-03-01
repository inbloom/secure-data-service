require 'rubygems'
require 'mongo'
require 'logger'
require 'digest/sha1'

def get_sha1_hash(entityType, tenantId, stateOrganizationId)
    key = "#{entityType}|~#{tenantId}|~#{stateOrganizationId}|~"
    Digest::SHA1.hexdigest key    
end

if ARGV.count < 1
  puts "Usage: <dbhost:port> <database>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the database (Defaults to sli)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
end

database = (ARGV[1].nil? ? 'sli' : ARGV[1])
hp = ARGV[0].split(":")
connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})

#database = 'sli'
#connection = Mongo::Connection.new('127.0.0.1', 27017, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})

@db = connection[database]
@log = Logger.new(STDOUT)

#update the realm unique identifier
@db[:realm].update({'body.uniqueIdentifier' => 'Shared Learning Infrastructure'}, '$set' => {'body.uniqueIdentifier' => 'Shared Learning Collaborative'})

@db[:application].find({}).each { |app_auth|
  @log.info "starting migration application #{app_auth['_id']} #{app_auth['body']['name']}"
  ed_orgs = app_auth['body']['authorized_ed_orgs']

  unless ed_orgs.nil?
      new_ed_orgs = Array.new
  
      ed_orgs.each do |ed_org_id|      
          unless ed_org_id.nil?
              ed_org = @db[:educationOrganization].find_one({"_id" => ed_org_id})
              
              unless ed_org.nil?
                stateOrganizationId = ed_org['body']['stateOrganizationId']
                tenantId = ed_org['metaData']['tenantId']
                entityType = 'educationOrganization'
                sha1_hash = get_sha1_hash(entityType, tenantId, stateOrganizationId)
        
                @log.info "keys: #{entityType}, #{tenantId}, #{stateOrganizationId}"
                new_ed_orgs.push(sha1_hash + "_id")
                @log.info "converted : #{ed_org_id} -> #{sha1_hash}_id"
            end
          end                 
      end

      unless new_ed_orgs.empty?
          @db[:application].update({'_id' => app_auth['_id']}, '$set' => {'body.authorized_ed_orgs' => new_ed_orgs})
      end
  end
  
  @log.info "migrated application #{app_auth['_id']} #{app_auth['body']['name']}"
}

@db[:applicationAuthorization].find({}).each { |auth|
    @log.info "starting migration applicationAuthorization #{auth['_id']}"
    auth_id = auth['body']['authId']

    unless auth_id.nil?
        ed_org = @db[:educationOrganization].find_one({"_id" => auth_id})

        unless ed_org.nil?
            stateOrganizationId = ed_org['body']['stateOrganizationId']
            tenantId = ed_org['metaData']['tenantId']
            entityType = 'educationOrganization'
            @log.info "keys: #{entityType}, #{tenantId}, #{stateOrganizationId}"

            sha1_hash = get_sha1_hash(entityType, tenantId, stateOrganizationId)
            new_id = "#{sha1_hash}_id"

            @db[:applicationAuthorization].update({'_id' => auth['_id']}, '$set' => {'body.authId' => new_id})
            @log.info "converted : #{auth_id} -> #{new_id}"
        end
    end

    @log.info "migrated applicationAuthorization #{auth['_id']}"
}

@log.info "Finished migration."
