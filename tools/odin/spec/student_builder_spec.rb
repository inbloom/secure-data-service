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
#require_relative 'spec_helper'
require_relative '../lib/EntityCreation/student_builder.rb'
#require_relative '../lib/Shared/demographics.rb'


#Broken with recent refactor -- enable once everything is in place

describe "StudentBuilder" do
  context "Instantiate StudentBuilder and Student" do
    let(:work_order) {{:id => 41, :sessions => [{:school => 63, :sections => [{:id => 31, :edOrg => 63}]}]}}
    let(:birthday) {Date.new(2000, 9, 1)}
    let(:rand) {Random.new(work_order[:id])}
    let(:studentParent) {StringIO.new('', 'w')}
    let(:enrollment) {StringIO.new('', 'w')}
    let(:build) {StudentBuilder.new(work_order, birthday, {:studentParent => studentParent, :enrollment => enrollment})}
    
    it "Can access the init objects of the student_builder instance" do
      build.id.should eq(41)
      build.rand.should_not be_nil
      build.work_order.length.should eq(2)
      build.student_writer.should_not be_nil
      build.enrollment_writer.should_not be_nil
    end
    
    it "Can access demographics.state class variable" do
      build.state.should eq("NY")
    end
    
    it "will choose gender" do
      build.sex.should match(/^Male|^Female/)
    end
      
    it "will match the city in choices.yml" do
      build.city.should eq("New York")
    end

    it "will match the HARDCODED zip code"  do
      build.postalCode.should eq("10292")
    end

    it "should generate a first name" do
      build.firstName.should_not be_nil
    end
    
    it "should have a valid email that ends in fakemail.com" do
      build.email.should match(/.*?@fakemail.com/)
    end
    
    it "should have a login id that ends in fakemail.com" do
      build.loginId.should match (/.*?@fakemail.com/)
    end
    
    it "should have a valid address in demographics" do
      build.address.should match(/[0-9]+\s[a-z|A-z]+/)
    end

    it "should return a boolean value for disability" do
      build.disability.should_not be_nil
    end
    
    it "should return student id of 41" do
      build.id.should eq(41)
    end
    
    it "should choose the race of a student from list" do
      build.race.should_not be_nil
    end
    
    it "returns 55-65% white with white weighted distribution set to 60%" do
      grMethod = build.method(:race)
      build.distributionTester(grMethod, "White", 720, 780, 1000).should eq("true")
    end
      
    it "returns 15-25% hispanicLatino with true weight set to 20%" do
      hlMethod = build.method(:hispanicLatino)
      build.distributionTester(hlMethod, true, 150, 250, 1000).should eq("true")
    end
    
    it "returns 5-15% economicDisadvantaged with true weight set to 10%" do
      edMethod = build.method(:economicDisadvantaged)
      build.distributionTester(edMethod, true, 80, 120, 1000).should eq("true")
    end 
      
    it "returns 91-97% not limitedEnglish 'not limited' set to 94%" do
      leMethod = build.method(:limitedEnglish)
      build.distributionTester(leMethod, 'NotLimited', 910, 970, 1000).should eq("true")
    end
      
    it "returns 92-98% not disabled with weight set to 95%" do
      daMethod = build.method(:disability)
      build.distributionTester(daMethod, false, 920, 980, 1000).should eq("true")
    end  
      
    it "returns 68-72% schoolFood with 'Full price' set to 70%" do
      sfMethod = build.method(:schoolFood)
      build.distributionTester(sfMethod, 'Full price', 680, 720, 1000).should eq("true")
    end
    
    it "returns 1% Jr suffix" do
      suMethod = build.method(:suffix)
      build.distributionTester(suMethod, 'Jr', 1, 19, 1000).should eq("true")
    end
  end

=begin
 
  describe "#build" do
    context "With new student credentials" do
      let(:work_order) {{:id => 42, :sessions => [{:school => 64, :sections => [{:id => 32, :edOrg => 64},
                                                                                  {:id => 33, :edOrg => 64},
                                                                                  {:id => 34, :edOrg => 128}]},
                                                    {:school => 65, :sections => [{:id => 16, :edOrg => 65},
                                                                                  {:id => 17, :edOrg => 65}]}],
                                    :demographics => Demographics.new,
                                    :birth_day_after => Date.new(2000, 9, 1)}}
      let(:studentParent) {StringIO.new('', 'w')}
      let(:enrollment) {StringIO.new('', 'w')}
      let(:build) {StudentBuilder.new(work_order, {:studentParent => studentParent, :enrollment => enrollment})}
      before {builder.build}
      
      
      let(:new_id) {52}
      let(:newDemographics) {Demographics.new}
      let(:newRand) {Random.new(new_id)}
      let(:newBirthDay) {Date.new(1999, 1, 12)}
      let(:newStudent) {Student.new(new_id, newBirthDay, newDemographics, newRand)}      
 
    context 'With a simple work order' do
      let(:work_order) {{:id => 42, :sessions => [{:school => 64, :sections => [{:id => 32, :edOrg => 64},
                                                                                  {:id => 33, :edOrg => 64},
                                                                                  {:id => 34, :edOrg => 128}]},
                                                    {:school => 65, :sections => [{:id => 16, :edOrg => 65},
                                                                                  {:id => 17, :edOrg => 65}]}],
                                    :demographics => Demographics.new,
                                    :birth_day_after => Date.new(2000, 9, 1)}}
      let(:studentParent) {StringIO.new('', 'w')}
      let(:enrollment) {StringIO.new('', 'w')}
      let(:builder) {StudentBuilder.new(work_order, {:studentParent => studentParent, :enrollment => enrollment})}
      before {builder.build}

      it "will build student documents with the given student id" do
        studentParent.string.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      end

      it "will have the right number of schools associations" do
        enrollment.string.lines.select{|l| l.match('<StudentSchoolAssociation>')}.length.should eq(2)
      end

      it "will have the right number of section associations" do
        enrollment.string.lines.select{|l| l.match('<StudentSectionAssociation>')}.length.should eq(5)
      end

      it "will find a school with the correct school ids" do
        work_order[:sessions].each do |session|
          school_id = session[:school]
          enrollment.string.match("<StateOrganizationId>school#{school_id}</StateOrganizationId>").should_not be_nil
        end
      end
    end
  end
=end
end

