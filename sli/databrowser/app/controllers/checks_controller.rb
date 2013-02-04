=begin
#--

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


#A simple controller to handle the logout functionality for the databrowser.
#It's only valid method is logout
class ChecksController < ApplicationController
  #This method will call the system/session/logout endpoint
  #on the Api to clear out your session there, and then clear
  #the session in rails so that they don't get confused
  def logout
    Entity.url_type = "system/session/logout"
    Entity.format = ActiveResource::Formats::JsonLinkFormat
    response = Entity.get("")
    session[:full_name] = nil
    reset_session
    cookies['iPlanetDirectoryPro'] = nil
    render :layout=> false, :file => "#{Rails.root}/public/LoggedOut.html"
  end
end
