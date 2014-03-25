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
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'
require_relative '../../apiV1/utils/api_utils.rb'
require 'socket'
require 'mongo'

MIDGAR_DB_NAME = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"

############################### Before Scenario Do ################################

Before('@clearIndexer') do |scenario|
  # Import tenant just in case it's empty in later extracts
  step 'I import into tenant collection'
end

###############################  After Scenario Do ###############################
# Clear Elastic Search Indexer
# Clear student and section collection from mongo
After('@clearIndexer') do |scenario|
  # we might not need to clear indexer, as extract command will clear it
  step 'I DELETE to clear the Indexer'
  #undo the import in Before scenario
  step 'I clear the tenants that I previously imported'
end
###################################################################################

Given /^I send a command to start the extractor to extract now$/ do
  sendCommand("extract")
end

Given /^I send a command to start the extractor to extract "(.*?)" now$/ do |tenant|
  sendCommand("extract #{tenant}")  
end

Given /^I DELETE to clear the Indexer$/ do
  # we don't need a token for search calls
  @sessionId = ""
  @format = "application/json;charset=utf-8"
  url = Property['elastic_search_address']
  restHttpDeleteAbs(url)
  assert(@res != nil, "Response from rest-client POST is nil")
  puts @res
end

Given /^I flush the Indexer$/ do
  @format = "application/json;charset=utf-8"
  url = Property['elastic_search_address'] + "/_flush"
  restHttpPostAbs(url)
  assert(@res != nil, "Response from rest-client POST is nil")
  puts @res
end

When /^I do elastic search for assessment in Midgar tenant$/ do
      url = Property['elastic_search_address'] + "/" + MIDGAR_DB_NAME + "/assessment/_search?from=0&size=10000"
      restHttpGetAbs(url)
      assert(@res != nil, "Response from rest-client GET is nil")
      #puts @res
      result = JSON.parse(@res.body)
      @index_entities = result["hits"]["hits"]
      #puts @index_entities
end

Then /^I should see below records in response$/ do |table|
    sleep 5
    @table = table
    verifyIndexerEntities(@index_entities,@table)
end

When /^I update some assessment records in mongo$/ do
  updateAssmtEntityId = "e33ce38ad4136e409b426b1ffe7781d09aed2aec_id"
  updateAssmtEntityBody =   
  { 
        "assessmentTitle"=>"READ2-MOY 2", 
        "assessmentFamilyReference"=>"3e06112dd0cdb94f96c8dc55e770e5e51c65c6e7_id", 
        "assessmentIdentificationCode"=>
        [
         {"identificationSystem"=>"LEA", 
           "ID"=>"01234B"}
         ], 
         "academicSubject"=>"Reading", 
         "assessmentCategory"=>"Benchmark test", 
         "gradeLevelAssessed"=>"Second grade",
         "lowestGradeLevelAssessed"=>"First grade", 
         "assessmentPerformanceLevel"=>[
           {
             "performanceLevelDescriptor"=>
             [
             {"codeValue"=>"Level 1"}, 
               {"description"=>"At or Above Benchmark"}
                 ], 
               "assessmentReportingMethod"=>"Composite Score", 
               "minimumScore"=>190,
               "maximumScore"=>380
           }, 
               {
                 "performanceLevelDescriptor"=>
                 [
                   {"codeValue"=>"Level 2"}, 
                     {"description"=>"Below Benchmark"}
                       ], 
                 "assessmentReportingMethod"=>"Composite Score", 
                 "minimumScore"=>100, 
                 "maximumScore"=>200
               }, 
                 {
                   "performanceLevelDescriptor"=>[
                     {"codeValue"=>"Level 3"}, 
                     {"description"=>"Well Below Benchmark"}
                       ], 
                     "assessmentReportingMethod"=>"Composite Score", 
                     "minimumScore"=>13, 
                     "maximumScore"=>144}
                     ], 
                     "maxRawScore"=>380, 
                     "minRawScore"=>13, 
                     "contentStandard"=>"School Standard", 
                     "version"=>2,
                     "assessmentPeriodDescriptorId"=>"486b2868f8556c81e7d2094845bf3a40a0abef02_id"
  }
  

  conn = Mongo::Connection.new(Property[:db_host], Property[:db_port])
  @mdb = conn.db(MIDGAR_DB_NAME)
  
  # update the assessment entity with id is e33ce38ad4136e409b426b1ffe7781d09aed2aec_id
  @assmt_coll = @mdb["assessment"]
  assmtEntity=@assmt_coll.find_one({"_id"=>updateAssmtEntityId})
  assert(assmtEntity, "cant find assmt entity with id: #{updateAssmtEntityId}")
  assmtEntity["body"] = updateAssmtEntityBody
  @assmt_coll.save(assmtEntity)
  
end

When /^I update some objectiveAssessment records in mongo$/ do
  updateOA = [
      {
        "type" => "objectiveAssessment",
        "_id" => "49da176bc1b8025d5a6c2855cebfec421a418541_idfe14cc91fa32a668208a1ea2b9fac0df193fdb26_id",
        "body" => {
          "nomenclature" => "updated-Nomenclature",
          "identificationCode" => "2004-First grade Assessment 2.OA-0 Sub",
          "percentOfAssessment" => 60,
          "assessmentItemRefs" => [
            "49da176bc1b8025d5a6c2855cebfec421a418541_id7b95f8ecea0d09751d711509609be46ed8d81758_id",
            "49da176bc1b8025d5a6c2855cebfec421a418541_id734e6f72fcd15f68b580aec31c6bf918ee6df278_id"
          ],
          "assessmentId" => "49da176bc1b8025d5a6c2855cebfec421a418541_id",
          "assessmentPerformanceLevel" => [
            {
              "performanceLevelDescriptor" => [
                {
                  "codeValue" => "code1"
                }
              ],
              "assessmentReportingMethod" => "Number score",
              "minimumScore" => 10,
              "maximumScore" => 60
            }
          ],
          "learningObjectives" => [
            "18f258460004b33fa9c1249b8c9ed3bd33c41645_id",
            "c19a0f38f598ba8d6d8b7968efd1861f754dcc04_id",
            "43ebe40d85cb70c4dc00ed94ee9f68cfae0c5d1a_id",
            "ff84d3d1c6594847234ab13f8cc8bcd2a45bb75d_id",
            "8f8b1ff4fd3459d3ab1bc54c9deb3581820e0bac_id"
          ],
          "maxRawScore" => 60
        }
      }  
    ]
    
  #update OA in assessment with id is 49da176bc1b8025d5a6c2855cebfec421a418541_id
   
   updateAssmtEntityId = "49da176bc1b8025d5a6c2855cebfec421a418541_id"
   assmtEntity = @assmt_coll.find_one({"_id"=>updateAssmtEntityId})
   assert(assmtEntity, "cant find assmt entity with id: #{updateAssmtEntityId}")
   assmtEntity["objectiveAssessment"]= updateOA
   @assmt_coll.save(assmtEntity)
end

When /^I update some assessmentItem records in mongo$/ do
  updateAI =  [
   {
     "type" => "assessmentItem",
     "_id" => "49da176bc1b8025d5a6c2855cebfec421a418541_id29dffcc6126fc40afd39abe470e80f56e0ce9225_id",
     "body" => {
       "identificationCode" => "2004-First grade Assessment 2#4",
       "assessmentId" => "49da176bc1b8025d5a6c2855cebfec421a418541_id",
       "correctResponse" => "A",
       "itemCategory" => "Multiple Choice",
       "maxRawScore" => 20
     }
   }
   ]
   
  #update AI in assessment with id is 49da176bc1b8025d5a6c2855cebfec421a418541_id
    
    updateAssmtEntityId = "49da176bc1b8025d5a6c2855cebfec421a418541_id"
    assmtEntity = @assmt_coll.find_one({"_id"=>updateAssmtEntityId})
    assert(assmtEntity, "cant find assmt entity with id: #{updateAssmtEntityId}")
    assmtEntity["assessmentItem"] = updateAI
    @assmt_coll.save(assmtEntity)
end

When /^I update some assessmentPeriodDescriptor records in mongo$/ do
  updateAPDId = "486b2868f8556c81e7d2094845bf3a40a0abef02_id"
  updateAPDBody = {
      "codeValue"=>"assessment_2013", 
      "description"=>"updated_assessment", 
      "beginDate"=>"2013-01-01", 
      "endDate"=>"2013-02-01"}
      
  #update APD with id is 486b2868f8556c81e7d2094845bf3a40a0abef02_id
   apd_coll = @mdb["assessmentPeriodDescriptor"]
   apdEntity = apd_coll.find_one({"_id"=> updateAPDId})
   apdEntity["body"] = updateAPDBody
   apd_coll.save(apdEntity)
end

When /^I update some assessmentFamily records in mongo$/ do
  updateAFId = "1f3dfc4ddafb1360d971c78c71263cbc07081736_id"
  updateAFBody = {
     "assessmentFamilyTitle" => "2003 First grade Standard",
     "assessmentPeriods" => [
       "2eb2c7b35c165a1ba14fb391fce3520a7a9aa564_id"
     ],
     "assessmentFamilyIdentificationCode" => [
       {
         "identificationSystem" => "State",
         "ID" => "2003 First grade Standard"
       }
     ],
     "version" => 1
   }
   
  #update AF with id is 1f3dfc4ddafb1360d971c78c71263cbc07081736_id
    #remove its reference to parent assessmentFamily
    af_coll = @mdb["assessmentFamily"]
    afEntity = af_coll.find_one({"_id"=> updateAFId})
    afEntity["body"] = updateAFBody
    af_coll.save(afEntity)
end


When /^I send a command to start the extractor to update "(.*?)" tenant now$/ do |tenant|
  sendCommand("reconcile #{tenant}")
  sleep 5
end


# Current not being used
Given /^I import some student data$/ do
  entityData = {
      "student1" => {
      "birthData" => {
        "birthDate" => "1994-04-04"
      },
      "sex" => "Male",
      "studentUniqueStateId" => "123456",
      "economicDisadvantaged" => false,
      "name" => {
        "firstName" => "Alex",
        "middleName" => "John",
        "lastSurname" => "Doe"
      }
    },
    "student2" => {
      "birthData" => {
        "birthDate" => "1994-04-04"
      },
      "sex" => "Male",
      "studentUniqueStateId" => "9876",
      "economicDisadvantaged" => true,
      "name" => {
        "firstName" => "Matt",
        "middleName" => "Steve",
        "lastSurname" => "Moe"
      }
    }
  }
  entityData.each do |key, value|
    @fields = value
    step "format \"application/vnd.slc+json\""
    step "I navigate to POST \"/students\""
    step "I should receive a return code of 201"
  end 
end

Given /^I drop Invalid Files to Inbox Directory$/ do
  @srcFileName = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a_student_1.json"
  src = "test/features/search/test_data/" + @srcFileName
  fileCopy(src)
end

Given /^I drop Valid Files to Inbox Directory$/ do
  @srcFileName = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a_student_2.json"
  src = "test/features/search/test_data/" + @srcFileName
  fileCopy(src)
end

Then /^I drop another Valid File to the Inbox Directory$/ do
  @srcFileName = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a_student_3.json"
  src = "test/features/search/test_data/" + @srcFileName
  fileCopy(src)
end

Then /^I should see the file has been prcoessed$/ do
  destPath = Property['elastic_search_inbox'] + "/" + @srcFileName
  current = 0
  finished = false
  while (!finished && current < 5)
    if (!File.exists?(destPath))
      finished = true
    end
    sleep 1  
    current +=1
  end
  assert(finished, "#{destPath} exists in Inbox")
end

Given /^I should see the file has not been processed$/ do
  destPath = Property['elastic_search_inbox'] + "/" + @srcFileName
  current = 0
  finished = false
  while (!finished || current < 2)
    if (File.exists?(destPath))
      finished = true
    end
    sleep 1  
    current +=1
  end
  assert(finished, "#{destPath} still exists in Inbox")
end

Then /^Indexer should have "(.*?)" entities$/ do |numEntities|
  verifyElasticSearchCount(numEntities)
end

Given /^I check that Elastic Search is non\-empty$/ do
  verifyElasticSearchCount()
end

Given /^I search in Elastic Search for "(.*?)" in tenant "(.*?)"$/ do |query, tenant|
  url = Property['elastic_search_address'] + "/" + convertTenantIdToDbName(tenant) + "/_search?q=" + query
  restHttpGetAbs(url)
  puts url
  assert(@res != nil, "Response from rest-client GET is nil")
end

Given /^"(.*?)" hit is returned$/ do |expectedHits|
  result = JSON.parse(@res.body)
  hits = result["hits"]["total"]
  assert(expectedHits.to_i == hits.to_i, "Expected: #{expectedHits} Actual: #{hits}")
end

Given /^I search in API for "(.*?)"$/ do |query|
  url = Property["api_server_url"] + "/api/rest/v1/search/students?q=" + query
  restHttpGetAbs(url)
  assert(@res != nil, "Response from rest-client GET is nil")  
  @result = JSON.parse(@res.body)
end

Given /^I see the following fields:$/ do |table|
  @table = table
  json = JSON.parse(@res.body)
  arrayOfHits = json["hits"]["hits"]
  verifyElementsOnResponse(arrayOfHits, @table)   
end

Then /^I see the following search results at index (\d+):$/ do |index, table|
  json = JSON.parse(@res.body)
  @table = table
  verifyElementsOnResponseAtIndex(index, json, @table)
end

Then /^no search results are returned$/ do
  response = JSON.parse(@res.body)
  assert(response == [], "Error: Unauthorized student data is accessible")  
end

Given /^I import into tenant collection$/ do
  tenants = ['Midgar', 'Hyrule']
  DbClient.new.for_sli.open do |db|
    tenants.each do |tenant|
      doc = generateTenantDoc(tenant)
      db.insert(:tenant, doc) unless db.find_one(:tenant, {'body.tenantId' => tenant})
    end
  end
end

Given /^I clear the tenants that I previously imported$/ do
  tenants = ["Midgar", "Hyrule"]
  db = Mongo::Connection.new(Property[:db_host], Property[:db_port])['sli']
  tenants.each do |tenant|
    id = convertTenantIdToDbName(tenant)
    db['tenant'].remove("_id" => id)
  end
end

############ sorting-step utils for pagination ##########
Then /^the header "([^\"]*)" equals (\d+)$/ do |header, value|
  value = convert(value)
  header.downcase!
  headers = @res.raw_headers
  headers.should_not == nil
  assert(headers[header])
  headers[header].should_not == nil
  resultValue = headers[header]
  resultValue.should be_a Array
  resultValue.length.should == 1
  singleValue = convert(resultValue[0])
  singleValue.should == value
end

Then /^the a next link exists with offset equal to (\d+) and limit equal to (\d+)$/ do |start, max|
  links = @res.raw_headers["link"];
  links.should be_a Array
  found_link = false
  links.each do |link|
    if /rel=next/.match link
      assert(Regexp.new("offset=" + start).match(link), "offset is not correct: #{link}")
      assert(Regexp.new("limit=" + max).match(link), "limit is not correct: #{link}")
      found_link = true
    end
  end
  found_link.should == true
end

Then /^the a previous link exists with offset equal to (\d+) and limit equal to (\d+)$/ do |start, max|
  links = @res.raw_headers["link"];
  links.should be_a Array
  found_link = false
  links.each do |link|
    if /rel=prev/.match link
      assert(Regexp.new("offset=" + start).match(link), "offset is not correct: #{link}")
      assert(Regexp.new("limit=" + max).match(link), "limit is not correct: #{link}")
      found_link = true
    end
  end
  found_link.should == true
end



###### End of sorting-step utils for pagination ##########

def fileCopy(sourcePath, destPath = Property['elastic_search_inbox'])
  assert(destPath != nil, "Destination path is nil")
  assert(sourcePath != nil, "Source path is nil")
  
  if !File.directory?(destPath)
    FileUtils.mkdir_p(destPath)
  end
  FileUtils.cp sourcePath, destPath  
end

def verifyElementsOnResponseAtIndex(index, arrayOfElements, table)
  response = arrayOfElements.at(index.to_i)
  table.hashes.each do |row|
    field = row["Field"]
    currentRes = response
    value = nil
    while (field.include? ".")
      delimiter = field.index('.') + 1
      length = field.length - delimiter      
      current = field[0..delimiter-2]        
      field = field[delimiter,length]  
      currentRes = currentRes[current]   
    end          
    value = currentRes[field]
    assert(value == row["Value"], "Expected #{row["Value"]} Actual #{value}" )
  end
end

def verifyElementsOnResponse(arrayOfElements, table)
  arrayOfElements.each do |response|
    table.hashes.each do |row|
      field = row["Field"]
      currentRes = response
      value = nil
      while (field.include? ".")
        delimiter = field.index('.') + 1
        length = field.length - delimiter      
        current = field[0..delimiter-2] 
        puts current       
        field = field[delimiter,length]  
        puts field
        currentRes = currentRes[current]   
        puts currentRes
      end          
      value = currentRes[field]
      assert(value == row["Value"], "Expected #{row["Value"]} Actual #{value}" )
    end
  end
end

def verifyElasticSearchCount(numEntities = -1)
  # set sessionId to be non-nil
  @sessionId = ""
  max = 10
  done = false
  numTries = 0
  indexCount = 0
  sleep 2
  while (numTries < max && !done)
    url = Property['elastic_search_address'] + "/_count"
    restHttpGetAbs(url)
    assert(@res != nil, "Response from rest-client POST is nil")
    puts @res
    result = JSON.parse(@res.body)
    indexCount = result["count"]
    if (indexCount == nil)
      indexCount = 0
    end
    if (indexCount.to_i == numEntities.to_i || (numEntities == -1 && indexCount.to_i > 0))
      done = true
    else
      numTries+=1
      sleep 1
    end
  end
  assert(done, "Expected #{numEntities} Actual #{indexCount}")
end

def generateTenantDoc(tenantName)
  doc = {
    "_id" => convertTenantIdToDbName(tenantName),
    "type" => "tenant",
    "body" => {
      "tenantId" => tenantName,
      "dbName" => convertTenantIdToDbName(tenantName)
      },
    "metaData" => {
        "tenantId" => tenantName
      }
  }
end

def sendCommand(command)
  hostname = Property['elastic_search_host']
  port = Property['elastic_search_remote_command_port']
  socket = TCPSocket.open(hostname, port)
  socket.write(command)
  socket.close 
end

def verifyIndexerEntities(arrayElements,table)
  
  table.hashes.each do |row|
  id_field = row["_id"]
  index_field = row["_index"]
  type_field = row["_type"]
    found = false
    arrayElements.each do |indexEntity|
      if indexEntity["_index"] == index_field and indexEntity["_id"] == id_field and indexEntity["_type"] == type_field
        possibleValues = findPossibleNestedValue(row["Field"],indexEntity["_source"])
          if !possibleValues.nil? and (possibleValues.include? row["Value"] or possibleValues.include? row["Value"].to_i or possibleValues.include? row["Value"].to_f)
          found = true
          end
        break
      end
    end
    assert(found,"didnt find indexer entity with _id: #{id_field}, _index: #{index_field}, _type: #{type_field}, Field: #{row["Field"]}, Value:#{row["Value"]}")
  end
  
end

def findPossibleNestedValue(field,resp,possibleValues=[])
  currentResp = resp
  while(field.include? ".")
    delimiter = field.index('.') + 1
    length = field.length - delimiter      
    current = field[0..delimiter-2] 
    field = field[delimiter,length]  
    resp = resp[current] if !resp.kind_of?(Array) 
    if resp.kind_of?(Array) 
      resp.each do |elementResp|
        findPossibleNestedValue(field,elementResp,possibleValues)
      end
    else
      currentResp = resp
    end
  end
  possibleValues << currentResp[field] if !currentResp[field].nil?
  return possibleValues
end
  

