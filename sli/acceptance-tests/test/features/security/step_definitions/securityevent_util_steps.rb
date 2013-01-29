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
  coll = securityEventCollection()
  coll.remove()
end

Then /^a security event matching "([^"]*)" should be in the sli db$/ do |securityeventpattern|
  disable_NOTABLESCAN()
  secEventCount = getMatchingSecEvents(securityeventpattern);
  enable_NOTABLESCAN()
  assert(secEventCount > 0, "No security events were found with logMessage matching " + securityeventpattern)
end

def securityEventCollection
  db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  coll ||= db.collection('securityEvent')
  return coll
end

def getMatchingSecEvents(securityeventpattern)
    retryCount = 3;
    securityEventCount = 0;
    while retryCount > 0 do
        coll = securityEventCollection()
        retryCount = retryCount - 1
        secEventCount = coll.find({"body.logMessage" => /#{securityeventpattern}/}).count()
        break if secEventCount > 0
        sleep (10)
    end
    puts "SecEvent retries left [" + retryCount.to_s + "]"
    return secEventCount
end





