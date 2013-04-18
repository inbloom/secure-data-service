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
require "set"
require 'date'
require 'logger'
require 'digest/sha1'
include Process

class LEAMarker
  attr_accessor :count, :readDb
  def initialize(readDb, writeDb, tenant, lea, log = nil)
    $stdout.sync = true
    @base_lea = lea
    @tenant = tenant
    @basic_query = {}
    @writeDb = writeDb
    @readDb = readDb
    @students = @readDb['student']
    @count = 0
    @basic_options = {:timeout => false, :batch_size => 1000}
    @log = log || Logger.new($stdout)
    @log.level = Logger::INFO
  end

  def start
      @student_to_edorg = {}
      @edorg_to_lea = {}
      @parent_ed_org_hash = {}
      @tenant_to_ed_orgs = {}
      @log.info "We are stamping #{@writeDb.name}"
      time = Time.now
      @pids = []
      fix {build_edorg_list}
      fix {fix_students}
      @pids << fork {fix {fix_student_data}}
      @pids << fork {fix {fix_staff}}
      # fix {fix_sections}
      # fix {fix_staff}
      # @pids << fork {    fix {fix_attendance}}
      # @pids << fork {   fix {fix_assessments}}
      # @pids << fork {    fix {fix_disciplines}}
      # @pids << fork {       fix {fix_parents}}
      # @pids << fork {   fix {fix_report_card}}
      # @pids << fork {      fix {fix_programs}}
      # @pids << fork {       fix {fix_courses}}
      # @pids << fork { fix {fix_miscellany}}
      # @pids << fork {       fix {fix_cohorts}}
      # @pids << fork {        fix {fix_grades}}
      # @pids << fork {      fix {fix_sessions}}
      Process.waitall

      finalTime = Time.now - time
      @log.info "\t #{@writeDb.name} - Final time is #{finalTime} secs"
      @log.info "\t #{@writeDb.name} - Documents(#{@count}) per second #{@count/finalTime}"
  end

  def fix
    @log.debug "Clearing out caches"
    @start_time = Time.now
    @stamping = []
    @cache = Set.new
    yield
    total_time = Time.now - @start_time
    @log.error "Finished stamping: #{@stamping.join(", ")}"
    @log.error "\tTotal time: #{total_time} s"
    @log.error "\tCount-#{@base_lea}: #{@cache.count}"
    @log.error "\tRPS-#{@base_lea}: #{@cache.count/total_time}"
  end

  def fix_students
    #set_stamps(@readDb['studentSchoolAssociation'])
    set_stamps(@readDb['student'])
    @log.info "Iterating students with query {schools.edOrgs =>#{@base_lea}}"
    
    @readDb['student'].find({"schools.edOrgs" => @base_lea}, @basic_options) do |cur|
      cur.each do |student|
        @student_to_edorg[student["_id"]] = @base_lea
        stamp_id(@readDb['student'], student["_id"])
      end
    end
  end
  
  def fix_student_data
    set_stamps(@readDb['studentAssessment'])
    set_stamps(@readDb['studentCompetencyObjective'])
    set_stamps(@readDb['studentGradebookEntry'])
    set_stamps(@readDb['studentSchoolAssociation'])
    student_key_set = []
    splice = 0
    splice = @student_to_edorg.keys.size / 10000 if @student_to_edorg.keys.size > 0
    splice += 1
    splice.times {|s| student_key_set[s] = @student_to_edorg.keys.slice(s*10000, 10000)}
    student_key_set.each do |set|
      query = {"body.studentId" => {"$in" =>set}}
      @readDb['studentAssessment'].find(query, @basic_options) do |cur|
        cur.each do |sA|
          stamp_id @readDb['studentAssessment'], sA['_id']
        end
      end
    
      @readDb['studentCompetencyObjective'].find(query, @basic_options) do |cur|
        cur.each do |sA|
          stamp_id @readDb['studentCompetencyObjective'], sA['_id']
        end
      end
    
      @readDb['studentGradebookEntry'].find(query, @basic_options) do |cur|
        cur.each do |sA|
          stamp_id @readDb['studentGradebookEntry'], sA['_id']
        end
      end
    
      @readDb['studentSchoolAssociation'].find(query, @basic_options) do |cur|
        cur.each do |sA|
          stamp_id @readDb['studentSchoolAssociation'], sA['_id']
        end
      end
    end
  end
  
  def fix_staff
     set_stamps(@readDb['staffEducationOrganizationAssociation'])
     set_stamps(@readDb['staff'])
     set_stamps(@readDb['teacherSchoolAssociation'])

     edorg_set = []
     splice = 0
     splice = @edorg_to_lea.keys.size / 10000 if @edorg_to_lea.keys.size > 0
     @log.info "Edorgs in LEA #{@edorg_to_lea.keys}"
     splice += 1
     splice.times {|s| edorg_set[s] = @edorg_to_lea.keys.slice(s*10000, 10000)}
     edorg_set.each do |set|
       query = {"body.educationOrganizationReference" => {"$in" => set}}
       @readDb['staffEducationOrganizationAssociation'].find(query, @basic_options) do |cur|
         cur.each do |staff|
           stamp_id(@readDb['staffEducationOrganizationAssociation'], staff['_id'])
           stamp_id(@readDb['staff'], staff['body']['staffReference'])
         end
       end
       #This needed?
       query = {"body.schoolId" => {"$in" => set}}
       @readDb['teacherSchoolAssociation'].find(query, @basic_options) do |cur|
         cur.each do |teacher|
           stamp_id(@readDb['teacherSchoolAssociation'], teacher['_id'])
           stamp_id(@readDb['staff'], teacher['body']['teacherId'])
         end
       end
     end
   end

  def fix_sections
    set_stamps(@readDb['section'])
    set_stamps(@readDb['teacherSectionAssociaton'])
    set_stamps(@readDb['sectionAssessmentAssociation'])
    set_stamps(@readDb['studentSectionAssociation'])
    set_stamps(@readDb['sectionSchoolAssociation'])
    @log.info "Iterating sections with query: #{@basic_query}"
    @readDb['section'].find(@basic_query, @basic_options) do |cur|
      cur.each do |section|
        edorgs = section['body']['schoolId']
        stamp_id(@readDb['section'], section['_id'], edorgs, section['metaData']['tenantId'])
        sectionQuery = @tenant.nil? ? {"body.sectionId" => section['_id']} : {"metaData.tenantId" => @tenant, "body.sectionId" => section['_id'] }
        @log.info "Iterating teacherSectionAssociation with query: #{sectionQuery}"
        @readDb['teacherSectionAssociation'].find(sectionQuery, @basic_options) do |scur|
          scur.each {|assoc| stamp_id(@readDb['teacherSectionAssociation'], assoc['_id'], edorgs, assoc['metaData']['tenantId'])}
        end
        @log.info "Iterating sectionAssessmentAssociation with query: #{sectionQuery}"
        @readDb['sectionAssessmentAssociation'].find(sectionQuery, @basic_options) do |scur|
          scur.each {|assoc| stamp_id(@readDb['sectionAssessmentAssociation'], assoc['_id'], edorgs, assoc['metaData']['tenantId']) }
        end
        @log.info "Iterating studentSectionAssociation with query: #{sectionQuery}"
        @readDb['studentSectionAssociation'].find(sectionQuery, @basic_options) do |scur|
          scur.each { |assoc| stamp_id(@readDb['studentSectionAssociation'], assoc['_id'], ([] << edorgs << student_edorgs(assoc['body']['studentId'])).flatten.uniq, assoc['metaData']['tenantId']) }
        end
      end
    end
    @log.info "Iterating sectionSchoolAssociation with query: #{@basic_query}"
    @readDb['sectionSchoolAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each { |assoc| stamp_id(@readDb['sectionSchoolAssociation'], assoc['_id'], assoc['body']['schoolId'], assoc['metaData']['tenantId']) }
    end
  end

  def fix_attendance
    set_stamps(@readDb['attendance'])
    @log.info "Iterating attendance with query: #{@basic_query}"
    @readDb['attendance'].find(@basic_query, @basic_options) do |cur|
      cur.each do |attendance|
        edOrg = student_edorgs(attendance['body']['studentId'])
        stamp_id(@readDb['attendance'], attendance['_id'], edOrg, attendance['metaData']['tenantId'])
      end
    end
  end

  def fix_assessments

    set_stamps(@readDb['studentAssessmentAssociation'])
    set_stamps(@readDb['sectionAssessmentAssociation'])
    @log.info "Iterating studentAssessmentAssociation with query: #{@basic_query}"
    @readDb['studentAssessmentAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |studentAssessment|
        edOrg = []
        student_edorg = student_edorgs(studentAssessment['body']['studentId'])
        edOrg << student_edorg
        edOrg = edOrg.flatten.uniq
        stamp_id(@readDb['studentAssessmentAssociation'], studentAssessment['_id'], edOrg, studentAssessment['metaData']['tenantId'])
      end
    end
    @log.info "Iterating sectionAssessmentAssociation with query: #{@basic_query}"
    @readDb['sectionAssessmentAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |assessment|
        edorgs = []
        section_edorg = old_edorgs(@readDb['section'], assessment['body']['sectionId'])
        edorgs << section_edorg
        edorgs = edorgs.flatten.uniq
        stamp_id(@readDb['sectionAssessmentAssociation'], assessment['_id'], section_edorg, assessment['metaData']['tenantId'])
      end
    end
  end

  def fix_disciplines
    set_stamps(@readDb['disciplineAction'])
    set_stamps(@readDb['studentDisciplineIncidentAssociation'])
    set_stamps(@readDb['disciplineIncident'])
    @log.info "Iterating disciplineAction with query: #{@basic_query}"
    @readDb['disciplineAction'].find(@basic_query, :timeout => false) do |cur|
      cur.each do |action|
        edorg = student_edorgs(action['body']['studentId'])
        stamp_id(@readDb['disciplineAction'], action['_id'], edorg, action['metaData']['tenantId'])
      end
    end
    @log.info "Iterating studentDisciplineIncidentAssociation with query: #{@basic_query}"
    @readDb['studentDisciplineIncidentAssociation'].find(@basic_query, :timeout => false) do |cur|
      cur.each do |incident|
        edorg = student_edorgs(incident['body']['studentId'])
        stamp_id(@readDb['studentDisciplineIncidentAssociation'], incident['_id'], edorg, incident['metaData']['tenantId'])
        stamp_id(@readDb['disciplineIncident'], incident['body']['disciplineIncidentId'], edorg, incident['metaData']['tenantId'])
      end
    end
    @log.info "Iterating disciplineIncident with query: #{@basic_query}"
    @readDb['disciplineIncident'].find(@basic_query, :timeout => false) do |cur|
      cur.each do |discipline|
        edorgs = []
        edorgs << dig_edorg_out(discipline)
        edorgs << discipline['body']['schoolId']
        edorgs = edorgs.flatten.uniq
        stamp_id(@readDb['disciplineIncident'], discipline['_id'], edorgs, discipline['metaData']['tenantId'])
      end
    end
  end

  def fix_parents
    set_stamps(@readDb['studentParentAssociation'])
    set_stamps(@readDb['parent'])
    @log.info "Iterating studentParentAssociation with query: #{@basic_query}"
    @readDb['studentParentAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |parent|
        edorg = student_edorgs(parent['body']['studentId'])
        stamp_id(@readDb['studentParentAssociation'], parent['_id'], edorg, parent['metaData']['tenantId'])
        stamp_id(@readDb['parent'], parent['body']['parentId'], edorg, parent['metaData']['tenantId'])
      end
    end
  end

  def fix_report_card
    set_stamps(@readDb['reportCard'])
    @log.info "Iterating reportCard with query: #{@basic_query}"
    @readDb['reportCard'].find(@basic_query, @basic_options) do |cur|
      cur.each do |card|
        edorg = student_edorgs(card['body']['studentId'])
        stamp_id(@readDb['reportCard'], card['_id'], edorg, card['metaData']['tenantId'])
      end
    end
  end

  def fix_programs
    set_stamps(@readDb['studentProgramAssociation'])
    set_stamps(@readDb['program'])
    set_stamps(@readDb['staffProgramAssociation'])
    @log.info "Iterating studentProgramAssociation with query: #{@basic_query}"
    @readDb['studentProgramAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |program|
        edorg = student_edorgs(program['body']['studentId'])
        stamp_id(@readDb['studentProgramAssociation'], program['_id'], program['body']['educationOrganizationId'], program['metaData']['tenantId'])
        stamp_id(@readDb['program'], program['body']['programId'], program['body']['educationOrganizationId'], program['metaData']['tenantId'])
      end
    end
    @log.info "Iterating staffProgramAssociation with query: #{@basic_query}"
    @readDb['staffProgramAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |program|
        edorg = []
        program_edorg = old_edorgs(@readDb['program'], program['body']['programId'])
        staff_edorg = old_edorgs(@readDb['staff'], program['body']['staffId'])
        edorg << program_edorg << staff_edorg
        edorg = edorg.flatten.uniq
        stamp_id(@readDb['program'], program['body']['programId'], edorg, program['metaData']['tenantId'])
        stamp_id(@readDb['staffProgramAssociation'], program['_id'], staff_edorg, program['metaData']['tenantId'])
      end
    end
  end

  def fix_cohorts
    set_stamps(@readDb['cohort'])
    set_stamps(@readDb['studentCohortAssociation'])
    set_stamps(@readDb['staffCohortAssociation'])
    @log.info "Iterating cohort with query: #{@basic_query}"
    @readDb['cohort'].find(@basic_query, @basic_options) do |cur|
      cur.each do |cohort|
        stamp_id(@readDb['cohort'], cohort['_id'], cohort['body']['educationOrgId'], cohort['metaData']['tenantId'])
      end
    end
    @log.info "Iterating studentCohortAssociation with query: #{@basic_query}"
    @readDb['studentCohortAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = old_edorgs(@readDb['student'], cohort['body']['studentId'])
        stamp_id(@readDb['studentCohortAssociation'], cohort['_id'], edorg, cohort['metaData']['tenantId'])
      end
    end
    @log.info "Iterating staffCohortAssociation with query: #{@basic_query}"
    @readDb['staffCohortAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = []
        @log.error "Special cohort" if cohort['body']['cohortId'] == "b40926af-8fd5-11e1-86ec-0021701f543f"
        edorg << old_edorgs(@readDb['cohort'], cohort['body']['cohortId'])
        edorg << old_edorgs(@readDb['staff'], cohort['body']['staffId'])
        edorg = edorg.flatten.uniq
        stamp_id(@readDb['staffCohortAssociation'], cohort['_id'], edorg, cohort['metaData']['tenantId'])
        stamp_id(@readDb['cohort'], cohort['body']['cohortId'], edorg, cohort['metaData']['tenantId'])
      end
    end
  end

  def fix_sessions
    set_stamps(@readDb['session'])
    set_stamps(@readDb['schoolSessionAssociation'])
    set_stamps(@readDb['gradingPeriod'])
    @log.info "Iterating session with query: #{@basic_query}"
    @readDb['session'].find(@basic_query, @basic_options) do |cur|
      cur.each do |session|
        edorg = []
        sessionQuery = @tenant.nil? ? {"body.sessionId" => session["_id"]} : {"metaData.tenantId" => @tenant,"body.sessionId" => session["_id"]}
        @log.info "Iterating section with query: #{sessionQuery}"
        @readDb['section'].find(sessionQuery, @basic_options) do |scur|
          scur.each do |sec|
            edorg << sec['metaData']['edOrgs'] unless sec['metaData'].nil?
          end
        end
        edorg = edorg.flatten.uniq
        stamp_id(@readDb['session'], session['_id'], edorg, session['metaData']['tenantId'])
        @log.info "Iterating schoolSessionAssociation with query: #{sessionQuery}"
        @readDb['schoolSessionAssociation'].find(sessionQuery, @basic_options) do |scur|
          scur.each do |assoc|
            stamp_id(@readDb['schoolSessionAssociation'], assoc['_id'], edorg, assoc['metaData']['tenantId'])
          end
        end
        gradingPeriodReferences = session['body']['gradingPeriodReference']
        unless gradingPeriodReferences.nil?
          gradingPeriodReferences.each do |gradingPeriodRef|
            old = old_edorgs(@readDb['gradingPeriod'], gradingPeriodRef)
            value = (old << edorg).flatten.uniq
            stamp_id(@readDb['gradingPeriod'], gradingPeriodRef, value, session['metaData']['tenantId'])
          end
        end
      end
    end
  end

 

  def fix_grades
    set_stamps(@readDb['gradebookEntry'])
    set_stamps(@readDb['grade'])
    @log.info "Iterating gradebookEntry with query: #{@basic_query}"
    @readDb['gradebookEntry'].find(@basic_query, @basic_options) do |cur|
      cur.each do |grade|
        edorg = old_edorgs(@readDb['section'], grade['body']['sectionId'])
        stamp_id(@readDb['gradebookEntry'], grade['_id'], edorg, grade['metaData']['tenantId'])
      end
    end
    #Grades and grade period
    @log.info "Iterating grade with query: #{@basic_query}"
    @readDb['grade'].find(@basic_query, @basic_options) do |cur|
      cur.each do |grade|
        edorg = old_edorgs(@readDb['studentSectionAssociation'], grade['body']['studentSectionAssociationId'])
        stamp_id(@readDb['grade'], grade['_id'], edorg, grade['metaData']['tenantId'])
        #      stamp_id(@readDb['gradingPeriod'], grade['body']['gradingPeriodId'], edorg)
      end
    end
  end

  def fix_courses
    set_stamps(@readDb['courseOffering'])
    set_stamps(@readDb['course'])
    @log.info "Iterating section with query: #{@basic_query}"
    @readDb['section'].find(@basic_query, @basic_options) do |cur|
      cur.each do |section|
        edorg = section['metaData']['edOrgs']
        stamp_id(@readDb['courseOffering'], section['body']['courseOfferingId'], edorg, section['metaData']['tenantId'])
      end
    end
    @log.info "Iterating courseOffering with query: #{@basic_query}"
    @readDb['courseOffering'].find(@basic_query, @basic_options) do |cur|
      cur.each do |courseOffering|
        edorg = courseOffering['metaData']['edOrgs']
        stamp_id(@readDb['course'], courseOffering['body']['courseId'], edorg, courseOffering['metaData']['tenantId'])
      end
    end
    @log.info "Iterating courseOffering with query: #{@basic_query}"
    @readDb['courseOffering'].find(@basic_query, @basic_options) do |cur|
      cur.each do |courseOffering|
        edorgs = []
        edorgs << old_edorgs(@readDb['courseOffering'], courseOffering['_id'])
        edorgs << old_edorgs(@readDb['course'], courseOffering['body']['courseId'])
        edorgs << old_edorgs(@readDb['session'], courseOffering['body']['sessionId'])
        edorgs = edorgs.flatten.uniq
        stamp_id(@readDb['courseOffering'], courseOffering['_id'], edorgs, courseOffering['metaData']['tenantId'])
      end
    end
  end

  def fix_miscellany
    set_stamps(@readDb['studentTranscriptAssociation'])
    set_stamps(@readDb['studentSectionGradebookEntry'])
    set_stamps(@readDb['studentCompetency'])
    set_stamps(@readDb['studentAcademicRecord'])
    #StudentTranscriptAssociation
    @log.info "Iterating studentTranscriptAssociation with query: #{@basic_query}"

    #Student Academic Record
    @log.info "Iterating studentAcademicRecord with query: #{@basic_query}"
    @readDb['studentAcademicRecord'].find(@basic_query, @basic_options) do |cur|
      cur.each do |student|
        edorg = student_edorgs(student['body']['studentId'])
        stamp_id(@readDb['studentAcademicRecord'], student['_id'], edorg, student['metaData']['tenantId'])
      end
    end
    @readDb['studentTranscriptAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |trans|
        edorg = []
        edorg << old_edorgs(@readDb['studentTranscriptAssociation'], trans['_id'])
        edorg << old_edorgs(@readDb['studentAcademicRecord'], trans['body']['studentAcademicRecordId'])
        edorg = edorg.flatten.uniq
        @log.warn "No edorgs on student #{trans['body']['studentId']}?" if edorg.empty?
        @log.debug "Edorgs for sTA##{trans['_id']} is #{edorg.to_s}" unless edorg.empty?
        stamp_id(@readDb['studentTranscriptAssociation'], trans['_id'], edorg, trans['metaData']['tenantId'])
      end
    end

    #StudentGradebookEntry
    @log.info "Iterating studentGradebookEntry with query: #{@basic_query}"
    @readDb['studentGradebookEntry'].find(@basic_query, @basic_options) do |cur|
      cur.each do |trans|
        edorg = student_edorgs(trans['body']['studentId'])
        stamp_id(@readDb['studentGradebookEntry'], trans['_id'], edorg, trans['metaData']['tenantId'])
      end
    end

    #Student Compentency
    @log.info "Iterating studentCompetency with query: #{@basic_query}"
    @readDb['studentCompetency'].find(@basic_query, @basic_options) do |cur|
      cur.each do |student|
        edorg = old_edorgs(@readDb['studentSectionAssociation'], student['body']['studentSectionAssociationId'])
        stamp_id(@readDb['studentCompetency'], student['_id'], edorg, student['metaData']['tenantId'])
      end
    end

  end

  private
  
  def set_stamps(collection)
    @stamping.push collection.name
    @writeDb[collection.name].remove {}
  end
  
  def stamp_id(collection, id)
    begin
      if id.is_a? Array
        id.each do |array_id|
          @writeDb[collection.name].insert({"_id" => id, "sliId" => id})
        end
        @cache.merge id if id.is_a? Array
      else
        @writeDb[collection.name].insert({"_id" => id, "sliId" => id})
        @cache.add id unless id.is_a? Array
      end
    rescue Exception => e
      # @log.error "Writing to #{collection.name}##{id} - #{e.message}"
      # @log.error "Writing to #{collection.name}##{id} - #{e.backtrace}"
    end
    @log.info "Working in #{collection.name}" if @count % 200 == 0
    @count += 1
  end

  def student_edorgs(id)
    if id.is_a? Array
      temp = []
      id.each do |i|
        temp = (temp + @student_to_edorg[i]) if @student_to_edorg.has_key? i
      end
      return temp.flatten.uniq
    end
    return @student_to_edorg[id] if @student_to_edorg.has_key? id
    []
  end

  def old_edorgs(collection, id)
    # Some base cases are to avoid rolling up "Old" data
    # we do taht by keeping an array of the collections we stamp
    #
    # If we are asking for old edorgs within that set, we refuse
    # to give it unless we have already started stamping it.
    if @stamping.include? collection.name and !@cache.include? id and !id.is_a? Array
      @log.debug "We aren't stamping #{collection.name}##{id} because of additive concerns"
      return []
    end
    if id.is_a? Array
      id.each do |i|
        if @cache.include? i and @stamping.include? collection.name

          @log.debug "We aren't stamping #{collection.name}##{id} because of additive concerns"
        end
      end
      @writeDb[collection.name].find({"_id" => id})
      doc = []
      doc = @writeDb[collection.name].find({"_id" => {'$in' => id}}) do |cur|
        cur.each {|d| doc << d }
      end
    else
      doc = [@writeDb[collection.name].find_one({"_id" => id})]
      doc = [@writeDb[collection.name].find_one({"_id" => id})]
    end
    final = [ ]
    doc.each do |d|
      final << dig_edorg_out(d)
    end
    final.flatten.uniq
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
    set_stamps(@readDb['educationOrganization'])
    edorg = @readDb['educationOrganization'].find_one({"_id" => @base_lea})
    return if edorg.nil?
    id = edorg['_id']
    get_child_edorgs(id)
  end

  def get_child_edorgs(id)
    @edorg_to_lea[id] = @base_lea
    stamp_id(@readDb['educationOrganization'], id)
    @readDb['educationOrganization'].find({"body.parentEducationAgencyReference" => id}, @basic_options) do |cur|
      cur.each { |child| get_child_edorgs(child['_id']) }
    end
  end
end
