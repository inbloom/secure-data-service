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


require 'json'
require 'mongo'

require_relative '../../utils/sli_utils.rb'

Given /^the sli securityEvent collection is empty/ do
  securityEventCollection()
  @coll.remove()
end

Then /^a security event matching ("[^"]*") should be in the sli db$/ do |securityeventpattern|
  params = {'search' => 'DB'}
#  search_string = params['search']
  search_string = params[securityeventpattern]

#  securityEventCollection()
  db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  coll ||= db.collection('securityEvent')
  
  STDOUT.puts("Matching on #{securityeventpattern}") if ENV['DEBUG']
  secEventCount = coll.find({"body.logMessage" => /#{search_string}/}).count()
  STDOUT.puts("Found #{secEventCount} matching security events out of " + coll.count().to_s) if ENV['DEBUG']
  STDOUT.puts("Find one security event ") if ENV['DEBUG']
  STDOUT.puts coll.find_one({"body.logMessage" => /#{search_string}/}).to_s if ENV['DEBUG']
  assert(secEventCount > 0, "No security events were found with logMessage matching #{securityeventpattern}")
end

def securityEventCollection
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  @coll ||= @db.collection('securityEvent')
  return @coll
end
