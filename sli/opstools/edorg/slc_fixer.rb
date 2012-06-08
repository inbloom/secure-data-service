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
  end
  
  def start
    time = Time.now
    @threads = []
    Benchmark.bm(20) do |x|
      x.report("Students")      {fix_students}
      x.report("Sections")      {fix_sections}
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
      @threads << Thread.new {x.report("Staff")         {fix_staff}}
    end
    @threads.each do |th|
      th.join
    end
    finalTime = Time.now - time
    puts "\t Final time is #{finalTime} secs"
    puts "\t Documents(#{@count}) per second #{@count/finalTime}"
  end
  

  def fix_students
    ssa = @db['studentSchoolAssociation']
    ssa.find.each do |student|
      edorgs = []
      old = old_edorgs(@students, student['body']['studentId'])
      edorgs << student['body']['schoolId'] unless student['body'].has_key? 'exitWithdrawDate' and Date.parse(student['body']['exitWithdrawDate']) <= Date.today - 2000
      edorgs << old unless old.empty?
      edorgs = edorgs.flatten.uniq.sort
      stamp_id(ssa, student['_id'], student['body']['schoolId'])
      if !edorgs.eql? old
        @student_hash[student['body']['studentId']] = edorgs
        stamp_id(@students, student['body']['studentId'], edorgs)
      end
    end
  end
  
  def fix_sections
    sections = @db['section']
    sections.find.each do |section|
      edorgs = section['body']['schoolId']
      stamp_id(sections, section['_id'], edorgs)
      @db['teacherSectionAssociation'].find({"body.sectionId"    => section['_id']}).each { |assoc| stamp_id(@db['teacherSectionAssociation'], assoc['_id'], edorgs) }
      @db['sectionAssessmentAssociation'].find({"body.sectionId" => section['_id']}).each { |assoc| stamp_id(@db['sectionAssessmentAssociation'], assoc['_id'], edorgs) }
      @db['studentSectionAssociation'].find({'body.sectionId' => section['_id']}).each { |assoc| stamp_id(@db['studentSectionAssociation'], assoc['_id'], edorgs) }
    end
    @db['sectionSchoolAssociation'].find.each { |assoc| stamp_id(@db['sectionSchoolAssociation'], assoc['_id'], assoc['body']['schoolId']) }
  end

  def fix_attendance
    attendances = @db['attendance']
    attendances.find.each do |attendance|
      edOrg = student_edorgs(attendance['body']['studentId'])
      stamp_id(attendances, attendance['_id'], edOrg)
    end
  end

  def fix_assessments
    saa = @db['studentAssessmentAssociation']
    saa.find.each do |assessment|
      edOrg = student_edorgs(assessment['body']['studentId'])
      stamp_id(saa, assessment['_id'], edOrg)
      stamp_id(@db['assessment'], @db['assessment'].find_one({"_id" => assessment['body']['assessmentId']})['_id'], edOrg)
    end
    ssa = @db['sectionAssessmentAssociation']
    ssa.find.each do |assessment|
      edorgs = []
      assessment_edOrg = old_edorgs(@db['assessment'], assessment['body']['assessmentId'])
      section_edorg = old_edorgs(@db['section'], assessment['body']['sectionId'])
      edorgs << assessment_edOrg << section_edorg
      edorgs = edorgs.flatten.uniq
      stamp_id(@db['assessment'], assessment['body']['assessmentId'], edorgs)
      stamp_id(ssa, assessment['_id'], edorgs)
    end
  end

  def fix_disciplines
    da = @db['disciplineAction']
    da.find.each do |action|
      edorg = student_edorgs(action['body']['studentId'])
      stamp_id(da, action['_id'], edorg)
    end
    dia = @db['studentDisciplineIncidentAssociation']
    dia.find.each do |incident|
      edorg = student_edorgs(incident['body']['studentId'])
      stamp_id(dia, incident['_id'], edorg)
      stamp_id(@db['disciplineIncident'], incident['body']['disciplineIncidentId'], edorg)
    end
  end

  def fix_parents
    spa = @db['studentParentAssociation']
    spa.find.each do |parent|
      edorg = student_edorgs(parent['body']['studentId'])
      stamp_id(spa, parent['_id'], edorg)
      stamp_id(@db['parent'], parent['body']['parentId'], edorg)
    end
  end

  def fix_report_card
    report_card = @db['reportCard']
    report_card.find.each do |card|
      edorg = student_edorgs(card['body']['studentId'])
      stamp_id(report_card, card['_id'], edorg)
    end
  end

  def fix_programs
    @start_time = Time.now
    spa = @db['studentProgramAssociation']
    spa.find.each do |program|
      edorg = student_edorgs(program['body']['studentId'])
      stamp_id(spa, program['_id'], edorg)
      stamp_id(@db['program'], program['body']['programId'], edorg)
    end
    spa = @db['staffProgramAssociation']
    spa.find.each do |program|
      edorg = old_edorgs(@db['program'], program['body']['programId'])
      stamp_id(spa, program['_id'], edorg)
    end
  end

  def fix_cohorts
    @start_time = Time.now
    cohorts = @db['cohort']
    cohorts.find.each do |cohort|
      stamp_id(cohorts, cohort['_id'], cohort['body']['educationOrgId'])
    end
    @db['studentCohortAssociation'].find.each do |cohort|
      edorg = old_edorgs(@db['cohort'], cohort['body']['cohortId'])
      stamp_id(@db['studentCohortAssociation'], cohort['_id'], edorg)
    end
    @db['staffCohortAssociation'].find.each do |cohort|
      edorg = old_edorgs(@db['cohort'], cohort['body']['cohortId'])
      stamp_id(@db['staffCohortAssociation'], cohort['_id'], edorg)
    end
  end

  def fix_sessions
    ssa = @db['schoolSessionAssociation']
    ssa.find.each do |session|
      edorg = session['body']['schoolId']
      stamp_id(ssa, session['_id'], edorg)
      stamp_id(@db['session'], session['body']['sessionId'], edorg)
    end
  end

  def fix_staff
    sea = @db['staffEducationOrganizationAssociation']
    sea.find.each do |staff|
      old = old_edorgs(@db['staff'], staff['body']['staffReference'])
      edorg = staff['body']['educationOrganizationReference']
      stamp_id(sea, staff['_id'], edorg)
      stamp_id(@db['staff'], staff['body']['staffReference'], (old << edorg).flatten.uniq)
    end
    #This needed?
    tsa = @db['teacherSchoolAssociation']
    tsa.find.each do |teacher|
      old = old_edorgs(@db['staff'], teacher['body']['teacherId'])
      stamp_id(tsa, teacher['_id'], teacher['body']['schoolId'])
      stamp_id(@db['staff'], teacher['body']['teacherId'], (old << teacher['body']['schoolId']).flatten.uniq)
    end
  end

  def fix_grades
    @db['gradebookEntry'].find.each do |grade|
      edorg = old_edorgs(@db['section'], grade['body']['sectionId'])
      stamp_id(@db['gradebookEntry'], grade['_id'], edorg)
    end
    #Grades and grade period
    @db['grade'].find.each do |grade|
      edorg = old_edorgs(@db['studentSectionAssociation'], grade['body']['studentSectionAssociationId'])
      stamp_id(@db['grade'], grade['_id'], edorg)
      stamp_id(@db['gradingPeriod'], grade['body']['gradingPeriodId'], edorg)
    end
  end

  def fix_courses
    sections = @db['section']
    sections.find.each do |section|
      edorg = section['metaData']['edOrgs']
      stamp_id(@db['course'], section['body']['courseId'], edorg)
    end
    co = @db['courseOffering']
    co.find.each do |course|
      stamp_id(co, course['_id'], old_edorgs(@db['course'], course['body']['courseId']))
    end
  end

  def fix_miscellany
    #StudentTranscriptAssociation
    @db['studentTranscriptAssociation'].find.each do |trans|
      edorg = student_edorgs(trans['body']['studentId'])
      stamp_id(@db['studentTranscriptAssociation'], trans['_id'], edorg)
    end
  
    #StudentSectionGradeBook
    @db['studentSectionGradebookEntry'].find.each do |trans|
      edorg = student_edorgs(trans['body']['studentId'])
      stamp_id(@db['studentSectionGradebookEntry'], trans['_id'], edorg)
    end
  
    #Student Compentency
    @db['studentCompetency'].find.each do |student|
      edorg = old_edorgs(@db['studentSectionAssociation'], student['body']['studentSectionAssociationId'])
      stamp_id(@db['studentCompetency'], student['_id'], edorg)
    end
    
    #Student Academic Record
    @db['studentAcademicRecord'].find.each do |student|
      edorg = student_edorgs(student['body']['studentId'])
      stamp_id(@db['studentAcademicRecord'], student['_id'], edorg)
    end
  end
  
  private
  def edorg_digger(id)
    edorgs = []
    edorgs << 
    []
  end
  def stamp_id(collection, id, edOrg)
    if edOrg.nil? or edOrg.empty?
      return
    end
    collection.update({"_id" => id}, {"$set" => {"metaData.edOrgs" => edOrg}})
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
    nil
  end
  
  def old_edorgs(collection, id)
    doc = collection.find_one({"_id" => id})
    begin
      old = doc['metaData']['edOrgs']
    rescue
      old = []      
    end
    return [] if old.nil?
    old
  end
end
