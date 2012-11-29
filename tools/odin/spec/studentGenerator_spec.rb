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
require_relative 'spec_helper'
require_relative '../lib/OutputGeneration/XML/studentParentInterchangeGenerator'
require 'factory_girl'

FactoryGirl.find_definitions

describe 'StudentGenerator' do
  let(:interchange) {StringIO.new('', 'w')}
  let(:generator) {StudentParentInterchangeGenerator.new(interchange, 1)}
  let(:student) {FactoryGirl.build(:student)}
  let(:parent) {FactoryGirl.build(:parent)}
  let(:entities) { [:student, :parent]}
  describe '<<' do
    it 'will write a student to edfi' do

      entities = [student,parent]
    
      generator << student
      generator << parent

      puts "Interchange #{interchange.string}"
      interchange.string.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      interchange.string.match('<FirstName>John</FirstName>').should_not be_nil
      interchange.string.match('<LastSurname>Snow</LastSurname>').should_not be_nil
      interchange.string.match('<EmailAddress>jsnow@thewall.com</EmailAddress>').should_not be_nil
    end
  end
end

