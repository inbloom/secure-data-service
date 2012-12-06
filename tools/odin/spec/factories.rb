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
    initialize_with { new(52, Date.new(1980, 10, 2)) }
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
    rand = Random.new(42)
    initialize_with { new(52, 42, rand) }
    primaryContactStatus true
  end
  
   factory :assessment do
  
    initialize_with { new(52, Date.new(2012, 11, 2)) }
    assessmentTitle "SAT"
  end
  
   factory :assessment_item do
  
  
    initialize_with { new(52, Date.new(2012, 11, 2) ) }
    identificationCode "8675309"
    itemCategory "Analytic"
    association :assessment, strategy: :build
   
  end
end
