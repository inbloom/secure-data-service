require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../utils/api_utils.rb'


Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "learningObjectives"                         if human_readable_id == "LEARNINGOBJECTIVE URI"
  id = "learningStandards"                          if human_readable_id == "LEARNINGSTANDARD URI"
  id = "dd9165f2-65fe-6d27-a8ec-bdc5f47757b7"       if human_readable_id == "LEARNINGOBJECTIVE_WITH_STANDARD ID"
  id = "chileLearningObjectives"                    if human_readable_id == "CHILD_LEARNINGOBJECTIVE"
  id = "parentLearningObjectives"                   if human_readable_id == "PARENT_LEARNINGOBJECTIVE"
  id
end

Given /^I have two math learning objectives and (\d+) english learning objective ingested in the SDS$/ do |arg1|
   # fixture data is configured from SDS
end

Then /^I should receive a "([^"]*)" entity$/ do |entityType|
  assert(@result["entityType"] == convert(entityType), "did not receive #{entityType}")
end

Then /^I should recieve a collection of (\d+) "([^"]*)" entity$/ do |num, entityType|
   count=0
  @result.each do |result|
   if result["entityType"]==entityType
     count = count+1
   end
 end
 assert(count==Integer(num),"didnt receive a collection of #{num} #{entityType} entity")
end

Then /^I should find one entity with "([^"]*)" is "([^"]*)"$/ do |key, value|
  found=false
  @result.each do |result|
    if result[key]==value
      found=true
    end
  end
  assert(found,"didnt find entity with #{key} is #{value}")
end