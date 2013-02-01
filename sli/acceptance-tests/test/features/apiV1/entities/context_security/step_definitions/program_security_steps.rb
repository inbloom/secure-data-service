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
require_relative '../../../../utils/sli_utils.rb'

Transform /the program "([^"]*)"/ do |arg1|
  id = "9b8cafdc-8fd5-11e1-86ec-0021701f543f_id" if arg1 == "ACC-TEST-PROG-1"
  id = "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id" if arg1 == "ACC-TEST-PROG-2"
  id
end

Transform /the student "([^"]*)"/ do |arg1|
  id = "103fe655-a376-4945-aa6e-fc389ff138d4_id" if arg1 == "Randy Voelker"
  id = "9c206f2d-8f9c-4bb7-a0d4-03f500c2136f_id" if arg1 == "Curtis Omeara"
  id = "431b6cca-dfd9-4512-93ce-36796e9310e1_id" if arg1 == "Theresa Deguzman"
  id = "9b00720f-1341-4e1a-b0d0-34ef1671ec87_id" if arg1 == "Paul Bunker"
  id = "e4f71ad7-13e5-472f-812f-99d0a8448f59_id" if arg1 == "Sabrina Knepper"
  id = "737dd4c1-86bd-4892-b9e0-0f24f76210be_id" if arg1 == "Roberta Jones"
  id = "eaa8286a-9a4f-452a-978d-aba7351c5b4f_id" if arg1 == "Christopher Bode"
  id = "6f9692c7-5d41-4c07-82be-ba377ca0fbd2_id" if arg1 == "Todd Angulo"
  id = "22bf5f8f-5e6b-4749-9e1a-2efda072d506_id" if arg1 == "Agnes Trinh"
  id = "86af5b86-e05e-4360-858f-68ce05d32cf1_id" if arg1 == "Stella Rego"
  id = "ace7d09a-56b4-486a-85bd-64474ab64083_id" if arg1 == "Glenda Koch"
  id = "1c30fdce-11ad-4894-a95d-d8315c88ac7d_id" if arg1 == "Johnny Tallent"
  id = "33b80864-ec9a-4836-b114-47e45b291ac6_id" if arg1 == "Thelma Frasier"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id" if arg1 == "Marvin Miller"
  id = "6a98d5d3-d508-4b9c-aec2-59fce7e16825_id" if arg1 == "Delilah D. Sims"
  id
end

When /^I make an API call to get (the program "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/programs/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end
