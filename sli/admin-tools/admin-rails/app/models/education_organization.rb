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


class EducationOrganization < SessionResource
  self.site = APP_CONFIG['api_base'] + "/v1" if APP_CONFIG['api_base'].index("v1") == nil

  self.collection_name = "educationOrganizations"
  schema do
    string "id"
    boolean "is_allowed"
    string  "organizationCategories"
  end

  # Get list of EducationOrganization objects that are immediate children of edOrg with given ID
  def self.get_edorg_children(edOrg)
    children = find(:all, :params => {"parentEducationAgencyReference" => edOrg, "limit" => 0})
    return children
  end
  
  # Return list containing the given edOrg ID and the IDs of all its descendant edOrgs
  def self.get_edorg_descendants(edOrg)
    result = {}
    result[edOrg] = true
    children = get_edorg_children(edOrg).map { |edOrg| edOrg.id }
    children.each do |child|
      desc = get_edorg_descendants(child)
      desc.each do |eo|
        result[eo] = true
      end
    end
    return result.keys
  end

end
