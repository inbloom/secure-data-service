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
    puts "| Enables an App for every edorg in a tenant, give argument(s) as follows:"
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

def get_edorgs_for_tenant(tenantDbName)

  edorgids = []

  db = @conn[tenantDbName]['educationOrganization']
  db.find({}).each do |edorg|
       edorgids.push(edorg["_id"])
  end

  return edorgids

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


  # Update Application Enablements
  app = @conn['sli']['application'].find_one({'body.client_id' => params['app_id']})
  if app.nil?
    puts "Could not find app with client id #{params["app_id"]}"
    usage(params, tenant2db)
  end
  app_id=app["_id"];
  app_body=app["body"];
  authed_edorgs = app_body["authorized_ed_orgs"];

  for tname in tenants
    tenant_edorg_ids = get_edorgs_for_tenant(tenant2db[tname])
    puts "Enabling application for #{tenant_edorg_ids.size} edorgs in tenant #{tname}"
    authed_edorgs = authed_edorgs + tenant_edorg_ids
    authed_edorgs = authed_edorgs.uniq
  end

  app_body["authorized_ed_orgs"] = authed_edorgs

  @conn['sli']['application'].update({"_id" => app_id}, app)



  puts "All done."
end

# Run it
main(ARGV)
