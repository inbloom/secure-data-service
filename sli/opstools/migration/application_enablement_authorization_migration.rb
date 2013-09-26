require 'mongo'
DEBUG = false

def application_enablement_migration(edOrg2tenant, tenant2sea, app2bulkExtract)
  @db = @conn["sli"]
  @apps = @db["application"]
  @apps.find({}).each do |app|
    body = app["body"]
    appId = app["_id"]
    edOrgs = body["authorized_ed_orgs"]
    if edOrgs != nil and edOrgs.length>0
      puts "enable  --- app "  + appId
      involved_tenants = []
      edOrgs.each do |edOrg|
        dbname = edOrg2tenant[edOrg]
        if dbname != nil
          involved_tenants.push(dbname).uniq!
        end
      end
      puts involved_tenants.map{|tenant| "#{tenant}"} if DEBUG == true
       involved_tenants.each do |tenant|
          @db = @conn[tenant]
          @edorgs = @db["educationOrganization"]
          if app2bulkExtract[appId]
            puts "enable bulk extract application " + appId
            sea_leas = []
            sea = tenant2sea[tenant]
            sea_leas.push(sea)
            @edorgs.find({"body.parentEducationAgencyReference" => sea}).each do |lea|
              lea = lea["_id"]
              sea_leas.push(lea);
            end
            body["authorized_ed_orgs"] = sea_leas
            @apps.update({"_id" => app["_id"]},{"$set" => {"body" => body}})
          else
            puts "enable non bulk extract application " + appId
            allEdorgs = []
            @edorgs.find({}).each do |edorg|
              allEdorgs.push(edorg["_id"])
            end
            body["edorgs"] = allEdorgs
            body["authorized_ed_orgs"] = allEdorgs.to_a
            @apps.update({"_id" => app["_id"]},{"$set" => {"body" => body}})
          end
      end
    end
  end
end

def application_authorization_migration(dbname, tenant2sea, app2bulkExtract)
  @db = @conn[dbname]
  @appAuths = @db['applicationAuthorization']
  @edorgs = @db["educationOrganization"]
  @appAuths.find({}).each do |appAuth|
    body = appAuth["body"]
    appId = body["applicationId"]
    isBulkExtract = app2bulkExtract[appId]
    edorgs = body["edorgs"]
    if edorgs != nil
      if isBulkExtract
        puts "authorize bulk extract application " + appAuth["_id"]
        sea_leas = []
        sea = tenant2sea[dbname]
        sea_leas.push(sea)
        @edorgs.find({"body.parentEducationAgencyReference" => sea}).each do |lea|
          lea = lea["_id"]
          sea_leas.push(lea);
        end
        body["edorgs"] = sea_leas
        @appAuths.update({"_id" => appAuth["_id"]},{"$set" => {"body" => body}})
      else
        puts "authorize non bulk extract application " + appAuth["_id"]
        allEdorgs = []
        @edorgs.find({}).each do |edorg|
          allEdorgs.push(edorg["_id"])
        end
        body["edorgs"] = allEdorgs
        @appAuths.update({"_id" => appAuth["_id"]},{"$set" => {"body" => body}})
      end
    end
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
          return
        end
      end
      edOrg2tenant[edorgId] = db
    end

    @conn[db]["applicationAuthorization"].find({}).each do |appAuth|
      body = appAuth["body"]
      appId = body["applicationId"]

      if appId == nil
        puts "Migration Script exit - applicationAuthorization " + appAuth["_id"] +" is incomplete"
        return
      else
        @app = @conn["sli"]['application']
        app = @app.find_one({"_id" => appId})
        if app == nil
          puts "Migration Script exit - application " + appId +" does not exist in sli.application collection for tenant " +  name
          return
        else
          isBulkExtract = app["body"]["isBulkExtract"]
          app2bulkExtract[appId] = isBulkExtract
        end
      end
    end

  end

  puts "--------------tenant->sea-------------" if DEBUG == true
  puts tenant2sea.map{|key, value| "#{key}: #{value}"} if DEBUG == true
  puts "--------------edorg->tenant-------------" if DEBUG == true
  puts edOrg2tenant.map{|key, value| "#{key}: #{value}"} if DEBUG == true
  puts "--------------app->bulkExtractFlag-------------" if DEBUG == true
  puts app2bulkExtract.length
  puts app2bulkExtract.map{|key, value| "#{key} -> #{value}"} if DEBUG == true

  tenants = tenant2db.keys
  puts "start authorization"
  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Migrating application authorization data for tenant: " + tenant + ", database " + dbname
    application_authorization_migration(dbname, tenant2sea,app2bulkExtract)
  end

  puts
  puts "start enablement"
  application_enablement_migration(edOrg2tenant, tenant2sea,app2bulkExtract)

  puts "    " + "All done."
end

# Run it
main(ARGV)