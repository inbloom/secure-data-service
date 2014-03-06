#This script can be used to give an application access to all edorgs in a tenant. 
#It takes a tenant and client Id and creates an entry in the applicationAuthorization collection for the given
#client Id and puts all the edorgs in the tenant in the edorg array.
#Effectively this script can be used to bypass all LEA level application authorization security so should be used only
#it circumstances where the application should have access to all data in the tenant.

require 'mongo'
require 'optparse'
require 'securerandom'
require 'digest/sha1'

#Hold the command line options
options = {}

ARGV.options do |opts|
  opts.banner = "Usage: apphack -m localhost:27017 -t Tenant -c client_id [options]"
  opts.on(:REQUIRED, /.+/, '-m', '--mongo','The host and port for mongo') do |mongo|
    options[:mongo] = mongo
  end
  opts.on(:REQUIRED, /.+/, '-t', '--tenant','The tenants name' ) do |tenant|
    options[:tenant] = tenant
  end
  opts.on(:REQUIRED, /.+/, '-c', '--client_id', 'The client_id of the application to generate a token for') do |app|
    options[:application] = app
  end
  opts.on_tail(:NONE,'-h', '--help', "Display this screen.") do |tenant|
    puts opts
    exit
  end
  begin 
    opts.parse!
    unless options.include? :tenant and options.include? :mongo and options.include? :application
      throw Exception
    end
  rescue
    puts opts
    exit
  end
end

class PKFactory
  def create_pk(row)
    return row if row[:_id]
    row.delete(:_id)      # in case it exists but the value is nil
    row['_id'] ||= SecureRandom.uuid
    row
  end
end

hashed=Digest::SHA1.hexdigest options[:tenant]
hp = options[:mongo].split(':')
db = Mongo::Connection.new(hp[0], hp[1])
puts "dbname is #{hashed}"
tenantDb = db.db(hashed,:pk=>PKFactory.new)
db = db.db('sli', :pk => PKFactory.new)

app = db['application'].find_one({'body.client_id' => options[:application]})
abort "Unable to locate Application: #{options[:application]}" if app.nil?
application_id = app['_id']


#attempt to delete an older ApplicationAuthorization for the same app
if not tenantDb[:applicationAuthorization].find_one({'body.applicationId' => application_id}).nil? then
  puts "Removing old ApplicationAuthorization record for #{options[:application]}"
  tenantDb[:applicationAuthorization].remove({'body.applicationId' => application_id})
end


edorgs = tenantDb['educationOrganization'].distinct('_id')

#build the list of authorized edorgs like [{"authorizedEdorg" : 1234567890}, ...]
authorizedEdOrgs = []
for edOrgId in edorgs
  authorizedEdOrgs.push({"authorizedEdorg" => edOrgId})
end

body = {}
appAuth = {}
body[:applicationId] = application_id
body[:edorgs] = authorizedEdOrgs
appAuth[:type] = "applicationAuthorization"
appAuth[:body] = body
tenantDb[:applicationAuthorization].insert(appAuth)

puts "Done. Application with ClientId #{options[:application]} is now enabled for all EdOrgs in tenant #{options[:tenant]}"
