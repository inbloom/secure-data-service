require 'mongo'
require 'optparse'
require 'SecureRandom'

#Hold the command line options
options = {}

ARGV.options do |opts|
  opts.banner = "Usage: generator -t Tenant -u User -a Application [options]"
  options[:roles] = ['Educator']
  options[:mongo] = 'localhost:27017'
  options[:realm] = "Sandbox Realm"
  options[:expire] = 86400
  opts.on(:OPTIONAL, /.+/, '-m', '--mongo','The host and port for mongo (localhost:27017)') do |mongo|
    options[:mongo] = mongo
  end
  opts.on(:REQUIRED, /.+/, '-R', '--realm','The realm name (The Sandbox Realm)' ) do |realm|
    options[:realm] = realm
  end
  opts.on(:REQUIRED,/\d+/, '-e', '--expire','The number of seconds that this session will expire in (86400 [1 day])' ) do |expire|
    # Convert to milis
    options[:expire] = expire * 1000
  end
  opts.on(:REQUIRED, /.+/, '-a', '--application', 'The name of the application to generate a token for (Databrowser)') do |app|
    options[:application] = app
  end
  opts.on(:REQUIRED, ["Educator", "Leader", "IT Administrator"], Array, '-r', '--roles', 'The roles you want for this user to have') do |roles|
    options[:roles] = roles
  end
  opts.on(:REQUIRED, /.+/, '-u', '--user', 'The first and last name of the user to generate a token for for (Linda Kim)') do |user|
    options[:user] = user
  end
  opts.on(:REQUIRED, /.+/, '-t', '--tenant', "The users's tenant (email address)") do |tenant|
    options[:tenant] = tenant
  end
  
  opts.on_tail(:NONE,'-h', '--help', "Display this screen.") do |tenant|
    puts opts
    exit
  end
  begin 
    opts.parse!
    unless options.include? :tenant and options.include? :user and options.include? :application and options.include? :user
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
hp = options[:mongo].split(':')
db = Mongo::Connection.new(hp[0], hp[1])
db = db.db('sli', :pk => PKFactory.new)

#Get the realm
realm = db[:realm].find_one({'body.name'=>options[:realm]})
abort "Unable to locate #{options[:realm]} realm" if realm.nil?
#Get the app
app = db['application'].find_one({'body.name' => options[:application]})
abort "Unable to locate Application: #{options[:application]}" if app.nil?
#Get the user
parts = options[:user].split(' ')
user = db[:staff].find_one({"metaData.tenantId" => options[:tenant], "body.name.firstName" => parts[0], "body.name.lastSurname" => parts[1]})
abort "Unable to locate user: #{options[:user]}" if app.nil?

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
principal[:externalId] = user['metaData']['externalId']
principal[:id] = principal[:externalId] + "@" + principal[:tenantId]
body[:principal] = principal
body[:appSession] = appSessions
userSession[:body] = body
userSession[:metaData] = {}
db[:userSession].insert(userSession)

puts "Your new long-lived session token is #{appSession[:token]}"
