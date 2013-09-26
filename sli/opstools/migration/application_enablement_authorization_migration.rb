#
# Release 86 migration script
#
# Migrates data for sli.applicaiton and <tenant>.applicationAuthorization
#

require 'mongo'
DEBUG = false

#
# Update sli.application.authorized_ed_orgs[]
#
def application_enablement_migration(edOrg2tenant, tenant2sea, app2bulkExtract)

  @db = @conn["sli"]
  @apps = @db["application"]

  @apps.find({}).each do |app|
    body = app["body"]
    appId = app["_id"]
    authorized_ed_orgs = body["authorized_ed_orgs"]
    next if authorized_ed_orgs.nil? or authorized_ed_orgs.empty?
    
    puts "enable  --- app "  + appId

    # Map of database IDs of involved tenants
    involved_tenants = {}
    # Ed Org IDs we will replace for the app
    new_authorized_ed_orgs = {}
    
    # Get all tenants involved with this app (for which one more of the tenants edOrgs is enabled)
    authorized_ed_orgs.each do |edOrg|
      if edOrg2tenant.has_key?(edOrg)
        involved_tenants[edOrg2tenant[edOrg]] = true
      else
        puts "WARNING: enabled edOrg '" + edOrg + "' for app '" + appId + "' does not exist in any tenant educationOrganization collection"
        new_authorized_ed_orgs[edOrg] = true
      end
    end
    puts involved_tenants.keys.map{|tenant| "#{tenant}"} if DEBUG == true

    # For each tenant, enable the app for all appropriate edOrgs in that tenant
    involved_tenants.keys.each do |tenant|
      @db = @conn[tenant]
      @edorgs = @db["educationOrganization"]
      if app2bulkExtract[appId]
        puts "Migrating bulk extract application " + appId
        sea = tenant2sea[tenant]
        new_authorized_ed_orgs[sea] = true
        @edorgs.find({"body.parentEducationAgencyReference" => sea}).each do |child|
          new_authorized_ed_orgs[child["_id"]] = true
        end
      else
        puts "Migrating non bulk extract application " + appId
        @edorgs.find({}).each do |edorg|
          new_authorized_ed_orgs[edorg["_id"]] = true
        end
      end
    end

    # Update application with edorgs across all tenants
    body["authorized_ed_orgs"] = new_authorized_ed_orgs.keys
    @apps.update({"_id" => app["_id"]},{"$set" => {"body" => body}})
  end
end

#
# Update <tenant>.applicationAuthorization
#
def application_authorization_migration(dbname, tenant2sea, app2bulkExtract)
  @db = @conn[dbname]
  @appAuths = @db['applicationAuthorization']
  @edorgs = @db["educationOrganization"]
  
  @appAuths.find({}).each do |appAuth|
    body = appAuth["body"]
    appId = body["applicationId"]
    isBulkExtract = app2bulkExtract[appId]

    # Make sure have "edorgs" field, that it is non-null (i.e., an array) and nonempty array
    next if !body.has_key?("edorgs")
    edorgs = body["edorgs"]
    next if edorgs.nil? or edorgs.empty?
    
    if isBulkExtract
      puts "Migrating applicationAuthorization for bulk extract application " + appAuth["_id"]
      new_edorgs = {}
      sea = tenant2sea[dbname]
      new_edorgs[sea] = true
      @edorgs.find({"body.parentEducationAgencyReference" => sea}).each do |child|
        child_id = child["_id"]
        new_edorgs[child_id] = true
      end
      body["edorgs"] = new_edorgs.keys
      @appAuths.update({"_id" => appAuth["_id"]},{"$set" => {"body" => body}})
    else
      puts "Migrating applicationAuthorization for non bulk extract application " + appAuth["_id"]
      allEdorgs = []
      @edorgs.find({}).each do |edorg|
        allEdorgs.push(edorg["_id"])
      end
      body["edorgs"] = allEdorgs
      @appAuths.update({"_id" => appAuth["_id"]},{"$set" => {"body" => body}})
    end
  end
end


# Parse command args
def parseArgs(argv)
  result = {"mongo_host" => "localhost", "mongo_port" => 27017}

  for arg in argv
    if arg.include?(":")
      host_port = arg.split(':')
      result["mongo_host"] = host_port[0]
      result["mongo_port"] = host_port[1]
    end
  end
  return result
end


#
# Main driver
#
def main(argv)
  params = parseArgs(argv)

  @conn = Mongo::Connection.new(params["mongo_host"], params["mongo_port"])

  #######################     CHECK DATA AND BUILD MAPS, etc.
  
  # Get map of app ID -> isbulkExtract
  app2bulkExtract = {}
  @conn["sli"]['application'].find({}).each do |app|
    appId = app["_id"]
    isBulkExtract = app["body"]["isBulkExtract"]
    app2bulkExtract[appId] = isBulkExtract
  end
  
  # Scan each tenants, scan educationOrganization collection in each
  # tenant, create edorg2Tenant map and tenant2Sea map
  tenant2db = {}
  edOrg2tenant = {}
  tenant2sea = {}
  errorsExist = false
  @conn['sli']['tenant'].find({}).each do |tenant|
    name = tenant['body']['tenantId']
    db = tenant['body']['dbName']

    puts "Checking data in tenant '" + name + "'"

    tenant2db[name] = db

    # Get tenant2sea and edOrg2tenant, checking exactly one SEA per tenant
    @conn[db]['educationOrganization'].find({}).each do |edorg|
      edorgId = edorg["_id"]
      edorgBody = edorg["body"]
      if edorgBody["organizationCategories"].include? "State Education Agency"
        puts db + "=> sea " + edorgId if DEBUG == true
        if ! tenant2sea.has_key?(db)
          tenant2sea[db] = edorgId
        else
          puts "Migration Script exit - More than one SEA in tenant " +  name
          errorsExist = true
          next
        end
      end
      edOrg2tenant[edorgId] = db
    end
    # Make sure we got (exactly one) SEA
    if ! tenant2sea.has_key?(db)
      puts "Migration Script exit - No SEA in tenant " +  name
      errorsExist = true
      next
    end

    # Check applicationAuthorization has all "good" apps
    @conn[db]["applicationAuthorization"].find({}).each do |appAuth|
      body = appAuth["body"]
      if ! body.has_key?("applicationId")
        # Unlikely
        puts "Migration Script exit - applicationAuthorization " + appAuth["_id"] +" is incomplete"
        errorsExist = true
        next
      else
        appId = body["applicationId"]
      end

      if !app2bulkExtract.has_key?(appId)
        puts "Migration Script exit - application " + appId +" does not exist in sli.application collection for tenant " +  name
        errorsExist = true
        next
      end
    end
  end

  # Don't continue if there were any errors
  if errorsExist
    puts "ABORTING WITHOUT UDPATE."
    return
  end

  puts "--------------tenant->sea-------------" if DEBUG == true
  puts tenant2sea.map{|key, value| "#{key}: #{value}"} if DEBUG == true
  puts "--------------edorg->tenant-------------" if DEBUG == true
  puts edOrg2tenant.map{|key, value| "#{key}: #{value}"} if DEBUG == true
  puts "--------------app->bulkExtractFlag-------------" if DEBUG == true
  puts app2bulkExtract.length
  puts app2bulkExtract.map{|key, value| "#{key} -> #{value}"} if DEBUG == true

  #######################     UPDATE DATA 

  puts "\n\nMIGRATING DATA ....\n"
  puts "start enablement"
  application_enablement_migration(edOrg2tenant, tenant2sea, app2bulkExtract)

  puts "start authorization"
  for tenant in tenant2db.keys
    dbname = tenant2db[tenant]
    puts "Migrating application authorization data for tenant: " + tenant + ", database " + dbname
    application_authorization_migration(dbname, tenant2sea,app2bulkExtract)
  end


  puts "    " + "All done."
end

# Run it
main(ARGV)
