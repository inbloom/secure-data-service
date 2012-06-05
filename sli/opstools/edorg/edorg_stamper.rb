require 'rubygems'
require 'mongo'
require "benchmark"
require "set"
require 'date'

class SLCFixer
  
  def initialize(hp)
    @connection = Mongo::Connection.new(hp[0], hp[1].to_i)
    @db = @connection['sli']
    @students = @db['student']
    @@count ||= 0
  end
  
  def start
    time = Time.now
    Benchmark.bm(20) do |x|
      x.report("Students") {fix_students}
      x.report("Sections") {fix_sections}
      x.report("Attendance") {fix_attendance}
      x.report("Assessments") {fix_assessments}
      x.report("Discipline") {fix_disciplines}
      x.report("Parents") {fix_parents}
      x.report("Report Card") {fix_report_card}
      x.report("Programs") {fix_programs}
      x.report("Courses") {fix_courses}
      x.report("Miscellaneous") {fix_miscellany}
      x.report("Cohorts") {fix_cohorts}
      x.report("Grades") {fix_grades}
      x.report("Sessions") {fix_sessions}
      x.report("Staff") {fix_staff}
    end
    finalTime = Time.now - time
    puts "\t Final time is #{finalTime} secs"
    puts "\t Records per second #{@@count/finalTime}"
  end

  def fix_students
    ssa = @db['studentSchoolAssociation']
    ssa.find.each do |student|
      edorgs = []
      old = student_edorgs(student['body']['studentId'])
      edorgs << old unless old.nil?
      edorgs << student['body']['schoolId'] unless student['body'].has_key? 'exitWithrdrawDate' and Date.parse(student['body']['exitWithdrawDate']) <= Date.today
      edorgs.flatten!.uniq!
      stamp_id(@students, student['body']['studentId'], edorgs)
      stamp_id(ssa, ssa['_id'], student['body']['schoolId'])
    end
  end

  def fix_attendance
    attendances = @db['attendance']
    attendances.find.each do |attendance|
      edOrg = student_edorgs(attendance['body']['studentId'])
      stamp_id(attendances, attendance['_id'], edOrg)
    end
  end

  def fix_sections
    ssa = @db['studentSectionAssociation']
    ssa.find.each do |section|
      edorgs = student_edorgs(section['body']['studentId'])
      stamp_id(ssa, section['_id'], edorgs)
      stamp_id(@db['section'], section['body']['sectionId'], edorgs)
      @db['teacherSectionAssociation'].find({"body.sectionId" => section['body']['sectionId']}).each { |assoc| stamp_id(@db['teacherSectionAssociation'], assoc['_id'], edorgs) }
      @db['sectionAssessmentAssociation'].find({"body.sectionId" => section['body']['sectionId']}).each { |assoc| stamp_id(@db['sectionAssessmentAssociation'], assoc['_id'], edorgs) }
    end
    @db['sectionSchoolAssociation'].find.each { |assoc| stamp_id(@db['sectionSchoolAssociation'], assoc['_id'], assoc['body']['schoolId']) }
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
      edorg = staff['body']['educationOrganizationReference']
      stamp_id(sea, staff['_id'], edorg)
      stamp_id(@db['staff'], staff['staffReference'], edorg)
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
    collection.update({"_id" => id}, {"$set" => {"metaData.edOrgs" => edOrg}})
    @@count += collection.count
  end

  def student_edorgs(id)
    student = @students.find_one({"_id" => id})
    return student['metaData']['edOrgs'] unless (student.nil? or student['metaData'].nil?)
    return []
  end
end



if ARGV.count < 1
  puts "Usage: edorg_stamper <dbhost:port>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  exit
end
hp = ARGV[0].split(":")
fixer = SLCFixer.new(hp)
fixer.start

