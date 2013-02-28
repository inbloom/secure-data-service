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

require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'
require_relative '../../utils/api_utils.rb'
require 'test/unit'

When /^I navigate to GET for each resource available$/ do
  resources.each do |resource|
    step "I navigate to GET \"</v1#{resource}>\""
    begin
      result = JSON.parse(@res.body)
    rescue JSON::ParserError => e
      $stderr.puts """JSON Parse Exception:
                  \tresource = #{resource}
                  \tqueryParams = #{@queryParams}
                  \tbody = #{@res.body}
                  \tuser = #{@user}"""
    end
    assert(result != nil, "result of parsing result body was nil!")
    assert(@res.code != 500, "Return code was 500 from api, body = #{@res.body}\n
           #requesting resource #{resource}")

  end
end

Then /^I should not encounter any table scans$/ do
end

def resources
config_path = File.expand_path("../../../../../../../api/src/main/resources/wadl/resources.json", __FILE__)
  namespaces = JSON.parse(File.read(config_path))

  full_paths = Array.new

  namespaces.each do |namespace|
    v1_resources = namespace['resources']
    paths = get_resource_paths v1_resources
    paths.each do |path|
        if path.include? "{id}"
            base_resource = get_base_resource path
            sub_id = get_id_for_resource base_resource
            path.gsub!("{id}", sub_id)
            full_paths.push(path)
        end
    end
  end

  full_paths
end

def get_resource_paths resources, base = ""
  paths = []
  resources.each do |resource|
    paths << base + resource['path']
    if resource.has_key? 'subResources'
      sub_paths = get_resource_paths resource['subResources'], base + resource['path']
      paths.concat sub_paths 
    end
  end
  paths
end

def get_base_resource path
  path.split('/')[1]
end

def get_id_for_resource resource
  ids = {"reportCards"=>"20120613-fd6c-4d3a-8a84-97bd8dd829b7",
  "sections"=>"01777045-266c-4e64-8f71-7bcb27b89e5c_id",
  "studentProgramAssociations"=>"242b5d92-e69a-416e-b964-6ceb8756fd33_idec9e6375-fa26-444f-9151-3374a685bb87_id",
  "courses"=>"0002f3f2-cf56-425a-ba24-56f805331743",
  "home"=>"",
  "gradingPeriods"=>"b40a7eb5-dd74-4666-a5b9-5c3f4425f130",
  "cohorts"=>"9ac7ad37-80aa-42ab-9d63-e48cc70a7863_id",
  "sessions"=>"0410354d-dbcb-0214-250a-404401060c93",
  "courseTranscripts"=>"36aeeabf-ee9b-46e6-8039-13320bf12346",
  "gradebookEntries"=>"0dbb262b-8a3e-4a7b-82f9-72de95903d91_id20120613-56b6-4d17-847b-2997b7227686_id",
  "staffEducationOrgAssignmentAssociations"=>"05e3de47-9e41-c048-a572-3eb4c7ee9095",
  "disciplineIncidents"=>"0e26de79-222a-5e67-9201-5113ad50a03b",
  "studentGradebookEntries"=>"0f5e6f78-5434-f906-e51b-d63ef970ef8f",
  "assessments"=>"c757f9f2dc788924ce0715334c7e86735c5e1327_id",
  "studentCohortAssociations"=>"9ac7ad37-80aa-42ab-9d63-e48cc70a7863_iddd5e5b41-30fb-40e5-a968-afe7ae32fce3_id",
  "educationOrganizations"=>"0a922b8a-7a3b-4320-8b34-0f7749b8b062",
  "teacherSectionAssociations"=>"15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id",
  "schools"=>"0a922b8a-7a3b-4320-8b34-0f7749b8b062",
  "programs"=>"242b5d92-e69a-416e-b964-6ceb8756fd33_id",
  "learningStandards"=>"dd9165f2-653e-6e27-a82c-bec5f48757b8",
  "parents"=>"38ba6ea7-7e73-47db-99e7-d0956f83d7e9",
  "teachers"=>"04f708bc-928b-420d-a440-f1592a5d1073",
  "studentAcademicRecords"=>"56afc8d4-6c91-48f9-8a11-de527c1131b7",
  "learningObjectives"=>"dd9165f2-65be-6d27-a8ac-bdc5f46757b6",
  "attendances"=>"4beb72d4-0f76-4071-92b4-61982dba7a7b",
  "studentSchoolAssociations"=>"002aa13e-0352-4744-afd0-3c1be2770433",
  "teacherSchoolAssociations"=>"26a4a0fc-fad4-45f4-a00d-285acd1f83eb",
  "staff"=>"04f708bc-928b-420d-a440-f1592a5d1073",
  "grades"=>"708b3c95-9942-11e1-a8a9-68a86d21d918",
  "studentCompetencies"=>"3a2ea9f8-9acf-11e1-add5-68a86d21d918",
  "studentSectionAssociations"=>"04ca900a-dc0b-41b8-8d47-504fb73b5166_id6f4183d0-a1bb-4584-b23c-49dba741534f_id",
  "staffCohortAssociations"=>"235b88ea-bfea-42ce-8b06-542143e19909",
  "studentAssessments"=>"e5e13e61-01aa-066b-efe0-710f7a011115_id",
  "competencyLevelDescriptor"=>"",
  "staffProgramAssociations"=>"04223945-b773-425c-8173-af090a960603",
  "studentCompetencyObjectives"=>"313db42ad65b911b0897d8240e26ca4b50bddb5e_id",
  "studentDisciplineIncidentAssociations"=>"74cf790e-84c4-4322-84b8-fca7206f1085_id0e26de6c-225b-9f67-9224-5113ad50a03b_id",
  "competencyLevelDescriptorTypes"=>"",
  "studentParentAssociations"=>"5738d251-dd0b-4734-9ea6-417ac9320a15_idc5aa1969-492a-5150-8479-71bfc4d57f1e_id",
  "courseOfferings"=>"00291269-33e0-415e-a0a4-833f0ef38189",
  "disciplineActions"=>"0e26de6c-225b-9f67-9201-8113ad50a03b",
  "students"=>"00209530-6e1f-4273-a5a7-eb686c79fcd9_id",
  "graduationPlans"=>"0002f3f2-cf56-425a-ba24-gradplan1111"}
  ids[resource]
end
