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


require 'rubygems'
require 'mongo'
require "benchmark"
require "set"
require 'date'
require 'logger'

class SLCFixer
  attr_accessor :count, :db, :log, :parent_ed_org_hash
  def initialize(db, logger = nil)
    @db = db
    @students = @db['student']
    @student_hash = {}
    @count = 0
    @basic_options = {:timeout => false, :batch_size => 100}
    @log = logger || Logger.new(STDOUT)
    @parent_ed_org_hash = {}
    @tenant_to_ed_orgs = {}
  end

  def start
    time = Time.now
    @threads = []

    Benchmark.bm(20) do |x|
      x.report("Parent EdOrgs") {build_edorg_list}
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
    @log.info "Iterating studentSchoolAssociation with query {}"
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
    @log.info "Iterating sections with query: {}"
    @db['section'].find({}, @basic_options) do |cur|
      cur.each do |section|
        edorgs = section['body']['schoolId']
        stamp_id(@db['section'], section['_id'], edorgs)
        @log.info "Iterating teacherSectionAssociation with query: {'body.sectionId: #{section['_id']}}"
        @db['teacherSectionAssociation'].find({"body.sectionId"    => section['_id']}, @basic_options) do |scur| 
          scur.each {|assoc| stamp_id(@db['teacherSectionAssociation'], assoc['_id'], edorgs)}
        end
        @log.info "Iterating sectionAssessmentAssociation with query: {'body.sectionId: #{section['_id']}}"    
        @db['sectionAssessmentAssociation'].find({"body.sectionId" => section['_id']}, @basic_options) do |scur| 
          scur.each {|assoc| stamp_id(@db['sectionAssessmentAssociation'], assoc['_id'], edorgs) }
        end
        @log.info "Iterating studentSectionAssociation with query: {'body.sectionId: #{section['_id']}}"
        @db['studentSectionAssociation'].find({'body.sectionId' => section['_id']}, @basic_options) do |scur|
          scur.each { |assoc| stamp_id(@db['studentSectionAssociation'], assoc['_id'], ([] << edorgs << student_edorgs(assoc['body']['studentId'])).flatten.uniq) }
        end
      end
    end
    @log.info "Iterating teacherSectionAssociation with query: {}"
    @db['sectionSchoolAssociation'].find({}, @basic_options) do |cur|
      cur.each { |assoc| stamp_id(@db['sectionSchoolAssociation'], assoc['_id'], assoc['body']['schoolId']) }
    end
  end

  def fix_attendance
    @log.info "Iterating attendance with query: {}"
    @db['attendance'].find({}, @basic_options) do |cur|
      cur.each do |attendance|
        edOrg = student_edorgs(attendance['body']['studentId'])
        stamp_id(@db['attendance'], attendance['_id'], edOrg)
      end
    end
  end

  def fix_assessments
=begin
    @log.info "Iterating over assessments with query: {}"
    @db['assessment'].find({}, @basic_options) do |cur|
      cur.each do |assessment|
        tenant_id = assessment['metaData']['tenantId']
        stamp_id(@db['assessment'],assessment['_id'], @tenant_to_ed_orgs[tenant_id]) unless tenant_id.nil? || @tenant_to_ed_orgs[tenant_id].nil?
      end
    end
=end
    @log.info "Iterating studentAssessmentAssociation with query: {}"
    @db['studentAssessmentAssociation'].find({}, @basic_options) do |cur|
      cur.each do |studentAssessment|
        edOrg = []
        student_edorg = student_edorgs(studentAssessment['body']['studentId'])
        #old_edorg = old_edorgs(@db['assessment'], studentAssessment['body']['assessmentId'])
        edOrg << student_edorg #<< old_edorg
        edOrg = edOrg.flatten.uniq
        stamp_id(@db['studentAssessmentAssociation'], studentAssessment['_id'], edOrg)
        #stamp_id(@db['assessment'], assessment['body']['assessmentId'], edOrg)
      end
    end
  end

  def fix_disciplines
    @log.info "Iterating disciplineAction with query: {}"
    @db['disciplineAction'].find({}, :timeout => false) do |cur|
      cur.each do |action|
        edorg = student_edorgs(action['body']['studentId'])
        stamp_id(@db['disciplineAction'], action['_id'], edorg)
      end
    end
    @log.info "Iterating studentDisciplineIncidentAssociation with query: {}"
    @db['studentDisciplineIncidentAssociation'].find({}, :timeout => false) do |cur|
      cur.each do |incident|
        edorg = student_edorgs(incident['body']['studentId'])
        stamp_id(@db['studentDisciplineIncidentAssociation'], incident['_id'], edorg)
        stamp_id(@db['disciplineIncident'], incident['body']['disciplineIncidentId'], edorg)
      end
    end
    @log.info "Iterating disciplineIncident with query: {}"
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
    @log.info "Iterating studentParentAssociation with query: {}"
    @db['studentParentAssociation'].find({}, @basic_options) do |cur|
      cur.each do |parent|
        edorg = student_edorgs(parent['body']['studentId'])
        stamp_id(@db['studentParentAssociation'], parent['_id'], edorg)
        stamp_id(@db['parent'], parent['body']['parentId'], edorg)
      end
    end
  end

  def fix_report_card
    @log.info "Iterating reportCard with query: {}"
    @db['reportCard'].find({}, @basic_options) do |cur|
      cur.each do |card|
        edorg = student_edorgs(card['body']['studentId'])
        stamp_id(@db['reportCard'], card['_id'], edorg)
      end
    end
  end

  def fix_programs
    @log.info "Iterating studentProgramAssociation with query: {}"
    @db['studentProgramAssociation'].find({}, @basic_options) do |cur|
      cur.each do |program|
        edorg = student_edorgs(program['body']['studentId'])
        stamp_id(@db['studentProgramAssociation'], program['_id'], program['body']['educationOrganizationId'])
        stamp_id(@db['program'], program['body']['programId'], program['body']['educationOrganizationId'])
      end
    end
    @log.info "Iterating staffProgramAssociation with query: {}"
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
    @log.info "Iterating cohort with query: {}"
    @db['cohort'].find({}, @basic_options) do |cur|
      cur.each do |cohort|
        stamp_id(@db['cohort'], cohort['_id'], cohort['body']['educationOrgId'])
      end
    end
    @log.info "Iterating studentCohortAssociation with query: {}"
    @db['studentCohortAssociation'].find({}, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = old_edorgs(@db['student'], cohort['body']['studentId'])
        stamp_id(@db['studentCohortAssociation'], cohort['_id'], edorg)
      end
    end
    @log.info "Iterating staffCohortAssociation with query: {}"
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
    @log.info "Iterating session with query: {}"
    @db['session'].find({}, @basic_options) do |cur|
      cur.each do |session|
        edorg = []
        @log.info "Iterating section with query: {'body.sessionId': #{session['_id']}}"
        @db['section'].find({"body.sessionId" => session["_id"]}, @basic_options) do |scur| 
          scur.each do |sec|
            edorg << sec['metaData']['edOrgs'] unless sec['metaData'].nil? 
          end
        end
        edorg = edorg.flatten.uniq
        stamp_id(@db['session'], session['_id'], edorg)
        @log.info "Iterating schoolSessionAssociation with query: {'body.sessionId':#{session['_id']}}"
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
    @log.info "Iterating teacherSectionAssociation with query: {}"
    @db['staffEducationOrganizationAssociation'].find({}, @basic_options) do |cur|
      cur.each do |staff|
        old = old_edorgs(@db['staff'], staff['body']['staffReference'])
        edorg = staff['body']['educationOrganizationReference']
        stamp_id(@db['staffEducationOrganizationAssociation'], staff['_id'], edorg)
        stamp_id(@db['staff'], staff['body']['staffReference'], (old << edorg).flatten.uniq)
      end
    end
    #This needed?
    @log.info "Iterating teacherSectionAssociation with query: {}"
    @db['teacherSchoolAssociation'].find({}, @basic_options) do |cur|
      cur.each do |teacher|
        old = old_edorgs(@db['staff'], teacher['body']['teacherId'])
        stamp_id(@db['teacherSchoolAssociation'], teacher['_id'], teacher['body']['schoolId'])
        stamp_id(@db['staff'], teacher['body']['teacherId'], (old << teacher['body']['schoolId']).flatten.uniq)
      end
    end
  end

  def fix_grades
    @log.info "Iterating gradebookEntry with query: {}"
    @db['gradebookEntry'].find({}, @basic_options) do |cur|
      cur.each do |grade|
        edorg = old_edorgs(@db['section'], grade['body']['sectionId'])
        stamp_id(@db['gradebookEntry'], grade['_id'], edorg)
      end
    end
    #Grades and grade period
    @log.info "Iterating grade with query: {}"
    @db['grade'].find({}, @basic_options) do |cur|
      cur.each do |grade|
        edorg = old_edorgs(@db['studentSectionAssociation'], grade['body']['studentSectionAssociationId'])
        stamp_id(@db['grade'], grade['_id'], edorg)
        #      stamp_id(@db['gradingPeriod'], grade['body']['gradingPeriodId'], edorg)
      end
    end
  end

  def fix_courses
    @log.info "Iterating section with query: {}"
    @db['section'].find({}, @basic_options) do |cur|
      cur.each do |section|
        edorg = section['metaData']['edOrgs']
        stamp_id(@db['courseOffering'], section['body']['courseOfferingId'], edorg)
      end
    end
    @log.info "Iterating courseOffering with query: {}"
    @db['courseOffering'].find({}, @basic_options) do |cur|
      cur.each do |courseOffering|
        edorg = courseOffering['metaData']['edOrgs']
        stamp_id(@db['course'], courseOffering['body']['courseId'], edorg)
      end
    end
    @log.info "Iterating courseOffering with query: {}"
    @db['courseOffering'].find({}, @basic_options) do |cur|
      cur.each do |courseOffering|
        edorgs = []
        edorgs << old_edorgs(@db['courseOffering'], courseOffering['_id'])	  
        edorgs << old_edorgs(@db['course'], courseOffering['body']['courseId'])
        edorgs << old_edorgs(@db['session'], courseOffering['body']['sessionId'])
        edorgs = edorgs.flatten.uniq
        stamp_id(@db['courseOffering'], courseOffering['_id'], edorgs)
      end
    end
  end

  def fix_miscellany
    #StudentTranscriptAssociation
    @log.info "Iterating studentTranscriptAssociation with query: {}"
    @db['studentTranscriptAssociation'].find({}, @basic_options) do |cur|
      cur.each do |trans|
        edorg = []
        edorg << old_edorgs(@db['studentTranscriptAssociation'], trans['_id'])	  
        edorg << student_edorgs(trans['body']['studentId'])
        @log.info "Iterating studentAcademicRecord with query: {'_id': #{trans['body']['studentAcademicRecordId']}}"
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
    @log.info "Iterating studentSectionGradebookEntry with query: {}"
    @db['studentSectionGradebookEntry'].find({}, @basic_options) do |cur|
      cur.each do |trans|
        edorg = student_edorgs(trans['body']['studentId'])
        stamp_id(@db['studentSectionGradebookEntry'], trans['_id'], edorg)
      end
    end

    #Student Compentency
    @log.info "Iterating studentCompetency with query: {}"
    @db['studentCompetency'].find({}, @basic_options) do |cur|
      cur.each do |student|
        edorg = old_edorgs(@db['studentSectionAssociation'], student['body']['studentSectionAssociationId'])
        stamp_id(@db['studentCompetency'], student['_id'], edorg)
      end
    end

    #Student Academic Record
    @log.info "Iterating studentAcademicRecord with query: {}"
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
    #puts("stamp [" + collection.name + "]: " + id.to_s)
    if edOrg.nil? or edOrg.empty?
      #puts("[]")
      return
    end
    #puts("[" + edOrg.to_s + "]")
    begin
      tenant = collection.find_one({"_id" => id})
      tenantid = tenant['metaData']['tenantId'] if !tenant.nil? and tenant.include? 'metaData' and tenant['metaData'].include? 'tenantId'
      @log.warn "No tenantId on #{collection.name}##{id}" if tenantid.nil?

      edOrgs = []
      parent_edOrgs = []
      if edOrg.is_a? Array
        edOrg.each do |i|
            parent_edOrgs.concat(parent_ed_org_hash[i]) unless parent_ed_org_hash[i].nil?
        end
        edOrgs.concat(edOrg)
      else
        parent_edOrgs = parent_ed_org_hash[edOrg]
        edOrgs << edOrg
      end

      edOrgs.concat(parent_edOrgs) unless parent_edOrgs.nil?
      edOrgs = edOrgs.flatten.uniq

      collection.update({"_id" => id, 'metaData.tenantId' => tenantid}, {"$set" => {"metaData.edOrgs" => edOrgs}}) unless tenantid.nil?
    rescue Exception => e
      @log.error "Writing to #{collection.name}##{id} - #{e.message}"
      @log.error "Writing to #{collection.name}##{id} - #{e.backtrace}"
    end
    @log.info "Working in #{collection.name}" if @count % 200 == 0
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

  def build_edorg_list()
    @db['educationOrganization'].find({}, @basic_options) do |cur|
        cur.each do |edorg|
            id = edorg['_id']
            
            tenant_id = edorg['metaData']['tenantId']
            @tenant_to_ed_orgs[tenant_id] ||= []
            @tenant_to_ed_orgs[tenant_id].push id

            edorgs = []
            get_parent_edorgs(id, edorgs)

            if !edorgs.empty?
                @parent_ed_org_hash[edorg['_id']] = edorgs
            end

            stamp_id(@db['educationOrganization'], id, id)
        end
    end
  end

  def get_parent_edorgs(id, edorgs)
    edorg = @db['educationOrganization'].find_one({"_id" => id})
    parent_id = edorg['body']['parentEducationAgencyReference'] unless edorg.nil?

    if !parent_id.nil?
        edorgs << parent_id
        get_parent_edorgs(parent_id, edorgs)
    end

    return
  end

end
