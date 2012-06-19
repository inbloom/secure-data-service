require File.dirname(__FILE__) + '/../teacher_fixer'

describe TeacherFixer do
  before(:each) do
    conn = Mongo::Connection.new('localhost', 27017)
    @db = conn['test']
    Object.send(:db, db)
  end
  describe "#fixing_students" do
    it "should stamp students with section associations" do
      teacher_id = @db['staff'].insert({:name => "Teacher"})
      5.times do |i|
        student_id = @db['student'].insert({:name => "Student #{i}"})
        section_id = @db['section'].insert({:name => "Section #{id}"})
        @db['teacherSectionAssociation'].insert({:sectionId => section_id, :teacherId => teacher_id})
        @db['studentSectionAssociation'].insert({:studentId => id, :sectionId => section_id})
      end
      Object.send(:fix_students)
      
      @db['student'].find.each do |student| 
        student.should include 'metaData'
        student['metaData'].should include 'teacherId'
        student['metaData']['teacherId'].should eql teacher_id
      end
    end
    it "should stamp students with program assocations" do
      
    end
    it "should should stamp students with cohort associations" do
      
    end
    it "should respect end dates for associations" do
    end
  end
end
