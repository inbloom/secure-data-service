=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'



When /^I start crawling at "(.*?)"$/ do |arg1|
 	@visited = Set.new
 	@to_visit = [arg1]
end

Then /^I should be able to visit all available links$/ do
	
	while @to_visit.empty? == false
		
		url = @to_visit[0].to_s
		@to_visit.delete_at(0)
		
		if @visited.include?(url)
			next
		end

		@visited << url
		
		if url.include?('custom')
			next
		end

		begin 
			steps %Q{
		      When I navigate to GET \"#{url}\"
		      Then I should receive a return code of 200
		    }

		    if @result.kind_of?(Array)
		    	@result.each do |entity|
		    		parseEntity(entity)
		    	end
		   	else
		    	parseEntity(@result)
		    end
		rescue
			if @res.body.include?("Entity not found") || @res.body.include?("Access DENIED")
				# ok, nothing to log
			else

				puts "FAILED: "  + @res.code.to_s + " " + url 
				
				if @res.body.include?("jetty")
					puts "\tMissing endpoint"
				else
					puts @res.body
				end
			end
		end
	end
end

def parseEntity(entity)
	entity["links"].each do |link|
		url = link["href"]
		relativeUrl = makeRelative(url)
		if @visited.include?(relativeUrl) == false
			@to_visit << relativeUrl
		end
	end
end	

def makeRelative(url)
	if url.start_with?('http') 
		match = url.match(/.*api\/rest(.*)/).captures	
		return match[0]
	else
		return url
	end
end








