require 'mongo'
require 'securerandom'

def get_parent_edorgs(col, edorg)
  parents = []
  edorg_ent = col.find_one({"_id" => edorg})
  if edorg_ent && edorg_ent["body"]["parentEducationAgencyReference"]
    parents.push(edorg_ent["body"]["parentEducationAgencyReference"])
    more_parents = get_parent_edorgs(col, edorg_ent["body"]["parentEducationAgencyReference"])
    parents.push(*more_parents)
  end
  return parents
end

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

if ARGV.count < 1
  puts "This script updates application authorizations from Pre-70 format to the new format"
  puts "Usage: <dbhost:port>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  exit
end

class PKFactory
  def create_pk(row)
    return row if row[:_id]
    row.delete(:_id)      # in case it exists but the value is nil
    row['_id'] ||= SecureRandom.uuid
    row
  end
end

hp = ARGV[0].split(":")

client = Mongo::MongoClient.new(hp[0], hp[1])

client.database_names.each do |dbName|
  puts "updating db: #{dbName}"

  # This will be a map of LEA's edOrg ID to a list of application IDs
  app_map = {}
  col = client.db(dbName).collection("applicationAuthorization")
  col.find({"body.authType"=>"EDUCATION_ORGANIZATION"}).each { |entry|
    edOrg = entry['body']['authId']
    entry['body']['appIds'].each { |appId|
      if client.db("sli").collection("application").find_one({"_id"=>appId})
        if !app_map[appId]
          app_map[appId] = [edOrg]
        else
          app_map[appId].push(edOrg)
        end
      end
    }     
  }

  # Create extra entries for all the parent and child edorgs

  app_map.each { |app, edorgs|
    #parentEducationAgencyReference
    extra_edorgs = []
    edorgs.each{ |edorg|
      extra_edorgs.push(*get_parent_edorgs(client.db(dbName).collection("educationOrganization"), edorg))
      extra_edorgs.push(*get_child_edorgs(client.db(dbName).collection("educationOrganization"), edorg))
    }
    edorgs.push(*extra_edorgs)
    edorgs.uniq!
    client.db(dbName, :pk => PKFactory.new).collection("applicationAuthorization").insert({"body" => {"applicationId" => app, "edorgs" => edorgs}})
  }
  col.find({"body.authType"=>"EDUCATION_ORGANIZATION"}).each { |entry|
    col.remove(entry)
  }
end

