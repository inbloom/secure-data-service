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

require 'yaml'

require_relative '../lib/WorldDefinition/pre_requisite_builder.rb'
require_relative '../lib/Shared/util.rb'

# specifications for pre-requisite builder
describe "Pre-Requisite Builder" do

  before (:all) do
    configYAML    = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
    @scenarioYAML = load_scenario("10students", configYAML)
  end

  describe "--> retrieving symbol for pre-requisite hash" do
    it "--> will get :seas when 'State Education Agency' is specified" do
      PreRequisiteBuilder.get_symbol_using_type('State Education Agency').should eq(:seas)
    end

    it "--> will get :leas when 'Local Education Agency' is specified" do
      PreRequisiteBuilder.get_symbol_using_type('Local Education Agency').should eq(:leas)
    end

    it "--> will get :elementary when 'Elementary School' is specified" do
      PreRequisiteBuilder.get_symbol_using_type('Elementary School').should eq(:elementary)
    end

    it "--> will get :middle when 'Middle School' is specified" do
      PreRequisiteBuilder.get_symbol_using_type('Middle School').should eq(:middle)
    end

    it "--> will get :high when 'High School' is specified" do
      PreRequisiteBuilder.get_symbol_using_type('High School').should eq(:high)
    end

    it "--> will return nil when an un-recognized value is specified" do
      PreRequisiteBuilder.get_symbol_using_type('Secondary School').should be_nil
    end
  end

  describe "--> handles edge cases" do
    it "--> will handle nil input" do
      PreRequisiteBuilder.load_pre_requisites(nil).should be_nil
    end

    it "--> will handle missing yaml property" do
      @scenarioYAML["STAFF_CATALOG"] = nil
      pre_requisites = PreRequisiteBuilder.load_pre_requisites(@scenarioYAML)
      pre_requisites.each do |type, edOrgs|
        edOrgs.should be_empty
      end
    end

    it "--> will handle malformed json" do
      @scenarioYAML["STAFF_CATALOG"] = "spec/test_data/invalid/staff.json"
      pre_requisites = PreRequisiteBuilder.load_pre_requisites(@scenarioYAML)
      pre_requisites.each do |type, edOrgs|
        edOrgs.should be_empty if type != :high
        edOrgs.should eq({"Daybreak Central High" => [{:staff_id=>"cgray", :name=>"Charles Gray", :role=>"Educator"}]}) if type == :high
      end
    end
  end
end
