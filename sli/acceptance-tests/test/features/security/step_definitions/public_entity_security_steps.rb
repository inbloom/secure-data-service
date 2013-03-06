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

$context_map = {
  "assessment" => "/v1/assessments/b94b5194d45cd707465627c0cd6c4f68f3558600_id/",
  "competencyLevelDescriptor" => "/v1/competencyLevelDescriptors/",
  "course" => "/v1/courses/5841cf31-16a6-4b4d-abe1-3909d86b4fc3/",
  "courseOffering" => "/v1/courseOfferings/88ddb0c4-1787-4ed8-884e-96aa774e6d42/",
  "educationOrganization" => "/v1/educationOrganizations/92d6d5a0-852c-45f4-907a-912752831772/",
  "graduationPlan" => "/v1/graduationPlans/03d6a408-9ed8-4fea-88bb-gradplan1114/",
  "gradingPeriod" => "/v1/gradingPeriods/09f91102-1122-40fa-afc2-98ae32abc222/",
  "learningObjective" => "/v1/learningObjectives/dd9165f2-65fe-6d27-a8ec-bdc5f47757b7/",
  "learningStandard" => "/v1/learningStandards/dd9165f2-653e-7d27-a82c-bdc5f44757f4/",
  "program" => "/v1/programs/9b8cafdc-8fd5-11e1-86ec-0021701f543f_id/",
  "school" => "/v1/schools/92d6d5a0-852c-45f4-907a-912752831772/",
  "section" => "/v1/sections/15ab6363-5509-470c-8b59-4f289c224107_id/",
  "session" => "/v1/sessions/c549e272-9a7b-4c02-aff7-b105ed76c904/",
  "studentCompetencyObjective" => "/v1/studentCompetencyObjectives/313db42ad65b911b0897d8240e26ca4b50bddb5e_id"
}

When /^I make a call to get "(.*?)"$/ do |arg1|
  restHttpGet($context_map[arg1])
end

Then /^I should see the entity in the response$/ do
  body = JSON.parse(@res.body)
  assert(body != nil, "Did not get JSON response")
  assert(body["id"] != nil , "Body did not contain key \"id\"")
end

When /^I make a call to delete "(.*?)" which I don't have context to$/ do |arg1|
  restHttpDelete($context_map[arg1])
end