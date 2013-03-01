require 'json'
require 'digest/sha1'
require 'mongo'

#Hold the command line options
options = {}

if ARGV.size < 1
    puts "Usage: #{__FILE__} mongo(Defaults: localhost:27017)"
end
@@options = {}
@@options[:mongo] = ARGV[0] || 'localhost:27017'
@@options[:tenant] = ARGV[2]
@@options[:keys] = [:beginDate, :staffReference, :educationOrganizationReference, :staffClassification]
class PKFactory
    def create_pk(row)
        return row if row[:_id]
        row.delete(:_id)      # in case it exists but the value is nil
        row['_id'] ||= cerate_natural_key(row)
        row
    end
    private
    def cerate_natural_key(entity, keys = nil)
        real_keys = keys || @@options[:keys]
        entity.delete(:_id)      # in case it exists but the value is nil
        hash = ""
        delimiter = "|~"
        real_keys.sort!
        #First we do entity type and tenant
        hash << entity[:metaData][:type] << delimiter << @@options[:tenant] << delimiter
        real_keys.each do |nk|
            hash << entity[:body][nk] << delimiter
        end
        id = Digest::SHA1.hexdigest hash
        id += "_id"
        id
    end
end

def build_sea(tsa)
    body = {:beginDate => '2007-07-07', :staffReference => tsa['body']['teacherId'], 
        :educationOrganizationReference => tsa['body']['schoolId'], :staffClassification => 'Teacher'
    }
    metaData = {:type => "staffEducationOrganizationAssociation"}
    sea = {"_id" => nil, :body => body, :metaData => metaData}
    sea
end
puts "Establishing connection to SLI database..."
hp = @@options[:mongo].split(':')
@connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
sli = @connection['sli']
tenants = {}
sli['tenant'].find({}).each do |tenant|
    tenants[tenant['body']['tenantId']] = tenant['body']['dbName']
end
puts "We found #{tenants.keys.join(', ')} tenants to fix..."
tenants.each do |key, value|
    puts "Beginning to work on #{key}"
    tenant = @connection.db(value, :pk => PKFactory.new)
    @@options[:tenant] = key
    count = 0
    tenant['teacherSchoolAssociation'].find({}).each do |tsa|
        sea = build_sea tsa
        begin
            tenant['staffEducationOrganizationAssociation'].insert(sea)
            count += 1
        rescue Mongo::OperationFailure => e
            puts "Entity appears to exist already, skipping to the next one..."
        end
    end
    puts "We denormalized #{count} teacherSchoolAssociations in #{key}"
end
