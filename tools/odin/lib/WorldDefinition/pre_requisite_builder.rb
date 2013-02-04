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

require 'json'
require 'logger'

require_relative '../Shared/EntityClasses/enum/EducationOrganizationCategoryType.rb'
require_relative '../Shared/EntityClasses/enum/SchoolCategory.rb'

# Pre-Requisite Builder
# -> loads the specified 'STAFF_CATALOG' from the scenario yaml file
# -> builds up pre-requisite conditions for the world builder
#    - state organization ids for education organizations
#    - staff members (and their role) at education organizations
#    - teachers (and their role) at education organizations
class PreRequisiteBuilder

  # load pre-requisites from the staff json file specified in the scenario
  # -> base scenario currently loads staff and teachers from storied data set (linda kim, charles gray, ...)
  # -> this method will also populate education organizations that must be created during world building
  def self.load_pre_requisites(yaml)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    if yaml.nil?
      @log.info "No configuration file specified --> returning."
      return nil
    end

    catalog = yaml["STAFF_CATALOG"]
    pre_requisites = {:seas => {}, :leas => {}, :elementary => {}, :middle => {}, :high => {}}
    if catalog.nil?
      @log.info "No staff catalog specified for scenario --> All staff and teachers will be pseudo-randomly generated."
    else
      @log.info "Staff catalog specified for scenario: #{catalog}"
      catalog_file = File.open(catalog, "r") { |file| file.read }
      catalog_json = JSON.parse(catalog_file)
      catalog_json.each do |member|
        if !member["edOrgs"].nil? and member["edOrgs"].size > 0
          member["edOrgs"].each do |edOrg|
            type = get_symbol_using_type(edOrg["type"])
            if !type.nil?
              association = edOrg["association"]
              if !association.nil? and association.size > 0
                if !member["userId"].nil? and member["userId"].size > 0
                  pre_requisites[type][association] = [] if pre_requisites[type][association].nil?
                  pre_requisites[type][association] << {:staff_id => member["userId"], :name => member["name"], :role => edOrg["role"]}
                else
                  @log.warn "Failed to add staff member from staff catalog due to nil or empty user id."
                end
              else
                @log.warn "Failed to add staff: #{member["userId"]} from staff catalog due to nil or empty education organization name."
              end
            else
              @log.warn "Failed to add staff: #{member["userId"]} from staff catalog due to bad education organization type: #{edOrg["type"]}"
            end
          end
        end
      end
    end
    pre_requisites
  end

  # takes the 'type' specified in the staff catalog and returns the key into the pre-requisites hash
  def self.get_symbol_using_type(type)
    return :seas if type == EducationOrganizationCategoryType.to_string(:STATE_EDUCATION_AGENCY)
    return :leas if type == EducationOrganizationCategoryType.to_string(:LOCAL_EDUCATION_AGENCY)
    return :elementary if type == SchoolCategory.to_string(:ELEMENTARY)
    return :middle     if type == SchoolCategory.to_string(:MIDDLE)
    return :high       if type == SchoolCategory.to_string(:HIGH)
  end
end
