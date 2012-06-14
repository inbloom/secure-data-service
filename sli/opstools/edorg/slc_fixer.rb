require 'rubygems'
require 'mongo'
require "benchmark"
require "set"
require 'date'

class SLCFixer
  attr_accessor :count, :db
  def initialize(db)
    @db = db
    @students = @db['student']
    @student_hash = {}
    @count = 0
    @basic_options = {:timeout => false, :batch_size => 100}
  end

  def start
    time = Time.now
    @threads = []
    Benchmark.bm(20) do |x|
      x.report("Students")      {fix_students}
      x.report("Sections")      {fix_sections}
      x.report("Staff")         {fix_staff}
      @threads << Thread.new {x.report("Attendance")    {fix_attendance}}
      @threads << Thread.new {x.report("Assessments")   {fix_assessments}}
      @threads << Thread.new {x.report("Discipline")    {fix_disciplines}}
      @threads << Thread.new {x.report("Parents")       {fix_parents}}
      @threads << Thread.new {x.report("Report Card")   {fix_report_card}}
      @threads << Thread.new {x.report("Programs")      {fix_programs}}
      @threads << Thread.new {x.report("Courses")       {fix_courses}}
      @threads << Thread.new {x.report("Miscellaneous") {fix_miscellany}}
      @threads << Thread.new {x.report("Cohorts")       {fix_cohorts}}
      @threads << Thread.new {x.report("Grades")        {fix_grades}}
      @threads << Thread.new {x.report("Sessions")      {fix_sessions}}
    end
    @threads.each do |th|
      th.join
    end
    finalTime = Time.now - time
    puts "\t Final time is #{finalTime} secs"
    puts "\t Documents(#{@count}) per second #{@count/finalTime}"
  end


  def fix_students
    @db['studentSchoolAssociation'].find({}, @basic_options) do |cur|
      cur.each do |student|
        edorgs = []
        old = old_edorgs(@students, student['body']['studentId'])
        edorgs << student['body']['schoolId'] unless student['body'].has_key? 'exitWithdrawDate' and Date.parse(student['body']['exitWithdrawDate']) <= Date.today - 2000
        edorgs << old unless old.empty?
        edorgs = edorgs.flatten.uniq.sort
        stamp_id(@db['studentSchoolAssociation'], student['_id'], student['body']['schoolId'])
        @student_hash[student['body']['studentId']] = edorgs
        stamp_id(@students, student['body']['studentId'], edorgs)
      end
    end
  end

  def fix_sections
    @db['section'].find({}, @basic_options) do |cur|
      cur.each do |section|
        edorgs = section['body']['schoolId']
        stamp_id(@db['section'], section['_id'], edorgs)
        @db['teacherSectionAssociation'].find({"body.sectionId"    => section['_id']}, @basic_options) do |scur| 
          scur.each {|assoc| stamp_id(@db['teacherSectionAssociation'], assoc['_id'], edorgs)}
        end
            
        @db['sectionAssessmentAssociation'].find({"body.sectionId" => section['_id']}, @basic_options) do |scur| 
          scur.each {|assoc| stamp_id(@db['sectionAssessmentAssociation'], assoc['_id'], edorgs) }
        end
        @db['studentSectionAssociation'].find({'body.sectionId' => section['_id']}, @basic_options) do |scur|
          scur.each { |assoc| stamp_id(@db['studentSectionAssociation'], assoc['_id'], ([] << edorgs << student_edorgs(assoc['body']['studentId'])).flatten.uniq) }
        end
      end
    end
    @db['sectionSchoolAssociation'].find({}, @basic_options) do |cur|
      cur.each { |assoc| stamp_id(@db['sectionSchoolAssociation'], assoc['_id'], assoc['body']['schoolId']) }
    end
  end

  def fix_attendance
    @db['attendance'].find({}, @basic_options) do |cur|
      cur.each do |attendance|
        edOrg = student_edorgs(attendance['body']['studentId'])
        stamp_id(@db['attendance'], attendance['_id'], edOrg)
      end
    end
  end

  def fix_assessments
    @db['studentAssessmentAssociation'].find({}, @basic_options) do |cur|
      cur.each do |assessment|
        edOrg = []
        student_edorg = student_edorgs(assessment['body']['studentId'])
        old_edorg = old_edorgs(@db['assessment'], assessment['body']['assessmentId'])
        edOrg << student_edorg << old_edorg
        edOrg = edOrg.flatten.uniq
        stamp_id(@db['studentAssessmentAssociation'], assessment['_id'], edOrg)
        stamp_id(@db['assessment'], assessment['body']['assessmentId'], edOrg)
      end
    end
    @db['sectionAssessmentAssociation'].find({}, @basic_options) do |cur|
      cur.each do |assessment|
        edorgs = []
        assessment_edOrg = old_edorgs(@db['assessment'], assessment['body']['assessmentId'])
        section_edorg = old_edorgs(@db['section'], assessment['body']['sectionId'])
        edorgs << assessment_edOrg << section_edorg
        edorgs = edorgs.flatten.uniq
        stamp_id(@db['assessment'], assessment['body']['assessmentId'], edorgs)
        stamp_id(@db['sectionAssessmentAssociation'], assessment['_id'], section_edorg)
      end
    end
  end

  def fix_disciplines
    @db['disciplineAction'].find({}, :timeout => false) do |cur|
      cur.each do |action|
        edorg = student_edorgs(action['body']['studentId'])
        stamp_id(@db['disciplineAction'], action['_id'], edorg)
      end
    end
    @db['studentDisciplineIncidentAssociation'].find({}, :timeout => false) do |cur|
      cur.each do |incident|
        edorg = student_edorgs(incident['body']['studentId'])
        stamp_id(@db['studentDisciplineIncidentAssociation'], incident['_id'], edorg)
        stamp_id(@db['disciplineIncident'], incident['body']['disciplineIncidentId'], edorg)
      end
    end
    @db['disciplineIncident'].find({}, :timeout => false) do |cur|
      cur.each do |discipline|
        edorgs = []
        edorgs << dig_edorg_out(discipline)
        edorgs << discipline['body']['schoolId']
        edorgs = edorgs.flatten.uniq
        stamp_id(@db['disciplineIncident'], discipline['_id'], edorgs)
      end
    end
  end

  def fix_parents
    @db['studentParentAssociation'].find({}, @basic_options) do |cur|
      cur.each do |parent|
        edorg = student_edorgs(parent['body']['studentId'])
        stamp_id(@db['studentParentAssociation'], parent['_id'], edorg)
        stamp_id(@db['parent'], parent['body']['parentId'], edorg)
      end
    end
  end

  def fix_report_card
    @db['reportCard'].find({}, @basic_options) do |cur|
      cur.each do |card|
        edorg = student_edorgs(card['body']['studentId'])
        stamp_id(@db['reportCard'], card['_id'], edorg)
      end
    end
  end

  def fix_programs
    @db['studentProgramAssociation'].find({}, @basic_options) do |cur|
      cur.each do |program|
        edorg = student_edorgs(program['body']['studentId'])
        stamp_id(@db['studentProgramAssociation'], program['_id'], program['body']['educationOrganizationId'])
        stamp_id(@db['program'], program['body']['programId'], program['body']['educationOrganizationId'])
      end
    end
    @db['staffProgramAssociation'].find({}, @basic_options) do |cur|
      cur.each do |program|
        edorg = []
        program_edorg = old_edorgs(@db['program'], program['body']['programId'])
        staff_edorg = old_edorgs(@db['staff'], program['body']['staffId'])
        edorg << program_edorg << staff_edorg
        edorg = edorg.flatten.uniq 
        stamp_id(@db['program'], program['body']['porgramId'], edorg)
        stamp_id(@db['staffProgramAssociation'], program['_id'], staff_edorg)
      end
    end
  end

  def fix_cohorts
    @db['cohort'].find({}, @basic_options) do |cur|
      cur.each do |cohort|
        stamp_id(@db['cohort'], cohort['_id'], cohort['body']['educationOrgId'])
      end
    end
    @db['studentCohortAssociation'].find({}, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = old_edorgs(@db['student'], cohort['body']['studentId'])
        stamp_id(@db['studentCohortAssociation'], cohort['_id'], edorg)
      end
    end
    @db['staffCohortAssociation'].find({}, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = []
        edorg << old_edorgs(@db['cohort'], cohort['body']['cohortId'])
        edorg << old_edorgs(@db['staff'], cohort['body']['staffId'])
        edorg = edorg.flatten.uniq
        stamp_id(@db['staffCohortAssociation'], cohort['_id'], edorg)
        stamp_id(@db['cohort'], cohort['body']['cohortId'], edorg)
      end
    end
  end

  def fix_sessions
    @db['session'].find({}, @basic_options) do |cur|
      cur.each do |session|
        edorg = []
        @db['section'].find({"body.sessionId" => session["_id"]}, @basic_options) do |scur| 
          scur.each do |sec|
            edorg << sec['metaData']['edOrgs'] unless sec['metaData'].nil? 
          end
        end
        edorg = edorg.flatten.uniq
        stamp_id(@db['session'], session['_id'], edorg)
        @db['schoolSessionAssociation'].find({"body.sessionId" => session['_id']}, @basic_options) do |scur| 
          scur.each do |assoc|
            stamp_id(@db['schoolSessionAssociation'], assoc['_id'], edorg)
          end
        end
        gradingPeriodReferences = session['body']['gradingPeriodReference']
        unless gradingPeriodReferences.nil?
          gradingPeriodReferences.each do |gradingPeriodRef|
            old = old_edorgs(@db['gradingPeriod'], gradingPeriodRef)
            value = (old << edorg).flatten.uniq	      
            stamp_id(@db['gradingPeriod'], gradingPeriodRef, value)
          end
        end
      end
    end
  end

  def fix_staff
    @db['staffEducationOrganizationAssociation'].find({}, @basic_options) do |cur|
      cur.each do |staff|
        old = old_edorgs(@db['staff'], staff['body']['staffReference'])
        edorg = staff['body']['educationOrganizationReference']
        stamp_id(@db['staffEducationOrganizationAssociation'], staff['_id'], edorg)
        stamp_id(@db['staff'], staff['body']['staffReference'], (old << edorg).flatten.uniq)
      end
    end
    #This needed?
    @db['teacherSchoolAssociation'].find({}, @basic_options) do |cur|
      cur.each do |teacher|
        old = old_edorgs(@db['staff'], teacher['body']['teacherId'])
        stamp_id(@db['teacherSchoolAssociation'], teacher['_id'], teacher['body']['schoolId'])
        stamp_id(@db['staff'], teacher['body']['teacherId'], (old << teacher['body']['schoolId']).flatten.uniq)
      end
    end
  end

  def fix_grades
    @db['gradebookEntry'].find({}, @basic_options) do |cur|
      cur.each do |grade|
        edorg = old_edorgs(@db['section'], grade['body']['sectionId'])
        stamp_id(@db['gradebookEntry'], grade['_id'], edorg)
      end
    end
    #Grades and grade period
    @db['grade'].find({}, @basic_options) do |cur|
      cur.each do |grade|
        edorg = old_edorgs(@db['studentSectionAssociation'], grade['body']['studentSectionAssociationId'])
        stamp_id(@db['grade'], grade['_id'], edorg)
        #      stamp_id(@db['gradingPeriod'], grade['body']['gradingPeriodId'], edorg)
      end
    end
  end

  def fix_courses
    @db['section'].find({}, @basic_options) do |cur|
      cur.each do |section|
        edorg = section['metaData']['edOrgs']
        stamp_id(@db['course'], section['body']['courseId'], edorg)
      end
    end
    @db['courseOffering'].find({}, @basic_options) do |cur|
      cur.each do |course|
        edorgs = []
        edorgs << old_edorgs(@db['course'], course['body']['courseId'])
        edorgs << old_edorgs(@db['session'], course['body']['sessionId'])
        edorgs = edorgs.flatten.uniq
        stamp_id(@db['courseOffering'], course['_id'], edorgs)
      end
    end
  end

  def fix_miscellany
    #StudentTranscriptAssociation
    @db['studentTranscriptAssociation'].find({}, @basic_options) do |cur|
      cur.each do |trans|
        edorg = []
        edorg << old_edorgs(@db['studentTranscriptAssociation'], trans['_id'])	  
        edorg << student_edorgs(trans['body']['studentId'])

        @db['studentAcademicRecord'].find({"_id" => trans['body']['studentAcademicRecordId']}, @basic_options) do |scur|
          scur.each do |sar|
            studentId = sar['body']['studentId']
            edorg << student_edorgs(studentId)        
          end
        end
        edorg = edorg.flatten.uniq
        stamp_id(@db['studentTranscriptAssociation'], trans['_id'], edorg)
      end
    end

    #StudentSectionGradeBook
    @db['studentSectionGradebookEntry'].find({}, @basic_options) do |cur|
      cur.each do |trans|
        edorg = student_edorgs(trans['body']['studentId'])
        stamp_id(@db['studentSectionGradebookEntry'], trans['_id'], edorg)
      end
    end

    #Student Compentency
    @db['studentCompetency'].find({}, @basic_options) do |cur|
      cur.each do |student|
        edorg = old_edorgs(@db['studentSectionAssociation'], student['body']['studentSectionAssociationId'])
        stamp_id(@db['studentCompetency'], student['_id'], edorg)
      end
    end

    #Student Academic Record
    @db['studentAcademicRecord'].find({}, @basic_options) do |cur|
      cur.each do |student|
        edorg = student_edorgs(student['body']['studentId'])
        stamp_id(@db['studentAcademicRecord'], student['_id'], edorg)
      end
    end
  end

  private
  def edorg_digger(id)
    edorgs = []
    []
  end
  def stamp_id(collection, id, edOrg)
    if edOrg.nil? or edOrg.empty?
      return
    end
    collection.update({"_id" => id}, {"$set" => {"metaData.edOrgs" => edOrg}})
    puts "Working in #{collection.name}" if @count % 200 == 0
    @count += 1
  end

  def student_edorgs(id)
    if id.is_a? Array
      temp = []
      id.each do |i|
        temp = (temp + @student_hash[i]) if @student_hash.has_key? i
      end
      return temp.flatten.uniq
    end
    return @student_hash[id] if @student_hash.has_key? id
    []
  end

  def old_edorgs(collection, id)
    if id.is_a? Array
      doc = collection.find_one({"_id" => {'$in' => id}})
    else
      doc = collection.find_one({"_id" => id})
    end
    dig_edorg_out doc
  end

  def dig_edorg_out(doc)
    begin
      old = doc['metaData']['edOrgs']
    rescue
      old = []      
    end
    return [] if old.nil?
    old
  end
end
