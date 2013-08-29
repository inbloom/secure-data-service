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


require 'mongo'
require 'rest-client'


################## Static Data ########################################

@jenkinsHostname = "jenkins.slidev.org"
@jenkinsMongoPort = 27017
@token = ""

@pathToTestMap = {
  "sli/acceptance-tests/test/features/api" => ["api", "odin-api"],
  "sli/acceptance-tests/test/features/simple_idp" => ["admin" , "databrowser"],
  "sli/acceptance-tests/test/features/ingestion" => ["ingestion"],
  "sli/acceptance-tests/test/features/admintools" => ["admin"],
  "sli/acceptance-tests/test/features/databrowser" => ["databrowser"],
  "sli/acceptance-tests/test/features/dash" => ["dashboard"],
  "sli/acceptance-tests/test/features/odin" => ["odin"],
  "sli/acceptance-tests/test/features/apiV1/contextual_roles" => ["contextual-role"],    
  "sli/acceptance-tests/test/features/ingestion/features/ingestion_dashboardSadPath.feature" => ["ingestion", "dashboard"],
  "sli/acceptance-tests/test/features/ingestion/test_data/DashboardSadPath_IL_Daybreak" => ["ingestion", "dashboard"],
  "sli/acceptance-tests/test/features/ingestion/test_data/DashboardSadPath_IL_Sunset" => ["ingestion", "dashboard"],
  "sli/acceptance-tests/test/features/ingestion/test_data/DashboardSadPath_NY" => ["ingestion", "dashboard"],
  "sli/acceptance-tests/test/features/bulk_extract" => ["bulk-extract"],
  "sli/acceptance-tests/test/data/Midgar_data" => ["api", "odin-api" , "databrowser", "sdk"],
  "sli/acceptance-tests/test/data/Hyrule_data" => ["api", "odin-api" , "sdk"],
  "sli/acceptance-tests/test/data/unified_data" => ["dashboard", "sdk"],
  "sli/acceptance-tests/test/data/application_fixture.json" => ["api", "odin-api", "admin", "sdk"],
  "sli/acceptance-tests/test/data/realm_fixture.json" => ["api", "odin-api", "admin", "dashboard", "sdk"],
  "sli/acceptance-tests/test/data/oauth_authentication_tokens.json" => ["api", "odin-api"],
  "sli/acceptance-tests/suites/bulk-extract.rake" => ["bulk-extract"],
  "sli/acceptance-tests/suites/ingestion.rake" => ["ingestion"],
  "sli/acceptance-tests/suites/dashboard.rake" => ["dashboard"],
  "sli/api/" => ["api", "odin-api", "search-indexer", "admin", "sdk", "bulk-extract", "databrowser", "contextual-role", "dashboard"],
  "sli/simple-idp" => ["api", "odin-api", "admin", "sdk", "contextual-role", "dashboard"],
  "sli/SDK" => ["admin", "dashboard", "sdk"],
  "sli/data-access" => ["api", "odin-api", "ingestion", "bulk-extract", "contextual-role", "dashboard"],
  "sli/domain" => ["api", "odin-api", "ingestion", "bulk-extract", "dashboard"],
  "sli/bulk-extract" => ["bulk-extract"],
  "sli/ingestion/ingestion-core" => ["ingestion", "odin"],
  "sli/ingestion/ingestion-base" => ["ingestion", "odin"],
  "sli/ingestion/ingestion-validation" => ["ingestion"],
  "sli/ingestion/ingestion-service" => ["ingestion", "odin"],
  "sli/admin-tools" => ["admin"],
  "sli/dashboard/src" => ["dashboard"],
  "sli/databrowser" => ["databrowser"],
  "sli/search-indexer" => ["search-indexer", "dashboard"],
  "tools/odin" => ["odin", "odin-api"]
}

@testIdToUrlMap = {
  "api" => "NTS_API_Tests",
  "admin" => "NTS_Admin_Tests",
  "ingestion" => "NTS_Ingestion_Service_Tests",
  "dashboard" => "NTS_Dashboard_Tests",
  "databrowser" => "NTS_Databrowser_Tests",
  "sdk" => "SDK_Tests",
  "search-indexer" => "Search_Indexer_Tests",
  "odin" => "Odin_Data_Generation_Tests",
  "odin-api" => "API_Odin_Tests",
  "bulk-extract" => "Bulk_Extract_Tests",
  "contextual-role" => "API_Contextual_Roles_Tests"
}

################## Helpers and Input Parsing ###########################

if __FILE__ == $0
  unless ARGV.length == 2
      puts "Usage: prompt>ruby " + $0 + " commitHash view"
      puts "Example: ruby #{$0} 17570fe99fd6cfbdfd82d7121506ce5652548250 Master"
      exit(1)
  end

  currHash = ARGV[0]
  currView = ARGV[1]
end

# returns true if the time stamp of firstHash is less than the timestamp of the secondHash
def checkCommitOrder(firstHash, secondHash)
  firstTime = `git show -s --format=format:"%ad" #{firstHash} --date=raw | awk '{printf $1;}'`
  secondTime = `git show -s --format=format:"%ad" #{secondHash} --date=raw | awk '{printf $1;}'`

  firstTime.to_i < secondTime.to_i
end

# returns array of files changed between two hashes
# returns empty array if timestamp of first hash > second hash
def getFilesChanged(firstHash, secondHash)
  if firstHash.nil?
    output = `git ls-tree --name-only -r #{secondHash}`
  else
    unless checkCommitOrder(firstHash, secondHash)
      return []
    end
    output = `git diff --name-only #{firstHash} #{secondHash}`
  end
  list = output.split(/\n/)

  list
end

def getLastHashFromMongo(view)
  conn = Mongo::Connection.new(@jenkinsHostname, @jenkinsMongoPort)
  db = conn.db("git_hash")
  coll = db['commit']

  entry = coll.find_one("_id" => "last_used_commit_for_#{view}")
  entry = entry["commit_hash"] unless entry.nil?

  entry
end

def whichTestsToRun(hash, view)
  testsToRun = []
  mongoHash = getLastHashFromMongo(view)
  puts "Last commit hash in mongo is #{mongoHash}"
  changedFiles = getFilesChanged(mongoHash, hash)
  puts "Changed files: #{changedFiles}"
  keySet = @pathToTestMap.keys
  changedFiles.each do |file|
    keySet.each do |key|
      if file =~ /^#{key}/
        testsToRun.push @pathToTestMap[key]
        next
      end
    end
  end
  testsToRun.flatten.uniq
end

# given a test id to run, it will make the relevant api posts to jenkins to spart the appropriate test jobs
def sparkTest(test, hash, view)
  data = ""
  url = "http://jenkinsapi_user:test1234@#{@jenkinsHostname}:8080/view/#{view}/job/#{@testIdToUrlMap[test]}_#{view}/buildWithParameters?COMMIT_HASH=#{hash}"
  puts "Running #{test} test with url #{url}"
  res = RestClient.post(url, data, {:content_type => "application/json"} ){|response, request, result| response }

  puts(res.code,res.body,res.raw_headers)
end

# will update mongo to reflect the supplied git hash as the last used
def updateMongo(hash, view)
  conn = Mongo::Connection.new(@jenkinsHostname, @jenkinsMongoPort)
  db = conn.db("git_hash")
  coll = db['commit']
  currTime = Time.new

  coll.update({"_id" => "last_used_commit_for_#{view}"}, {"$set" => {"commit_hash" => hash, "lastUpdate" => currTime}}, {:upsert => true})

  puts "Newly persisted git hash in mongo: #{coll.find_one("_id" => "last_used_commit")["commit_hash"]}"


end

##################### Main Methods #########################################


whichTestsToRun(currHash, currView).each do |test|
  sparkTest(test, currHash, currView)
end

updateMongo(currHash, currView)










