#!/usr/bin/env ruby
=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end
# Example: this_script.rb --all host:myhost.com port:12345
#          this_script.rb host:myhost.com port:12345 db1 db2 db3
#

require 'mongo'

def application_enablement_migration(dbname)
  @db = @conn["sli"]
  @applications = @db["application"]
  @db = @conn[dbname]
  @edorgs = @db["educationOrganization"]
  @applications.find.each do |row|
    appId = row["_id"]
    body = row["body"]
    isBulkExtract = body["isBulkExtract"]
    edOrgs = body["authorized_ed_orgs"]
      if isBulkExtract
        if edOrgs != nil
          moreEdorgs = Set.new(edOrgs)
            #if edorgs is not empty, then add SEA and all top-level LEA
          sealeas = get_sea_topLEAs(@edorgs)
          moreEdorgs.add(sealeas)
          body["authorized_ed_orgs"] = moreEdorgs.to_a
          @applications.update({"_id" => row["_id"]},{"$set" => {"body" => body}})
        end
      else
        if edOrgs != nil
          treeEdorgs = Set.new(edOrgs)
          edOrgs.each do |edOrg|
            puts edOrg
            #get descendants of this edOrg
            children = get_child_edorgs(@edorgs, edOrg)
            treeEdorgs.add(children)
          end
          body["authorized_ed_orgs"] = treeEdorgs.to_a
          @applications.update({"_id" => row["_id"]},{"$set" => {"body" => body}})
        end
      end
  end
end

#Get sea and the its direct leas
def get_sea_topLEAs(col)
    seas = []
    leas = []
    sea_leas = []
    col.find({"body.organizationCategories" => "State Education Agency"}).each do |entry|
      sea_id = entry["_id"]
      seas.push(sea_id);
    end
    if seas.empty? == false
      col.find({"body.parentEducationAgencyReference" => seas}).each do |entry|
          lea_id = entry["_id"]
          leas.push(lea_id)
      end
    end
    sea_leas.push(seas)
    sea_leas.push(leas)
  return sea_leas
end

#Get all the descendants of an edorg
def get_child_edorgs(col, edorg)
  #puts "Getting children of #{edorg}"
  children = []
  col.find({"body.parentEducationAgencyReference" => edorg}).each do |entry|
    id = entry["_id"]
    children.push(id);
    children.push(*get_child_edorgs(col, id))
  end
  #puts "Found children #{children}"
  return children
end


def application_authorization_migration(dbname)
  @db = @conn[dbname]
  @appAuths = @db['applicationAuthorization']
  @db2 = @conn['sli']
  @app = @db2['application']
  @edorgs = @db["educationOrganization"]
  @appAuths.find({}).each do |appAuth|
    body = appAuth["body"]
    edOrgs = body["edorgs"]
    app = @app.find_one({"_id" => body["applicationId"]})
    if app!=nil
      body = app["body"]
      isBulkExtract = body["isBulkExtract"]
      if isBulkExtract
        puts "Bulk Extract Applications"
        body["edorgs"] = get_sea_topLEAs(@edorgs)
        @appAuths.update({"_id" => appAuth["_id"]},{"$set" => {"body" => body}})
      else
        puts "Non Bulk Extract Applications"
        allEdorgs = []
        if edOrgs!= nil
          edOrgs.each do |edorg|
            allEdorgs.push(edorg["_id"])
          end
          body["edorgs"] = allEdorgs
          @appAuths.update({"_id" => appAuth["_id"]},{"$set" => {"body" => body}})
        end
      end
    end
  end
end


def parseArgs(argv)
  result = { "all" => false, "mongo_host" => "localhost", "mongo_port" => 27017, "tenants" => [] }

  for arg in argv
    if arg == "--all"
      result["all"] = true
    elsif arg.include?(":")
      host_port = arg.split(':')
      result["mongo_host"] = host_port[0]
      result["mongo_port"] = host_port[1]
    else
      result["tenants"] << arg
    end
  end
  return result
end

#Main driver
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
      puts "No such tenant: " + tname
      return
    end
  end

  # Make sure that either a list of tenants, or "-all" was given (and not both)
  if ( 0==tenants.length and not params["all"] ) or ( tenants.length >= 1 and params["all"] )
    puts "--------------------------------------------------------------------------------------------"
    puts "| Tenant databases on server '" + params["mongo_host"] + "':"
    tenant2db.each_pair do |tname, dbname|
      puts "|    " + tname + " " + dbname
    end

    puts "--------------------------------------------------------------------------------------------"
    puts "| To migrate on application and applicationAuthorization collection, give argument(s) as follows:\n"
    puts "|\n"
    puts "|     --all                                     Migrate against all tenant dbs\n"
    puts "|       --OR--\n"
    puts "|     <tenant1> [<tenant2> ...]                 Migrate only database(s) for the given tenant(s)\n"
    puts "|\n"
    puts "|    myhost:myport                              Optional hostname and port defaults to localhost:27017"
    puts "--------------------------------------------------------------------------------------------"
    return
  end

  # Set tenants to complete list
  if params["all"]
    tenants = tenant2db.keys
  end


  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Migrating application enablement data for tenant: " + tenant + ", database " + dbname
    application_enablement_migration(dbname)
    application_authorization_migration(dbname)
  end

  puts "    " + "All done."
end

# Run it
main(ARGV)