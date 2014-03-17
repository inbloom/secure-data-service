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

def usage(params, tenant2db)
    puts "--------------------------------------------------------------------------------------------"
    puts "| Tenant databases on server '" + params["mongo_host"] + "':"
    tenant2db.each_pair do |tname, dbname|
      puts "|    " + tname + " " + dbname
    end
    puts "--------------------------------------------------------------------------------------------"
    puts ""
    puts "Usage : #{$0} <app_client_id> <auth_user> [<host>:<port>] <tenant1> [<tenant2> ...]"
    puts ""
    puts "--------------------------------------------------------------------------------------------"
    puts "| Unauthorizes all edorgs for the specified application, give argument(s) as follows:"
    puts "|"
    puts "|    <app_id>                                  Client Id of the application to be modified"
    puts "|"
    puts "|    <tenant1> [<tenant2> ...]                 Migrate only database(s) for the given tenant(s)"
    puts "|"
    puts "|    <host>:<port>                             Optional hostname and port defaults to localhost:27017"
    puts "--------------------------------------------------------------------------------------------"
    puts ""
    puts "Example:"
    puts "   #{$0} zi0azh7ec8 Midgar Hyrule myhost.com:12345"
    puts ""
    puts "Note:"
    puts "   This script does not support tenant names with ':' characters in them."
    exit
end

def parseArgs(argv)
  result = { "mongo_host" => "localhost", "mongo_port" => 27017, "tenants" => [], "auth_user" => nil }

  result["app_id"] = argv.shift

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


  # Kill Application Enablements
  app = @conn['sli']['application'].find_one({'body.client_id' => params['app_id']})
  if app.nil?
    puts "Could not find app with client id #{params["app_id"]}"
    usage(params, tenant2db)
  end
  puts "Removing #{app["body"]["authorized_ed_orgs"].size} Enablements for Application #{app["body"]["name"]} (#{app["body"]["client_id"]})"
  app_id=app["_id"];
  app_body=app["body"];
  app_body.update({"authorized_ed_orgs" => []})
  @conn['sli']['application'].update({"_id" => app_id}, app)
  puts "Application has been disabled"


  # Kill Application Authorizations in each given tenant
  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Deleting ApplicationAuthorization for application #{app["body"]["name"]} (#{app["body"]["client_id"]}) in tenant: #{tenant} (#{dbname})"
    deleted = @conn[dbname]['applicationAuthorization'].remove({"body.applicationId" => app_id})
    puts "Deleted #{deleted["n"]} documents from ApplicationAuthorization collection in Tenant DB"
  end

  puts "All done."
end

# Run it
main(ARGV)
