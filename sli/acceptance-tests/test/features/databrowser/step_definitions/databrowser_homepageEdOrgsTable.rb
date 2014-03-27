require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'


Then /^I should see a table for EdOrgs$/ do
assertWithWait("Failed to find EdOrgs table on homepage") {@driver.find_element(:id, "edOrg_home")}
end



Then /^I should not see a table for EdOrgs$/ do
elements = @driver.find_elements(:id, "edOrg_home")
assert(0 == elements.length )
end

And /^I should see the values for EdOrgs Table$/ do |table|
testResult = @driver.find_element(:id, "edOrg_home")
#puts testResult.text
testResultText = testResult.text.split("/\r?\n/")
#puts "tokenized testResult: #{testResultText}"
#puts "TABLE: #{table}"
tableHashes = table.hashes
testResultText.each do |e|
tableHashes.each do |f|
#puts "tokenized testResult item: #{e}"
#puts "Table hash: #{f}"
assert(e.include? f['EdOrgsId'])
assert(e.include? f['EdOrgsName'])
assert(e.include? f['EdOrgsType'])
assert(e.include? f['EdOrgsParent'])
end
end
end
