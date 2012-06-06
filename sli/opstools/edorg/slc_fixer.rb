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
      x.report("Attendance")    {@threads << Thread.new {fix_attendance}}
      x.report("Assessments")   {@threads << Thread.new {fix_assessments}}
      x.report("Discipline")    {@threads << Thread.new {fix_disciplines}}
      x.report("Parents")       {@threads << Thread.new {fix_parents}}
      x.report("Report Card")   {@threads << Thread.new {fix_report_card}}
      x.report("Programs")      {@threads << Thread.new {fix_programs}}
      x.report("Courses")       {@threads << Thread.new {fix_courses}}
      x.report("Miscellaneous") {@threads << Thread.new {fix_miscellany}}
      x.report("Cohorts")       {@threads << Thread.new {fix_cohorts}}
      x.report("Grades")        {@threads << Thread.new {fix_grades}}
      x.report("Sessions")      {@threads << Thread.new {fix_sessions}}
      x.report("Staff")         {@threads << Thread.new {fix_staff}}
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
      edorgs << student['body']['schoolId'] unless student['body'].has_key? 'exitWithrdrawDate' and Date.parse(student['body']['exitWithdrawDate']) <= Date.today
      edorgs << old unless old.empty?
      edorgs = edorgs.flatten.uniq.sort
      if !edorgs.eql? old
        @student_hash[student['body']['studentId']] = edorgs
        stamp_id(@students, student['body']['studentId'], edorgs)
        stamp_id(ssa, student['_id'], student['body']['schoolId'])
      end
    end
  end
  
  def fix_sections
    sections = @db['section']
    sections.find.each do |section|
      edorgs = section['body']['schoolId']
      stamp_id(sections, section['_id'], edorgs)
      @db['teacherSectionAssociation'].find({"body.sectionId" => section['_id']}).each { |assoc| stamp_id(@db['teacherSectionAssociation'], assoc['_id'], edorgs) }
      @db['sectionAssessmentAssociation'].find({"body.sectionId" => section['_id']}).each { |assoc| stamp_id(@db['sectionAssessmentAssociation'], assoc['_id'], edorgs) }
    end
    @student_hash.each do |k, v|
      @db['studentSectionAssociation'].find({'body.studentId' => k}).each do |section|
        stamp_id(db['studentSectionAssociation'], section['_id'], v)
      end
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
      edorg = @db['program'].find_one({"_id" => program['body']['programId'][0]})['metaData']['edOrgs']
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
      edorg = @db['cohort'].find_one({'_id' => cohort['body']['cohortId']})['metaData']['edOrgs']
      stamp_id(@db['studentCohortAssociation'], cohort['_id'], edorg)
    end
    @db['staffCohortAssociation'].find.each do |cohort|
      edorg = @db['cohort'].find_one({'_id' => cohort['body']['cohortId'][0]})['metaData']['edOrgs']
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
      old = old_edorgs(@db['staff'], staff['staffReference'])
      edorg = staff['body']['educationOrganizationReference']
      stamp_id(sea, staff['_id'], edorg)
      stamp_id(@db['staff'], staff['staffReference'], old.merge(edorg).flatten.uniq)
    end
    #This needed?
    tsa = @db['teacherSchoolAssociation']
    tsa.find.each do |teacher|
      stamp_id(tsa, teacher['_id'], teacher['body']['schoolId'])
    end
  end

  def fix_grades
    @db['gradebookEntry'].find.each do |grade|
      edorg = @db['section'].find_one({"_id" => grade['body']['sectionId']})['metaData']['edOrgs']
      stamp_id(@db['gradebookEntry'], grade['_id'], edorg)
    end
    #Grades and grade period
    @db['grade'].find.each do |grade|
      edorg = @db['studentSectionAssociation'].find_one({"_id" => grade['body']['studentSectionAssociationId']})['metaData']['edOrgs']
      stamp_id(@db['grade'], grade['_id'], edorg)
      stamp_id(@db['gradingPeriod'], grade['body']['gradingPeriodId'], edorg)
    end
  end

  def fix_courses
    csa = @db['courseSectionAssociation']
    csa.find.each do |course|
      edorg = @db['section'].find_one({'_id' => course['body']['sectionId']})['metaData']['edOrgs']
      stamp_id(csa, course['_id'], edorg)
      stamp_id(@db['course'], course['body']['courseId'], edorg)
      stamp_id(@db['courseOffering'], course['body']['courseId'], edorg)
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
      edorg = @db['studentSectionAssociation'].find_one({'_id' => student['body']['studentSectionAssociationId']})['metaData']['edOrgs']
      stamp_id(@db['studentCompetency'], student['_id'], edorg)
    end
  end
  
  private
  def stamp_id(collection, id, edOrg)
    if edOrg.nil?
      return
    end
    collection.update({"_id" => id}, {"$set" => {"metaData.edOrgs" => edOrg}})
    @count += 1
  end

  def student_edorgs(id)
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
    old
  end
end