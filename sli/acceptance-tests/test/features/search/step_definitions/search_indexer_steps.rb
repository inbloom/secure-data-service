require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'
require_relative '../../apiV1/utils/api_utils.rb'

Given /^I DELETE to clear the Indexer$/ do
  @format = "application/json;charset=utf-8"
  url = PropLoader.getProps['elastic_search_address'] + "/midgar"
  restHttpDeleteAbs(url)
  assert(@res != nil, "Response from rest-client POST is nil")
  puts @res
  
  #step "I should receive a return code of 201"  
end

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
        "firstName" => "Doris",
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
        "firstName" => "Doris",
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
  @srcFileName = "InvalidStudentSearch.json"
  src = "test/features/search/test_data/" + @srcFileName
  fileCopy(src)
end

Given /^I drop Valid Files to Inbox Directory$/ do
  @srcFileName = "ValidStudentSearch1.json"
  src = "test/features/search/test_data/" + @srcFileName
  fileCopy(src)
end

Then /^I drop another Valid File to the Inbox Directory$/ do
  @srcFileName = "ValidStudentSearch2.json"
  src = "test/features/search/test_data/" + @srcFileName
  fileCopy(src)
end

Then /^I should see the file has been prcoessed$/ do
  destPath = PropLoader.getProps['elastic_search_inbox'] + "/" + @srcFileName
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
  destPath = PropLoader.getProps['elastic_search_inbox'] + "/" + @srcFileName
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
  done = false
  numTries = 0
  indexCount = 0
  sleep 2
  while (numTries < 10 && !done)
    url = PropLoader.getProps['elastic_search_address'] + "/midgar/_count"
    restHttpGetAbs(url)
    assert(@res != nil, "Response from rest-client POST is nil")
    puts @res
    result = JSON.parse(@res.body)
    indexCount = result["count"]
    if (indexCount == nil)
      indexCount = 0
    end
    if (indexCount.to_i == numEntities.to_i)
      done = true
    else
      numTries+=1
      sleep 1
    end
  end
  assert(indexCount.to_i == numEntities.to_i, "Expected #{numEntities} Actual #{indexCount}")
end

Given /^I configure the job to extract entity "(.*?)" with the following fields "(.*?)"$/ do |arg1, arg2|
end

Given /^I see an output file placed into the directory$/ do
end

Given /^I schedule the job to run every "(.*?)" minute$/ do |arg1|
end

Given /^I configure the file size cutoff to be "(.*?)"$/ do |arg1|
end

Given /^it should have extracted "(.*?)" entities$/ do |arg1|
end

Given /^each entity has its list of extracted fields$/ do
end

def fileCopy(sourcePath, destPath = PropLoader.getProps['elastic_search_inbox'])
  assert(destPath != nil, "Destination path is nil")
  assert(sourcePath != nil, "Source path is nil")
  
  if !File.directory?(destPath)
    FileUtils.mkdir_p(destPath)
  end
  FileUtils.cp sourcePath, destPath  
end
