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

require_relative '../lib/EntityCreation/world_builder.rb'
require_relative '../lib/OutputGeneration/XmlDataWriter.rb'
require_relative '../lib/Shared/util.rb'

# specifications for the world builder
describe "WorldBuilder" do
  describe "--> with a set of 10 students" do
    describe "--> builds correct infrastructure such that" do
      # generate the data once
      before(:all) do
        configYAML   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
        scenarioYAML = load_scenario("10students", configYAML)
        rand         = Random.new(configYAML['seed'])
        writer       = XmlDataWriter.new(scenarioYAML)
        builder      = WorldBuilder.new(rand, scenarioYAML, writer)
        builder.build
        writer.finalize
      end

      # before each test: refresh the file handle for the education organization interchange
      before (:each) do
        @education_organization = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrganization.xml", "r")
        @education_org_calendar = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrgCalendar.xml", "r")
        @master_schedule        = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeMasterSchedule.xml", "r")
  	  end

      it "education organization interchange will contain a single state education agency" do
        @education_organization.readlines.select{|l| l.match("<StateEducationAgency>")}.length.should eq(1)
      end
      it "education organization interchange will contain a single local education agency" do
        @education_organization.readlines.select{|l| l.match("<LocalEducationAgency>")}.length.should eq(1)
      end
      it "education organization interchange will contain a single elementary school" do
        @education_organization.readlines.select{|l| l.match("<SchoolCategory>Elementary School</SchoolCategory>")}.length.should eq(1)
      end
      it "education organization interchange will contain a single middle school" do
        @education_organization.readlines.select{|l| l.match("<SchoolCategory>Middle School</SchoolCategory>")}.length.should eq(1)
      end
      it "education organization interchange will contain a single high school" do
        @education_organization.readlines.select{|l| l.match("<SchoolCategory>High School</SchoolCategory>")}.length.should eq(1)
      end
      it "education organization interchange will contain the correct number of courses" do
        @education_organization.readlines.select{|l| l.match("<Course>")}.length.should eq(34)
      end
      it "education organization calendar interchange will contain the correct number of sessions" do
        @education_org_calendar.readlines.select{|l| l.match("<Session>")}.length.should eq(3)
      end
      it "education organization calendar interchange will contain the correct number of grading periods" do
        @education_org_calendar.readlines.select{|l| l.match("<GradingPeriod>")}.length.should eq(9)
      end
      it "education organization calendar interchange will contain the correct number of calendar dates" do
        @education_org_calendar.readlines.select{|l| l.match("<CalendarDate>")}.length.should eq(580)
      end
      it "master schedule interchange will contain the correct number of course offerings" do
        @master_schedule.readlines.select{|l| l.match("<CourseOffering>")}.length.should eq(102)
      end
    end
  end

  describe "--> with a set of 10,001 students" do
    describe "--> builds correct infrastructure such that" do
      # generate the data once
      before(:all) do
        configYAML   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
        scenarioYAML = load_scenario("10001students", configYAML)
        rand         = Random.new(configYAML['seed'])
        writer       = XmlDataWriter.new(scenarioYAML)
        builder      = WorldBuilder.new(rand, scenarioYAML, writer)
        builder.build
        writer.finalize
      end

      # before each test: refresh the file handle for the education organization interchange
      before (:each) do
        @education_organization = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrganization.xml", "r")
        @education_org_calendar = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrgCalendar.xml", "r")
        @master_schedule        = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeMasterSchedule.xml", "r")
  	  end

      it "education organization interchange will contain a single state education agency" do
      	@education_organization.readlines.select{|l| l.match("<StateEducationAgency>")}.length.should eq(1)
	    end
	    it "education organization interchange will contain multiple local education agencies" do
	  	  @education_organization.readlines.select{|l| l.match("<LocalEducationAgency>")}.length.should be_> 2
	    end
	    it "education organization interchange will contain many schools" do
	  	  @education_organization.readlines.select{|l| l.match("<School>")}.length.should be_> 2
	    end
	    it "education organization interchange will contain many elementary schools" do
	  	  @education_organization.readlines.select{|l| l.match("<SchoolCategory>Elementary School</SchoolCategory>")}.length.should be_> 2
	    end
	    it "education organization interchange will contain many middle schools" do
	  	  @education_organization.readlines.select{|l| l.match("<SchoolCategory>Middle School</SchoolCategory>")}.length.should be_> 2
	    end
	    it "education organization interchange will contain many high schools" do
	  	  @education_organization.readlines.select{|l| l.match("<SchoolCategory>High School</SchoolCategory>")}.length.should be_> 2
	    end
      it "education organization interchange will contain the correct number of courses" do
        @education_organization.readlines.select{|l| l.match("<Course>")}.length.should eq(34)
      end
      it "education organization calendar interchange will contain the correct number of sessions" do
        @education_org_calendar.readlines.select{|l| l.match("<Session>")}.length.should eq(4)
      end
      it "education organization calendar interchange will contain the correct number of grading periods" do
        @education_org_calendar.readlines.select{|l| l.match("<GradingPeriod>")}.length.should eq(12)
      end
      it "education organization calendar interchange will contain the correct number of calendar dates" do
        @education_org_calendar.readlines.select{|l| l.match("<CalendarDate>")}.length.should eq(775)
      end
      it "master schedule interchange will contain the correct number of course offerings" do
        @master_schedule.readlines.select{|l| l.match("<CourseOffering>")}.length.should eq(166)
      end
    end
  end
  describe "#choose_feeders" do
    context "with 8 elementary schools, 4 middle schools, and 2 high schools" do
      let(:elementary) {(1..8).map{|i| {'id' => i}}}
      let(:middle) {(9..12).map{|i| {'id' => i}}}
      let(:high) {(13..14).map{|i| {'id' => i}}}
      before(:all) do
        WorldBuilder.choose_feeders(elementary, middle, high)
      end
      it "will give middle schools a single feeder high school" do
        middle.each{|i|
          i['feeds_to'].should  have(1).items
          i['feeds_to'][0].should satisfy {|i| 13 <= i and i <= 14}
        }
      end
      it "will give elementary schools a feeder middle school and high school" do
        puts elementary
        elementary.each{|i|
          i['feeds_to'].should  have(2).items
          i['feeds_to'][0].should satisfy {|i| 9 <= i and i <= 12}
          i['feeds_to'][1].should satisfy {|i| 13 <= i and i <= 14}
        }
      end
    end
  end
  describe "#get_students_per_grade" do
    let(:grades) {[:KINDERGARTEN, :FIRST_GRADE, :SECOND_GRADE, :THIRDGRADE]}
    it "will evenly distribute students into grades" do
      students_per_grade = WorldBuilder.get_students_per_grade(grades, 20)
      grades.each{|g| students_per_grade[g].should eq 5}
    end
    it "will always distribute all students" do
      (0..40).each{|i|
        students_per_grade = WorldBuilder.get_students_per_grade(grades, i)
        students_per_grade.values.inject(:+).should eq i
      }
    end
  end
end
