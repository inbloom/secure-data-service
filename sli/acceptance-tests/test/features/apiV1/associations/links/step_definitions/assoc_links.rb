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

require_relative '../../crud/step_definitions/assoc_crud.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = human_readable_id
  id = ["85585b27-5368-4f10-a331-3abcaf3a3f4c", "b4c2a73f-336d-4c47-9b47-2d24871eef96"] if human_readable_id == "STAFF COHORT ASSOC EP1 MULTIPLE"
  id = ["b408635d-8fd5-11e1-86ec-0021701f543f_id", "b408d88e-8fd5-11e1-86ec-0021701f543f_id"] if human_readable_id == "STAFF COHORT ASSOC EP2 MULTIPLE"
  id = ["1b7b93b3-814a-4f30-86b9-bf19dd0064ff", "85585b27-5368-4f10-a331-3abcaf3a3f4c"] if human_readable_id == "STAFF PROGRAM ASSOC EP1 MULTIPLE"
  id = ["9b8c3aab-8fd5-11e1-86ec-0021701f543f_id", "9b8cafdc-8fd5-11e1-86ec-0021701f543f_id"] if human_readable_id == "STAFF PROGRAM ASSOC EP2 MULTIPLE"
  id
end
