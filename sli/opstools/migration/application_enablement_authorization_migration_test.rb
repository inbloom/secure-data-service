require 'mongo'
DEBUG = true

def application_enablement_migration_test()
  @db = @conn["sli"]
  @applications = @db["application"]
  @applications.find({}).each do |app|
    body = app["body"]
    appId = app["_id"]
    edOrgs = body["authorized_ed_orgs"]
    puts appId
    if edOrgs != nil
      puts edOrgs.length
    end
  end
end

def application_authorization_migration_test(dbname)
  @db = @conn[dbname]
  @appAuths = @db['applicationAuthorization']
  @appAuths.find({}).each do |appAuth|
    body = appAuth["body"]
    puts appAuth["_id"]
    edorgs = body["edorgs"]
    puts edorgs.length
  end
end


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

def main(argv)
  params = parseArgs(argv)

  @conn = Mongo::Connection.new(params["mongo_host"], params["mongo_port"])

  # Scan each tenants, scan educationOrganization collection in each tenant, create edorg2Tenant map and tenant2Sea map
  edOrg2tenant = {}
  tenant2sea = {}
  tenant2db = {}
  app2bulkExtract = {}

  @conn['sli']['tenant'].find({}).each do |tenant|
    name = tenant['body']['tenantId']
    db = tenant['body']['dbName']

    tenant2db[name] = db

    @conn[db]['educationOrganization'].find({}).each do |edorg|
      edorgId = edorg["_id"]
      edorgBody = edorg["body"]
      if edorgBody["organizationCategories"].include? "State Education Agency"
        puts db + "=> sea " + edorgId if DEBUG == true
        if tenant2sea[db] == nil
          tenant2sea[db] = edorgId
        else
          puts "Migration Script exit - More than one SEA in tenant " +  name
          #return
        end
      end
      edOrg2tenant[edorgId] = db
    end

    @conn[db]["applicationAuthorization"].find({}).each do |appAuth|
      body = appAuth["body"]
      appId = body["applicationId"]

      if appId == nil
        puts "Migration Script exit - applicationAuthorization " + appAuth["_id"] +" is incomplete"
        #return
      else
        @app = @conn["sli"]['application']
        app = @app.find_one({"_id" => appId})
        if app == nil
          puts "Migration Script exit - application " + appId +" does not exist in sli.application collection for tenant " +  name
          #return
        else
          isBulkExtract = app["body"]["isBulkExtract"]
          app2bulkExtract[appId] = isBulkExtract
        end
      end
    end

  end

  #puts "--------------tenant->sea-------------" if DEBUG == true
  #puts tenant2sea.map{|key, value| "#{key}: #{value}"} if DEBUG == true
  #puts "--------------edorg->tenant-------------" if DEBUG == true
  #puts edOrg2tenant.map{|key, value| "#{key}: #{value}"} if DEBUG == true
  #puts "--------------app->bulkExtractFlag-------------" if DEBUG == true
  #puts app2bulkExtract.length
  #puts app2bulkExtract.map{|key, value| "#{key} -> #{value}"} if DEBUG == true

  #Set tenants to complete list
  tenants = tenant2db.keys

  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Migrating application authorization data for tenant: " + tenant + ", database " + dbname
    #application_authorization_migration_test(dbname)
  end

  #application_enablement_migration(edOrg2tenant, tenant2sea,app2bulkExtract)
  application_enablement_migration_test()
  #puts "    " + "All done."
end

# Run it
main(ARGV)