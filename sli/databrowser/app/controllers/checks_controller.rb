=begin
#--

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


#A simple controller to handle the logout functionality for the databrowser.
#It's only valid method is logout
class ChecksController < ApplicationController
  #This method will call the system/session/logout endpoint
  #on the Api to clear out your session there, and then clear
  #the session in rails so that they don't get confused
  def logout
    #TODO remove the second call to the Api/verify it's needed.
    Entity.url_type = "system/session/logout"
    Entity.format = ActiveResource::Formats::JsonLinkFormat
    response = Entity.get("")
    puts "response = #{response}"

    session[:full_name] = nil
    reset_session
    cookies['iPlanetDirectoryPro'] = nil

    url = URI.parse("#{APP_CONFIG['api_base']}/system/session/logout".gsub("v1/", ""))
    req = Net::HTTP::Get.new(url.path)
    req.add_field("Authorization", "Bearer #{SessionResource.access_token}")
    http = Net::HTTP.new(url.host, url.port)
    if url.to_s.match(/^https/)
      http.use_ssl = true
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    end
    render :layout=> false, :file => "#{Rails.root}/public/LoggedOut.html"
  end
end
