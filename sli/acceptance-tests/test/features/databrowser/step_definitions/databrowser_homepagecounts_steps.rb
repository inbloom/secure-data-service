require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'


Then /^I should see a table for counts$/ do
	assertWithWait("Failed to find Counts table on homepage")  {@driver.find_element(:id, "edorgcounts_home")}
end

Then /^I should not see a table for counts$/ do
	elements = @driver.find_elements(:id, "edorgcounts_home")
	assert(0 == elements.length )
end

And /^my counts for <type> are <ever> and <current>$/ do |table|
	testResult = @driver.find_element(:id, "edorgcounts_home")
#	puts testResult.text
	testResultText = testResult.text.split("/\r?\n/")
#	puts "tokenized testResult: #{testResultText}"
	tableHashes = table.hashes
	testResultText.each do |e| 
#		puts "tokenized testResult item: #{e}"
		tableHashes.each do |f|
#			puts "Table hash: #{f}"
			if e.include? f['type']
				assert(e.include? f['ever'])
				assert(e.include? f['current'])
			end
		end
	end
end
