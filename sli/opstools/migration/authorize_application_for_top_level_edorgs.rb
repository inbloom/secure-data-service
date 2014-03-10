#
# DE3043
#
# This operation will replace/overwrite the existing application's authorized edorgs to be the set of top level edorgs.
# Top level edorgs are defined, per tenant, as the (single) SEA together with its immediate child edOrgs, regardless of
# their type for a specified bulk extract application.
#
# Run this script with no arguments to see usage:
#
#   ruby authorize_application_for_top_level_edorgs.rb
#
# You can give one or more tenant IDs whose databases will be migrated.
# Also you can optionally give the MongoDB host/port.
#
# Example: authorize_application_for_top_level_edorgs.rb 2013qu-69c75b20-673f-11e3-ba76-02445bc9997c bsuzuki@wgen.net Midgar Hyrule myhost.com:12345
#

require 'mongo'
require 'securerandom'


DEBUG = true
APPAUTH_COLLECTION = "applicationAuthorization"
EDORG_COLLECTION = "educationOrganization"

def usage(params, tenant2db)
    puts "--------------------------------------------------------------------------------------------"
    puts "| Tenant databases on server '" + params["mongo_host"] + "':"
    tenant2db.each_pair do |tname, dbname|
      puts "|    " + tname + " " + dbname
    end
    puts "--------------------------------------------------------------------------------------------"
    puts ""
    puts "Usage : #{$0} <app_client_id> <auth_user>  [<host>:<port>] <tenant1> [<tenant2> ...]"
    puts ""
    puts "--------------------------------------------------------------------------------------------"
    puts "| Authorizes only top level edorgs for the specified bulk extract application, give argument(s) as follows:"
    puts "|"
    puts "|    <app_client_id>                           the Application's client id"
    puts "|"
    puts "|    <tenant1> [<tenant2> ...]                 Migrate only database(s) for the given tenant(s)"
    puts "|"
    puts "|    <host>:<port>                             Optional hostname and port defaults to localhost:27017"
    puts "--------------------------------------------------------------------------------------------"
    puts ""
    puts "Example:"
    puts "   #{$0} zi0azh7ec8 me@inbloom.org Midgar Hyrule myhost.com:12345"
    puts ""
    puts "Note:"
    puts "   This script does not support tenant names with ':' characters in them."
    exit
end

def parseArgs(argv)
  result = { "mongo_host" => "localhost", "mongo_port" => 27017, "tenants" => [], "auth_user" => nil }

  result["app_client_id"] = argv.shift

  for arg in argv
    if arg.include?(":")
      host_port = arg.split(':', 2)
      result["mongo_host"] = host_port[0]
      result["mongo_port"] = host_port[1]
    else
      result["tenants"] << arg
    end
  end
  return result
end

def getSeas(dbname)
  sea_ids = []

  @conn[dbname][EDORG_COLLECTION].find({"body.organizationCategories" => "State Education Agency"}).each do |edorg|
    id = edorg["_id"]
    sea_ids.push(id) if ! id.nil?
  end

  return sea_ids
end

def getTopLevelEdorgs(dbname)
  ids = []

  # Get the top level edorgs
  sea_ids = getSeas(dbname)
  puts "   " + "The sea ids: #{sea_ids}" if DEBUG == true

  # inBloom platform currently only supports a single SEA per tenant
  if sea_ids.length == 1
    sea_id = sea_ids[0]
    #ids.push(sea_id)   #don't auth for SEA
    @conn[dbname][EDORG_COLLECTION].find({"body.parentEducationAgencyReference" => sea_id}).each do |edorg|
      id = edorg["_id"]
      ids.push(id) if ! id.nil?
    end
  else
    $stderr.puts "   " + "The inBloom platform currently only supports a single SEA per tenant."
    $stderr.puts "   " + "#{sea_ids.length.to_s} were found -- skipping db #{dbname}"
  end

  return ids
end

def updateAppAuth(dbName, app_id, edorg_ids)
  edorgs_new = []
  edorg_ids.each do |edorg_id|
    edorg_entry = {}
    edorg_entry["authorizedEdorg"] = edorg_id
    edorg_entry["lastAuthorizedDate"] = Time.now.to_i
    edorgs_new.push(edorg_entry)
  end

  if(@conn[dbName][APPAUTH_COLLECTION].find_one({"body.applicationId"=>app_id}).nil?)
    #add new app auth record

    puts "Could not find an existing applicationAuthorization, so I'm creating a new one"
    app_auth_body = {"applicationId" => app_id, "edorgs" => edorgs_new}
    app_auth = {"_id" => SecureRandom.uuid, "type" => "applicationAuthorization", "body" => app_auth_body}
    @conn[dbName][APPAUTH_COLLECTION].save(app_auth)

  else
    #update app auth record
    results = @conn[dbName][APPAUTH_COLLECTION].update({"body.applicationId"=>app_id}, { "$set" => {"body.edorgs"=>edorgs_new}}, {"multi"=>true})
    if results["err"].nil?
      puts "   " + "Updated #{results["n"]} application authorization documents"
    else
      $stderr.puts "   " + "Update failed with the following error : "
      $stderr.puts results["err"].to_s
    end
  end
end

def updateDB(dbname, app_id)
  # Get all edorgs with the SEA as parent
  top_level_edorg_ids = getTopLevelEdorgs(dbname)
  puts "   " + "Top level edorgs: #{top_level_edorg_ids}" if DEBUG == true

  # update the app auth info
  updateAppAuth(dbname, app_id, top_level_edorg_ids)
end

# Main driver
def main(argv)
  params = parseArgs(argv)

  tenants = params["tenants"]
  @conn = Mongo::Connection.new(params["mongo_host"], params["mongo_port"])

  # Map tenant name to database by getting records in sli.tenant collection
  tenant2db = {}
  @conn['sli']['tenant'].find({}).each do |tenant|
    name = tenant['body']['tenantId']
    db = tenant['body']['dbName']
    tenant2db[name] = db
  end

  # Make sure any tenants given explicitly actually exist
  for tname in tenants
    if not tenant2db.has_key?(tname)
      puts "No such tenant: #{tname}"
      return
    end
  end

  # Make sure that either a list of tenants was given
  if ( 0==tenants.length )
    usage(params, tenant2db)
  end

  # look up app id
  app = @conn['sli']['application'].find_one({"body.client_id" => params["app_client_id"]})
  if app.nil?
    puts "Could not find app with clientId: #{params["app_client_id"]}"
    usage(params, tenant2db);
  end

  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Updating authorization for application " + params['app_client_id'] + " in tenant: " + tenant + ", database " + dbname
    updateDB(dbname, app["_id"])
    puts "Completed update of tenant: " + tenant + ", database " + dbname + "\n"
  end

  puts "All done."
end

# Run it
main(ARGV)
