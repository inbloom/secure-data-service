require 'mongo'
require 'optparse'
require 'securerandom'
require 'digest/sha1'
require 'date'
#Hold the command line options
options = {}

ARGV.options do |opts|
  opts.banner = "Usage: generator -t Tenant -u User -c ClientId -r Role -e expiration [options]"
  options[:mongo] = 'localhost:27017'
  options[:realm] = "Shared Learning Collaborative"
  opts.on(:OPTIONAL, /.+/, '-m', '--mongo','The host and port for mongo (Default: localhost:27017)') do |mongo|
    options[:mongo] = mongo
  end
  opts.on(:REQUIRED, /.+/, '-R', '--realm','The realm unique name (Default: Shared Learning Collaborative)' ) do |realm|
    options[:realm] = realm
  end
  opts.on(:REQUIRED,/\d+/, '-e', '--expire','The number of seconds that this session will expire in' ) do |expire|
    # Convert to milis
    options[:expire] = expire.to_i * 1000
  end
  opts.on(:REQUIRED, /.+/, '-c', '--client_id', 'The client_id of the application to generate a token for') do |app|
    options[:application] = app
  end
  opts.on(:REQUIRED, '-r', '--role', 'The role you want for this user to have') do |roles|
    options[:roles] = [roles]
  end
  opts.on(:REQUIRED, /.+/, '-u', '--user', 'The staff unique state id of the user to generate a token for for') do |user|
    options[:user] = user
  end
  opts.on(:REQUIRED, /.+/, '-t', '--tenant', "The users's tenant") do |tenant|
    options[:tenant] = tenant
  end
  
  opts.on_tail(:NONE,'-h', '--help', "Display this screen.") do |tenant|
    puts opts
    exit
  end
  begin 
    opts.parse!
    unless options.include? :tenant and options.include? :user and options.include? :application and options.include? :roles and options.include? :expire
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
staffDb = db.db(hashed,:pl=>PKFactory.new)
db = db.db('sli', :pk => PKFactory.new)

#Get the realm
realm = db[:realm].find_one({'body.uniqueIdentifier'=>options[:realm]})
abort "Unable to locate #{options[:realm]} realm" if realm.nil?
#Get the app
app = db['application'].find_one({'body.client_id' => options[:application]})
abort "Unable to locate Application: #{options[:application]}" if app.nil?
#Get the user
user = staffDb[:staff].find_one({"body.staffUniqueStateId" => options[:user]})
abort "Unable to locate user: #{options[:user]}" if user.nil?

#Create a userSession
userSession = {}
body = {}
metaData = {}
principal = {}
appSessions = []
appSession = {}
principal = {}
# Lets make it expire in 1 day
expire = options[:expire].to_i
body[:expiration] = Time.now.to_i * 1000 + expire
body[:hardLogout] = body[:expiration]

appSession[:token] = "t-" << SecureRandom.uuid
appSession[:verified] = true
appSession[:state] = ""
appSession[:clientId] = app["body"]["client_id"]
code = {}
code[:value] = SecureRandom.uuid
code[:expiration] = Time.now.to_i
appSession[:code] = code
appSessions.push appSession
#Principal
principal[:realm] = realm['_id']
principal[:roles] = options[:roles]
principal[:tenantId] = options[:tenant]
principal[:name] = user['body']['name']['firstName'] + " " + user['body']['name']['lastSurname']
principal[:externalId] = user['body']['staffUniqueStateId']
principal[:id] = principal[:externalId] + "@" + principal[:tenantId]
body[:principal] = principal
body[:appSession] = appSessions
userSession[:body] = body
userSession[:metaData] = {}
userSession[:metaData][:created]=Date.today.strftime("%Y-%m-%d")
db[:userSession].insert(userSession)

puts "Your new long-lived session token is #{appSession[:token]}"
