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

class WaitlistUser < SessionResource
  attr_accessor :email

  URL_HEADER = {
    "Content-Type" => "application/json",
    "content_type" => "json",
    "accept" => "application/json"
  }
  
  def initialize(waitlist_user = nil)
    @waitlist_user = waitlist_user
  end
  
  def save
    # TODO: There's definitely something wrong here. Should be using some built-in validator. Not good enough with Ruby on Rails to figure how to fix it.
    if( @waitlist_user["email"].blank? || !(@waitlist_user["email"] =~ /^[a-zA-Z][\w\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]$/))
      return false
    end
    restResult = RestClient.post(APP_CONFIG['api_base']+"/v1/userAccounts/createWaitingListUser", @waitlist_user.to_json, URL_HEADER) {|response, request, result| response }
    restResult.code == 201
  end
  
  def persisted?
    false
  end
end