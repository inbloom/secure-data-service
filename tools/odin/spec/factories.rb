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

require 'factory_girl'

FactoryGirl.define do
  factory :student do
    initialize_with { new(42, Date.new(2000, 9, 1)) }
    sex "Male"
    firstName "John"
    lastName "Snow"
    email "jsnow@thewall.com"

  end

  factory :parent do
    initialize_with { new(FactoryGirl.build(:student), :dad) }
    sex "Male"
    firstName "James"
    lastName "Snow"
    email "jsnow1@thewall.com"
  end

  factory :staff do
    initialize_with { new(1052,  Date.new(1960, 11, 2)) }
    sex "Male"
    firstName "Frank"
    lastName "McCourt"
    email "fmccourt@thewall.com"
  end

  factory :studentParentAssociation do
    initialize_with { new(FactoryGirl.build(:student), :dad) }
    primaryContactStatus true
  end
  
  factory :assessment do
  
    initialize_with { new(52, Date.new(2012, 11, 2)) }
    assessmentTitle "SAT II - US History"
    gradeLevelAssessed "Twelfth grade"
  end
  
  factory :assessment_family do
  
    initialize_with { new(52, Date.new(2012, 11, 2)) }
    assessmentFamilyTitle "SAT II"
  end
  
  factory :assessment_item do

    initialize_with { new(52, FactoryGirl.build(:assessment)) }
    identificationCode "8675309"
    itemCategory "Analytic"
    association :assessment, strategy: :build
   
  end

  factory :behavior_descriptor do
    initialize_with{ new("42", "Incident", "The Noodle Incident", "Standard SEA", "School Violation")}
  end

  factory :discipline_descriptor do
    initialize_with{ new("42", "Chalkboard", "Student has to write a sentance over and over again on the chalkboard", "Standard SEA")}
  end

  factory :date_interval do
    d = Date.new(1985, 11, 18)
    initialize_with{ new(d, d+180, 180) }
  end

  factory :discipline_incident do
    initialize_with{ new(16, 32, "My School", "Mrs Wormwood", FactoryGirl.build(:date_interval), "Classroom", ["42"]) }
  end

  factory :student_discipline_incident_association do
    initialize_with{ new(42, 16, 32, "My School", "Perpetrator") }
  end

  factory :discipline_action do
    initialize_with{ new(42, "My School", FactoryGirl.build(:discipline_incident))}
  end
end
