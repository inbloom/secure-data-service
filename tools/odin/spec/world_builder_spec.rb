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

# specifications for the world builder
describe "WorldBuilder" do
  describe "--> with a set of 10 students" do
    describe "--> builds correct infrastructure such that" do
      # generate the data once
      before(:all) do
  	    scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
        yaml = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/10students'))
        rand = Random.new(scenario['seed'])
        builder = WorldBuilder.new
        builder.build(rand, yaml)
  	  end

  	  # before each test: refresh the file handle for the education organization interchange
  	  before (:each) do
  	  	@education_organization = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrganization.xml", "r")
        @education_org_calendar = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrgCalendar.xml", "r")
  	  end

      it "education organization interchange will contain a single state education agency" do
      	@education_organization.readlines.select{|l| l.match("<StateEducationAgency>")}.length.should eq(1)
	    end
	    it "education organization interchange will contain two local education agencies" do
	  	  @education_organization.readlines.select{|l| l.match("<LocalEducationAgency>")}.length.should eq(2)
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
        @education_org_calendar.readlines.select{|l| l.match("<Session>")}.length.should eq(6)
      end
      it "education organization calendar interchange will contain the correct number of grading periods" do
        @education_org_calendar.readlines.select{|l| l.match("<GradingPeriod>")}.length.should eq(18)
      end
      it "education organization calendar interchange will contain the correct number of calendar dates" do
        @education_org_calendar.readlines.select{|l| l.match("<CalendarDate>")}.length.should eq(1161)
      end
    end
  end

  describe "--> with a set of 1,000,000 students" do
    describe "--> builds correct infrastructure such that" do
      # generate the data once
      before(:all) do
  	    scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
        yaml = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/1Mstudents'))
        rand = Random.new(scenario['seed'])
        builder = WorldBuilder.new
        builder.build(rand, yaml)
  	  end

  	  # before each test: refresh the file handle for the education organization interchange
  	  before (:each) do
  	  	@education_organization = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrganization.xml", "r")
        @education_org_calendar = File.new("#{File.dirname(__FILE__)}/../generated/InterchangeEducationOrgCalendar.xml", "r")
  	  end

      it "education organization interchange will contain a single state education agency" do
      	@education_organization.readlines.select{|l| l.match("<StateEducationAgency>")}.length.should eq(1)
	    end
	    it "education organization interchange will contain multiple local education agencies" do
	  	  @education_organization.readlines.select{|l| l.match("<LocalEducationAgency>")}.length.should eq(286)
	    end
	    it "education organization interchange will contain many schools" do
	  	  @education_organization.readlines.select{|l| l.match("<School>")}.length.should eq(1421)
	    end
	    it "education organization interchange will contain many elementary schools" do
	  	  @education_organization.readlines.select{|l| l.match("<SchoolCategory>Elementary School</SchoolCategory>")}.length.should eq(859)
	    end
	    it "education organization interchange will contain many middle schools" do
	  	  @education_organization.readlines.select{|l| l.match("<SchoolCategory>Middle School</SchoolCategory>")}.length.should eq(281)
	    end
	    it "education organization interchange will contain many high schools" do
	  	  @education_organization.readlines.select{|l| l.match("<SchoolCategory>High School</SchoolCategory>")}.length.should eq(281)
	    end
      it "education organization interchange will contain the correct number of courses" do
        @education_organization.readlines.select{|l| l.match("<Course>")}.length.should eq(34)
      end
      it "education organization calendar interchange will contain the correct number of sessions" do
        @education_org_calendar.readlines.select{|l| l.match("<Session>")}.length.should eq(858)
      end
      it "education organization calendar interchange will contain the correct number of grading periods" do
        @education_org_calendar.readlines.select{|l| l.match("<GradingPeriod>")}.length.should eq(2574)
      end
      it "education organization calendar interchange will contain the correct number of calendar dates" do
        @education_org_calendar.readlines.select{|l| l.match("<CalendarDate>")}.length.should eq(166050)
      end
    end
  end
end