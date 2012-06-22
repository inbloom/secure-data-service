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


class Role < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/admin/"
  
  def self.get_static_information
    @roles = Role.all
    @roles.each do |role|
      role.examples = nil
      role.individual = nil
      case role.name
      when /Aggregate/
        role.examples = "State Data Analyst, State DOE Representative"
        role.individual = nil
      when /Leader/
        role.examples = "School Principal, District Superintendent, State Superintendent" 
        role.individual = "student enrolled in my district(s) or school(s)"
      when /Educator/
        role.examples = "Teacher, Athletic Coach, Classroom Assistant"
        role.individual = "student enrolled in my sections"
      when /IT Administrator/
        role.examples = "SLC Operator, SEA IT Admin, LEA IT Admin"
        role.individual = "student enrolled in my district(s) or school(s)"
      end
      role.general = []
      role.restricted = []
      role.aggregate = nil
      role.rights.each do |right|
        case right
        when /AGGREGATE_READ/
          role.aggregate = "yes"
        when /READ_GENERAL/
          role.general << "R"
        when /READ_RESTRICTED/
          role.restricted << "R"
        when /WRITE_GENERAL/
          role.general << "W"
        when /WRITE_RESTRICTED/
          role.restricted << "W"
        end    
      end
      role.general = role.general.join('/')
      role.restricted = role.restricted.join('/')
    end
    @roles
  end
end
