require 'mongo'
require 'optparse'
require 'securerandom'
require 'digest/sha1'
require 'date'
#Hold the command line options
options = {}

# These options are only used for automated tests.  It's not intended for use by the operator.
if ARGV.include? "--student" and ARGV.include? "--parent"
  raise "Can't specify both student and parent at the same time."
end
user_type = :staff
user_type = :student if ARGV.include? "--student"
ARGV.delete "--student" if ARGV.include? "--student"
user_type = :parent if ARGV.include? "--parent"
ARGV.delete "--parent" if ARGV.include? "--parent"

ARGV.options do |opts|
  opts.banner = "Usage: generator -t Tenant -u User -c ClientId -r Role -e expiration [options]"
  options[:mongo] = 'localhost:27017'
  options[:realm] = "Shared Learning Collaborative"
  opts.on(:OPTIONAL, /.+/, '-m', '--mongo','The host and port for mongo (Default: localhost:27017)') do |mongo|
    options[:mongo] = mongo
  end
    options[:edorg] = nil
    opts.on(:REQUIRED, /.+/, '-E', '--edorg','The education organization this role belongs to' ) do |edorg|
    options[:edorg] = edorg
  end
    options[:newSession] = false
    opts.on(:REQUIRED, /.+/, '-n', '--newSession','If true, create a new userSession (Default : false)' ) do |newSession|
    options[:newSession] = (newSession.downcase == 'true')
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

def createUserSession(options, realm, user, edorg, app, user_type, db)
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
  if user_type == :student
    principal[:externalId] = user['body']['studentUniqueStateId']
    principal[:userType] = 'student'
  elsif user_type == :parent
    principal[:externalId] = user['body']['parentUniqueStateId']
    principal[:userType] = 'parent'
  else
    principal[:externalId] = user['body']['staffUniqueStateId']
  end
  principal[:id] = principal[:externalId] + "@" + principal[:tenantId]
  
  if(edorg != nil)
    edorgRoles = {}
    edorgRoles[edorg['_id']] = options[:roles]
    principal[:edOrgRoles] = edorgRoles
  end
  body[:principal] = principal
  body[:appSession] = appSessions
  userSession[:body] = body
  userSession[:metaData] = {}
  userSession[:metaData][:created]=Date.today.strftime("%Y-%m-%d")
  db[:userSession].insert(userSession)
  return appSession
end

def updateUserSession(options, edorg, userSession, db)
  edOrgRoles = userSession['body']['principal']['edOrgRoles']

  #if edorg is specify in the input,
  #unionize the roles if the edorg already has some roles.
  #if the edorg is new, add the input roles to it.
  if(edorg != nil) 
    edorgId = edorg['_id']
    if(edOrgRoles.include?edorgId)
      edOrgRoles[edorgId] = edOrgRoles[edorgId] | options[:roles]
    else
      edOrgRoles[edorgId] = options[:roles]
    end
  end

  #unionize the roles existing in the principal
  newRoles = userSession['body']['principal']['roles'] | options[:roles]
  appSession = userSession['body']['appSession']


  expire = Time.now.to_i * 1000 + options[:expire].to_i
  db[:userSession].update({'_id' => userSession['_id']}, {'$set' => {'body.principal.roles' => newRoles, 
    'body.principal.edOrgRoles' => edOrgRoles,"body.expiration" => expire, "body.hardLogout" => expire, "body.appSession.0.code.expiration" => Time.now.to_i}})


  return appSession[0]['token']
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
if user_type == :student
  user = staffDb[:student].find_one({'body.studentUniqueStateId' => options[:user]})
elsif user_type == :parent
  user = staffDb[:parent].find_one({'body.parentUniqueStateId' => options[:user]})
else
  user = staffDb[:staff].find_one({"body.staffUniqueStateId" => options[:user]})
end
abort "Unable to locate user: #{options[:user]}" if user.nil?

#Get the edorg
edorg = nil
if(options[:edorg] != nil)
  edorg = staffDb[:educationOrganization].find_one({"body.stateOrganizationId" => options[:edorg]})
  abort "Unable to locate edorg: #{options[:edorg]}" if edorg.nil?
end

query = {}
query['body.principal.realm'] = realm['_id']
query['body.principal.tenantId'] = options[:tenant]
query['body.appSession.clientId'] = app["body"]["client_id"]
if user_type == :student
  externalId = user['body']['studentUniqueStateId']
  query['userType'] = 'student'
elsif user_type == :parent
  externalId = user['body']['parentUniqueStateId']
  query['userType'] = 'parent'
else
  externalId = user['body']['staffUniqueStateId']
end
query['body.principal.externalId'] = externalId

userSession = db[:userSession].find_one(query)
if(userSession != nil && !options[:newSession])
  token = updateUserSession(options, edorg, userSession, db)
else
  appSession = createUserSession(options, realm, user, edorg, app, user_type, db)
  token = appSession[:token]
end
  
puts "Your new long-lived session token is #{token}"
