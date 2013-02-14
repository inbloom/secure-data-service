=begin

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


class CustomRole < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  self.site = "#{APP_CONFIG['api_base']}"
  self.collection_name = "customRoles"

  def realm_name
    begin
      Realm.find(self.realmId).name
    rescue => e
        logger.error e.message
        logger.error e.backtrace.join("\n")     
    end
  end

  def self.defaults
    defs = Array.new
    self.find(:all, :params => {"defaultsOnly" => true}).each do |role|
      defs.push({:groupTitle => role.groupTitle, :names => role.names, :rights => role.rights, :isAdminRole => role.isAdminRole, :selfRights => role.selfRights})
    end
    return defs
  end

end


