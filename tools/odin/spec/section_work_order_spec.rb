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

require 'timeout'

require_relative 'spec_helper'
require_relative '../lib/Shared/date_interval.rb'
require_relative '../lib/WorldDefinition/section_work_order'
require_relative '../lib/Shared/EntityClasses/discipline_incident'

# specifications for the section work order factory
describe "SectionWorkOrderFactory" do
  let(:scenario) {{'STUDENTS_PER_SECTION' => {'high' => 10}, 'MAX_SECTIONS_PER_TEACHER' => {'high' => 5},
        'GRADEBOOK_ENTRIES_BY_GRADE' => {
          "Ninth grade" => { "Homework" => {"min" => 1, "max" => 4} },
          "Tenth grade" => { "Homework" => {"min" => 1, "max" => 4} }
        },
        'INCIDENTS_PER_SECTION' => 2,
        'BEHAVIORS' => [ { short: "Fighting", desc: "Fighting with another student", category: "School Violation" }]
      }}
  let(:config) {YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))}
  let(:session1) {{'year' => 2001, 'interval' => DateInterval.new(Date.new(2001), Date.new(2002), 180), 'name'=>'session1'}}
  let(:session2) {{'year' => 2002, 'interval' => DateInterval.new(Date.new(2002), Date.new(2003), 180), 'name'=>'session2'}}
  let(:offerings) {[{'id' => 1, 'grade' => :NINTH_GRADE, 'ed_org_id'=>'42', 'session'=>session1}, 
        {'id' => 2, 'grade' => :TENTH_GRADE, 'ed_org_id'=>'42', 'session'=>session1}, 
        {'id' => 3, 'grade' => :TENTH_GRADE, 'ed_org_id'=>'42', 'session'=>session1}]}
  let(:ed_org) {{'id' => 42, 'students' => {2001 => {:NINTH_GRADE => 30, :TENTH_GRADE => 0}, 2002 => {:NINTH_GRADE => 0, :TENTH_GRADE => 30}},
                             'offerings' => {2001 => offerings, 2002 => offerings},
                             'sessions' => [session1, session2],
                             'teachers' => []}}
  let(:world) {{ "high" => [ed_org] }}
  let(:prng) {Random.new(config['seed'])}
  let(:factory) {SectionWorkOrderFactory.new(world, scenario, prng)}

  describe "request to create sections --> with a simple edorg and scenario" do

    before(:all) do
      factory.generate_sections_with_teachers(ed_org, "high")
    end

    it "will return enough sections so that each student can take it" do
      ninth_grade_sections = factory.sections(ed_org['id'], "high", 2001, :NINTH_GRADE)
      ninth_grade_sections[1].count.should eq 3
      
      tenth_grade_sections = factory.sections(ed_org['id'], 'high', 2002, :TENTH_GRADE)
      tenth_grade_sections[2].count.should eq 3
      tenth_grade_sections[3].count.should eq 3
    end

    it "will return no sections when there are no available students" do
      ninth_grade_sections = factory.sections(ed_org['id'], 'high', 2002, :NINTH_GRADE)
      ninth_grade_sections.should be_empty
      
      tenth_grade_sections = factory.sections(ed_org['id'], 'high', 2001, :TENTH_GRADE)
      tenth_grade_sections.should be_empty
    end

  end

  describe "generate_sections_with_teachers" do
    let(:results) {Enumerator.new{|y| factory.generate_sections_with_teachers(ed_org, "high", y)}}
    let(:incidents) {results.select{|r| r.is_a? DisciplineIncident}}
    let(:sections) {results.select{|r| r.is_a? Hash and r[:type] == Section}}

    it "will create the correct number of discipline incidents" do
      incidents.count.should eq (2 * sections.count)
    end

  end
end
