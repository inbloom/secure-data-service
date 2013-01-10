require File.dirname(__FILE__) + '/../stamper'
require 'mongo'

describe Stamper::BaseStamper do
  before(:each) do
    @db = Mongo::Connection.new('localhost', 27017)['delta_spec']
    @stamper = Stamper::BaseStamper.new(@db, nil, nil)
  end
  describe "#stamp" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.stamp}.to raise_error "Not implemented"
    end
  end
  describe "#get_edorgs" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.get_edorgs}.to raise_error "Not implemented"
    end
  end
  describe "#get_teachers" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.get_teachers}.to raise_error "Not implemented"
    end
  end
  describe "#wrap_up" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.wrap_up}.to raise_error "Not implemented"
    end
  end
end
describe Stamper::StudentStamper do
  before(:each) do
    @students = []
    @connection = Mongo::Connection.new('localhost', 27017)
    @db = @connection['delta_spec']
    @student = @db['student'].insert({"body" => {"name" => "Student 1"},"metaData" => {"tenantId" => "test"}})
    @stamper = Stamper::StudentStamper.new(@db, @student, 'test')
    @expired_date = (Date.today - (@stamper.grace_period + 1)).to_time.utc.to_s
    @valid_date = (Date.today - (@stamper.grace_period - 1)).to_time.utc.to_s
  end
  after(:each) do
    @connection.drop_database 'delta_spec'
  end
  describe "#stamp" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.stamp}.to raise_error "Not implemented"
    end
  end
  describe "#get_edorgs" do
    before(:each) do
      @school = @db['educationOrganization'].insert({"body" => {"name" => "TestSchool"}, "metaData" => {"tenantId" => "test"}})
    end
    it "should find edorgs for valid studentSchoolAssociations" do
      @db['studentSchoolAssociation'].insert({"body" => {"schoolId" => @school, "studentId" => @student}, "metaData" => {"tenantId" => "test"}})
      edorgs = @stamper.get_edorgs
      edorgs.size.should eql 1
      edorgs.should include @school
    end
    it "should find edorgs for multiple studentSchoolAssociations" do
      school = @db['educationOrganization'].insert({"body" => {"name" => "TestSchool 1"}, "metaData" => {"tenantId" => "test"}})
      @db['studentSchoolAssociation'].insert({"body" => {"schoolId" => @school, "studentId" => @student}, "metaData" => {"tenantId" => "test"}})
      @db['studentSchoolAssociation'].insert({"body" => {"schoolId" => school, "studentId" => @student}, "metaData" => {"tenantId" => "test"}})
      edorgs = @stamper.get_edorgs
      edorgs.size.should eql 2
      edorgs.should include @school
      edorgs.should include school
    end
    it "should respect end dates " do
      @db['studentSchoolAssociation'].insert({"body" => {"schoolId" => @school, "studentId" => @student, "exitWithdrawDate" => @expired_date}, "metaData" => {"tenantId" => "test"}})
      edorgs = @stamper.get_edorgs
      edorgs.size.should eql 0
      edorgs.should_not include @school
    end
  end
  describe "#get_teachers" do
    before(:each) do
      @section = @db['section'].insert({"body" => {"name" => "A section"}, "metaData" => {"tenantId" => "test"}})
      @cohort = @db['cohort'].insert({"body" => {"name" => "A cohort"}, "metaData" => {"tenantId" => "test"}})
      @program = @db['program'].insert({"body" => {"name" => "A program"}, "metaData" => {"tenantId" => "test"}})
      @staff = @db['staff'].insert({"body" => {"name" => "A staff"}, "metaData" => {"tenantId" => "test"}})
    end
    it "should find teachers through a valid section" do
      @db['studentSectionAssociation'].insert({"body"=> {"studentId" => @student, "sectionId" => @section}, "metaData" => {"tenantId" => "test"}})
      @db['teacherSectionAssociation'].insert({"body"=> {"teacherId" => @staff, "sectionId" => @section}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 1
      teachers.should include @staff
    end
    it "should not find teachers through an expired student section association" do
      @db['studentSectionAssociation'].insert({"body"=> {"studentId" => @student, "sectionId" => @section, "endDate" => @expired_date}, "metaData" => {"tenantId" => "test"}})
      @db['teacherSectionAssociation'].insert({"body"=> {"teacherId" => @staff, "sectionId" => @section}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
    it "should not find teachers through an expired teacher section association" do
      @db['studentSectionAssociation'].insert({"body"=> {"studentId" => @student, "sectionId" => @section}, "metaData" => {"tenantId" => "test"}})
      @db['teacherSectionAssociation'].insert({"body"=> {"teacherId" => @staff, "sectionId" => @section, "endDate" => @expired_date}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
    it "should find teachers through section associations within the date" do
      @db['studentSectionAssociation'].insert({"body"=> {"studentId" => @student, "sectionId" => @section, "endDate" => @valid_date}, "metaData" => {"tenantId" => "test"}})
      @db['teacherSectionAssociation'].insert({"body"=> {"teacherId" => @staff, "sectionId" => @section, "endDate" => @valid_date}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 1
      teachers.should include @staff
    end
    it "should find teachers through a valid cohort" do
      @db['studentCohortAssociation'].insert({"body"=> {"studentId" => @student, "cohortId" => @cohort}, "metaData" => {"tenantId" => "test"}})
      @db['staffCohortAssociation'].insert({"body"=> {"staffId" => [@staff], "cohortId" => @cohort, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 1
      teachers.should include @staff
    end
    it "should not find teachers through an expired student cohort association" do
      @db['studentCohortAssociation'].insert({"body"=> {"studentId" => @student, "cohortId" => @cohort, "endDate" => @expired_date}, "metaData" => {"tenantId" => "test"}})
      @db['staffCohortAssociation'].insert({"body"=> {"staffId" => [@staff], "cohortId" => @cohort, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
    it "should not find teachers through an expired teacher cohort association" do
      @db['studentCohortAssociation'].insert({"body"=> {"studentId" => @student, "cohortId" => @cohort}, "metaData" => {"tenantId" => "test"}})
      @db['staffCohortAssociation'].insert({"body"=> {"staffId" => [@staff], "cohortId" => @cohort, "endDate" => @expired_date, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
    it "should find teachers through cohort associations within the date" do
      @db['studentCohortAssociation'].insert({"body"=> {"studentId" => @student, "cohortId" => @cohort, "endDate" => (Date.today + @stamper.grace_period).to_time.utc.to_s}, "metaData" => {"tenantId" => "test"}})
      @db['staffCohortAssociation'].insert({"body"=> {"staffId" => [@staff], "cohortId" => @cohort, "endDate" => (Date.today + @stamper.grace_period).to_time.utc.to_s, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 1
      teachers.should include @staff
    end
    it "should find teachers through a valid program" do
      @db['studentProgramAssociation'].insert({"body"=> {"studentId" => @student, "programId" => @program}, "metaData" => {"tenantId" => "test"}})
      @db['staffProgramAssociation'].insert({"body"=> {"staffId" => [@staff], "programId" => @program, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 1
      teachers.should include @staff
    end
    it "should not find teachers through an expired student program association" do
      @db['studentProgramAssociation'].insert({"body"=> {"studentId" => @student, "programId" => @program, "endDate" => @expired_date}, "metaData" => {"tenantId" => "test"}})
      @db['staffProgramAssociation'].insert({"body"=> {"staffId" => [@staff], "programId" => @program, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
    it "should not find teachers through an expired teacher program association" do
      @db['studentProgramAssociation'].insert({"body"=> {"studentId" => @student, "programId" => @program}, "metaData" => {"tenantId" => "test"}})
      @db['staffProgramAssociation'].insert({"body"=> {"staffId" => [@staff], "programId" => @program, "endDate" => @expired_date, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
    it "should find teachers through program associations within the date" do
      @db['studentProgramAssociation'].insert({"body"=> {"studentId" => @student, "programId" => @program, "endDate" => (Date.today + @stamper.grace_period).to_time.utc.to_s}, "metaData" => {"tenantId" => "test"}})
      @db['staffProgramAssociation'].insert({"body"=> {"staffId" => [@staff], "programId" => @program, "endDate" => (Date.today + @stamper.grace_period).to_time.utc.to_s, "studentRecordAccess" => true}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 1
      teachers.should include @staff
    end
    it "should not find teachers through program associations within the date but access is false" do
      @db['studentProgramAssociation'].insert({"body"=> {"studentId" => @student, "programId" => @program, "endDate" => (Date.today + @stamper.grace_period).to_time.utc.to_s}, "metaData" => {"tenantId" => "test"}})
      @db['staffProgramAssociation'].insert({"body"=> {"staffId" => [@staff], "programId" => @program, "endDate" => (Date.today + @stamper.grace_period).to_time.utc.to_s, "studentRecordAccess" => false}, "metaData" => {"tenantId" => "test"}})
      teachers = @stamper.get_teachers
      teachers.size.should eql 0
      teachers.should_not include @staff
    end
  end
  describe "#wrap_up" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.wrap_up}.to raise_error "Not implemented"
    end
  end
end
