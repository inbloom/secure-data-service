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
require 'thread'

class SLCFixer
  attr_accessor :count, :db, :forever, :parent_ed_org_hash
  def initialize(db, forever = false, tenant = nil)
    $stdout.sync = true
    @forever = forever
    @tenant = tenant
    @basic_query = {} 
    @mutex = Mutex.new
    @db = db
    @students = @db['student']
    @count = 0
    @basic_options = {:timeout => false, :batch_size => 100}
    @log = Logger.new($stdout)
    @log.level = Logger::INFO
  end

  def start
    begin
      @student_hash = {}
      @parent_ed_org_hash = {}
      @tenant_to_ed_orgs = {}
      @log.info "We are stamping #{@tenant}"
      time = Time.now
      @threads = []
      fix {build_edorg_list}
      fix {fix_students}
      fix {fix_sections}
      fix {fix_staff}
      @threads << Thread.new {    fix {fix_attendance}}
      @threads << Thread.new {   fix {fix_assessments}}
      @threads << Thread.new {    fix {fix_disciplines}}
      @threads << Thread.new {       fix {fix_parents}}
      @threads << Thread.new {   fix {fix_report_card}}
      @threads << Thread.new {      fix {fix_programs}}
      @threads << Thread.new {       fix {fix_courses}}
      @threads << Thread.new { fix {fix_miscellany}}
      @threads << Thread.new {       fix {fix_cohorts}}
      @threads << Thread.new {        fix {fix_grades}}
      @threads << Thread.new {      fix {fix_sessions}}

      @threads.each do |th|
        th.join
      end
      finalTime = Time.now - time
      @log.info "\t Final time is #{finalTime} secs"
      @log.info "\t Documents(#{@count}) per second #{@count/finalTime}"
    end while(@forever)
  end

  def fix
    @log.debug "Clearing out caches"
    Thread.current[:start_time] = Time.now
    Thread.current[:stamping] = []
    Thread.current[:cache] = Set.new
    yield
    total_time = Time.now - Thread.current[:start_time]
    @log.error "Finished stamping: #{Thread.current[:stamping].join(", ")}"
    @log.error "\tTotal time: #{total_time} s"
    @log.error "\tRPS: #{Thread.current[:cache].count/total_time}"
  end

  def fix_students
    set_stamps(@db['studentSchoolAssociation'])
    set_stamps(@db['student'])
    @log.info "Iterating studentSchoolAssociation with query #{@basic_query}"

    @db['studentSchoolAssociation'].find(@basic_query, @basic_options) do |cur|
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
    set_stamps(@db['section'])
    set_stamps(@db['teacherSectionAssociaton'])
    set_stamps(@db['sectionAssessmentAssociation'])
    set_stamps(@db['studentSectionAssociation'])
    set_stamps(@db['sectionSchoolAssociation'])
    @log.info "Iterating sections with query: #{@basic_query}"
    @db['section'].find(@basic_query, @basic_options) do |cur|
      cur.each do |section|
        edorgs = section['body']['schoolId']
        stamp_id(@db['section'], section['_id'], edorgs)
        sectionQuery = {"body.sectionId" => section['_id']}
        if section.include? "teacherSectionAssociation"
          teacherSectionAssociations = section["teacherSectionAssociation"]
            teacherSectionAssociations.each do |teacherSection|
            stamp_id(@db['section'],teacherSection['_id'],edorgs,"teacherSectionAssociation",section['_id'])
            end
        end
     #   @log.info "Iterating teacherSectionAssociation with query: #{sectionQuery}"
     #   @db['teacherSectionAssociation'].find(sectionQuery, @basic_options) do |scur|
      #    scur.each {|assoc| stamp_id(@db['teacherSectionAssociation'], assoc['_id'], edorgs)}
      #  end
        @log.info "Iterating sectionAssessmentAssociation with query: #{sectionQuery}"
        @db['sectionAssessmentAssociation'].find(sectionQuery, @basic_options) do |scur|
          scur.each {|assoc| stamp_id(@db['sectionAssessmentAssociation'], assoc['_id'], edorgs) }
        end
      #  @log.info "Iterating studentSectionAssociation with query: #{sectionQuery}"
       if section.include? "studentSectionAssociation"
       	studentSectionAssociations = section["studentSectionAssociation"]
           studentSectionAssociations.each do |studentSection|
             edOrg = []
             student_edorg = student_edorgs(studentSection['body']['studentId'])
             edOrg << student_edorg
             edOrg = edOrg.flatten.uniq
             stamp_id(@db['section'],studentSection['_id'],edOrg,"studentSectionAssociation",section['_id'])
           end
       end   
     #   @db['studentSectionAssociation'].find(sectionQuery, @basic_options) do |scur|
      #    scur.each { |assoc| stamp_id(@db['studentSectionAssociation'], assoc['_id'], ([] << edorgs << student_edorgs(assoc['body']['studentId'])).flatten.uniq, assoc['metaData']['tenantId']) }
      #  end
      end
    end
    @log.info "Iterating sectionSchoolAssociation with query: #{@basic_query}"
    @db['sectionSchoolAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each { |assoc| stamp_id(@db['sectionSchoolAssociation'], assoc['_id'], assoc['body']['schoolId']) }
    end
  end

  def fix_attendance
    set_stamps(@db['attendance'])
    @log.info "Iterating attendance with query: #{@basic_query}"
    @db['attendance'].find(@basic_query, @basic_options) do |cur|
      cur.each do |attendance|
        edOrg = student_edorgs(attendance['body']['studentId'])
        stamp_id(@db['attendance'], attendance['_id'], edOrg)
      end
    end
  end

  def fix_assessments

    set_stamps(@db['studentAssessmentAssociation'])
    set_stamps(@db['sectionAssessmentAssociation'])
    embedded_query = @basic_query.clone
    embedded_query["studentAssessmentAssociation"] = {"$exists" => true}
    @log.info "Iterating student with query: #{embedded_query}"
    @db['student'].find(embedded_query, @basic_options) do |cur|
      cur.each do |student|
        student["studentAssessmentAssociation"].each do |studentAssessment| 
          edOrg = []
          student_edorg = student_edorgs(studentAssessment['body']['studentId'])
          edOrg << student_edorg
          edOrg = edOrg.flatten.uniq
          stamp_id(@db['student'], studentAssessment['_id'], edOrg, 
                   "studentAssessmentAssociation", student["_id"])
        end
      end
    end
    @log.info "Iterating sectionAssessmentAssociation with query: #{@basic_query}"
    @db['sectionAssessmentAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |assessment|
        edorgs = []
        section_edorg = old_edorgs(@db['section'], assessment['body']['sectionId'])
        edorgs << section_edorg
        edorgs = edorgs.flatten.uniq
        stamp_id(@db['sectionAssessmentAssociation'], assessment['_id'], section_edorg)
      end
    end
  end

  def fix_disciplines
    set_stamps(@db['disciplineAction'])
    set_stamps(@db['studentDisciplineIncidentAssociation'])
    set_stamps(@db['disciplineIncident'])
    @log.info "Iterating disciplineAction with query: #{@basic_query}"
    @db['disciplineAction'].find(@basic_query, :timeout => false) do |cur|
      cur.each do |action|
        edorg = student_edorgs(action['body']['studentId'])
        stamp_id(@db['disciplineAction'], action['_id'], edorg)
      end
    end
    @log.info "Iterating studentDisciplineIncidentAssociation with query: #{@basic_query}"
    @db['studentDisciplineIncidentAssociation'].find(@basic_query, :timeout => false) do |cur|
      cur.each do |incident|
        edorg = student_edorgs(incident['body']['studentId'])
        stamp_id(@db['studentDisciplineIncidentAssociation'], incident['_id'], edorg)
        stamp_id(@db['disciplineIncident'], incident['body']['disciplineIncidentId'], edorg)
      end
    end
    @log.info "Iterating disciplineIncident with query: #{@basic_query}"
    @db['disciplineIncident'].find(@basic_query, :timeout => false) do |cur|
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
    set_stamps(@db['studentParentAssociation'])
    set_stamps(@db['parent'])
    embedded_query = @basic_query.clone
    embedded_query["studentParentAssociation"] = {"$exists" => true}
    @log.info "Iterating student with query: #{embedded_query}"

    @db['student'].find(embedded_query, @basic_options) do |cur|
        cur.each do |student|
            student["studentParentAssociation"].each do |studentParent|
              edOrg = []
              student_edorg = student_edorgs(studentParent['body']['studentId'])
              edOrg << student_edorg
              edOrg = edOrg.flatten.uniq
              stamp_id(@db['student'], studentParent['_id'], edOrg,
                       "studentParentAssociation", student["_id"])
            end
        end
    end
  end

  def fix_report_card
    set_stamps(@db['reportCard'])
    @log.info "Iterating reportCard with query: #{@basic_query}"
    @db['reportCard'].find(@basic_query, @basic_options) do |cur|
      cur.each do |card|
        edorg = student_edorgs(card['body']['studentId'])
        stamp_id(@db['reportCard'], card['_id'], edorg)
      end
    end
  end

  def fix_programs
    set_stamps(@db['program'])
    set_stamps(@db['staffProgramAssociation'])
    @log.info "Iterating program with query: #{@basic_query}"
    @db['program'].find(@basic_query, @basic_options) do |cur|
      cur.each do |program|
        stamp_id(@db['program'], program['body']['programId'], program['body']['educationOrganizationId'],
                 program['metaData']['tenantId'])
        studentProgramAssociations = program['studentProgramAssociation']
        if !studentProgramAssociations.nil?
          studentProgramAssociations.each do |studentProgram| 
            edOrg = []
            student_edorg = student_edorgs(studentProgram['body']['studentId'])
            edOrg << student_edorg
            edOrg = edOrg.flatten.uniq
            stamp_id(@db['program'], studentProgram['_id'], edOrg,  
                     "studentProgramAssociation", program["_id"])
          end
        end
      end
    end
    @log.info "Iterating staffProgramAssociation with query: #{@basic_query}"
    @db['staffProgramAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |program|
        edorg = []
        program_edorg = old_edorgs(@db['program'], program['body']['programId'])
        staff_edorg = old_edorgs(@db['staff'], program['body']['staffId'])
        edorg << program_edorg << staff_edorg
        edorg = edorg.flatten.uniq
        stamp_id(@db['program'], program['body']['programId'], edorg)
        stamp_id(@db['staffProgramAssociation'], program['_id'], staff_edorg)
      end
    end
  end

  def fix_cohorts
    set_stamps(@db['cohort'])
    set_stamps(@db['studentCohortAssociation'])
    set_stamps(@db['staffCohortAssociation'])
    @log.info "Iterating cohort with query: #{@basic_query}"
    @db['cohort'].find(@basic_query, @basic_options) do |cur|
      cur.each do |cohort|
        stamp_id(@db['cohort'], cohort['_id'], cohort['body']['educationOrgId'])
      end
    end
    @log.info "Iterating studentCohortAssociation with query: #{@basic_query}"
    @db['studentCohortAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = old_edorgs(@db['student'], cohort['body']['studentId'])
        stamp_id(@db['studentCohortAssociation'], cohort['_id'], edorg)
      end
    end
    @log.info "Iterating staffCohortAssociation with query: #{@basic_query}"
    @db['staffCohortAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |cohort|
        edorg = []
        @log.error "Special cohort" if cohort['body']['cohortId'] == "b40926af-8fd5-11e1-86ec-0021701f543f"
        edorg << old_edorgs(@db['cohort'], cohort['body']['cohortId'])
        edorg << old_edorgs(@db['staff'], cohort['body']['staffId'])
        edorg = edorg.flatten.uniq
        stamp_id(@db['staffCohortAssociation'], cohort['_id'], edorg)
        stamp_id(@db['cohort'], cohort['body']['cohortId'], edorg)
      end
    end
  end

  def fix_sessions
    set_stamps(@db['session'])
    set_stamps(@db['schoolSessionAssociation'])
    set_stamps(@db['gradingPeriod'])
    @log.info "Iterating session with query: #{@basic_query}"
    @db['session'].find(@basic_query, @basic_options) do |cur|
      cur.each do |session|
        edorg = []
        sessionQuery = {"body.sessionId" => session["_id"]}
        @log.info "Iterating section with query: #{sessionQuery}"
        @db['section'].find(sessionQuery, @basic_options) do |scur|
          scur.each do |sec|
            edorg << sec['metaData']['edOrgs'] unless sec['metaData'].nil?
          end
        end
        edorg = edorg.flatten.uniq
        stamp_id(@db['session'], session['_id'], edorg)
        @log.info "Iterating schoolSessionAssociation with query: #{sessionQuery}"
        @db['schoolSessionAssociation'].find(sessionQuery, @basic_options) do |scur|
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
    set_stamps(@db['staffEducationOrganizationAssociation'])
    set_stamps(@db['staff'])
    set_stamps(@db['teacherSchoolAssociation'])
    @log.info "Iterating staffEducationOrganizationAssociation with query: #{@basic_query}"
    @db['staffEducationOrganizationAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |staff|
        old = old_edorgs(@db['staff'], staff['body']['staffReference'])
        edorg = staff['body']['educationOrganizationReference']
        stamp_id(@db['staffEducationOrganizationAssociation'], staff['_id'], edorg)
        stamp_id(@db['staff'], staff['body']['staffReference'], (old << edorg).flatten.uniq)
      end
    end
    #This needed?
    @log.info "Iterating teacherSchoolAssociation with query: #{@basic_query}"
    @db['teacherSchoolAssociation'].find(@basic_query, @basic_options) do |cur|
      cur.each do |teacher|
        old = old_edorgs(@db['staff'], teacher['body']['teacherId'])
        stamp_id(@db['teacherSchoolAssociation'], teacher['_id'], teacher['body']['schoolId'])
        stamp_id(@db['staff'], teacher['body']['teacherId'], (old << teacher['body']['schoolId']).flatten.uniq)
      end
    end
  end

  def fix_grades
    set_stamps(@db['gradebookEntry'])
    set_stamps(@db['grade'])
    
    embedded_query = @basic_query.clone
    embedded_query["gradebookEntry"] = {"$exists" => true}
    @log.info "Iterating section with query: #{embedded_query}"
    @db['section'].find(embedded_query, @basic_options) do |cur|
      cur.each do |section|
        section["gradebookEntry"].each do |gradebookEntry| 
          edorg = old_edorgs(@db['section'], gradebookEntry['body']['sectionId'])
          stamp_id(@db['section'], gradebookEntry['_id'], edorg, "gradebookEntry", section['_id'])
        end
      end
    end  

    #Grades and grade period
    @log.info "Iterating grade with query: #{@basic_query}"
    @db['grade'].find(@basic_query, @basic_options) do |cur|
      cur.each do |grade|
        # lookup studentSectionAssociation from section and get edorgs
       section_docs = @db['section'].find({"studentSectionAssociation._id" => grade['body']['studentSectionAssociationId']})
       edorg =[]
       if section_docs != nil and section_docs.count >0
         section_docs.each do |section|
           section['studentSectionAssociation'].each do |ssa|
             if ssa["_id"]==grade['body']['studentSectionAssociationId']
               edorg << dig_edorg_out(ssa)
             end
           end
         end
       end
       # edorg = old_edorgs(@db['studentSectionAssociation'], grade['body']['studentSectionAssociationId'])
        stamp_id(@db['grade'], grade['_id'], edorg)
        #      stamp_id(@db['gradingPeriod'], grade['body']['gradingPeriodId'], edorg)
      end
    end
  end

  def fix_courses
    set_stamps(@db['courseOffering'])
    set_stamps(@db['course'])
    @log.info "Iterating section with query: #{@basic_query}"
    @db['section'].find(@basic_query, @basic_options) do |cur|
      cur.each do |section|
        edorg = section['metaData']['edOrgs']
        stamp_id(@db['courseOffering'], section['body']['courseOfferingId'], edorg)
      end
    end
    @log.info "Iterating courseOffering with query: #{@basic_query}"
    @db['courseOffering'].find(@basic_query, @basic_options) do |cur|
      cur.each do |courseOffering|
        edorg = courseOffering['metaData']['edOrgs']
        stamp_id(@db['course'], courseOffering['body']['courseId'], edorg)
      end
    end
    @log.info "Iterating courseOffering with query: #{@basic_query}"
    @db['courseOffering'].find(@basic_query, @basic_options) do |cur|
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
    set_stamps(@db['courseTranscript'])
    set_stamps(@db['studentSectionGradebookEntry'])
    set_stamps(@db['studentCompetency'])
    set_stamps(@db['studentAcademicRecord'])
    #courseTranscript
    @log.info "Iterating courseTranscript with query: #{@basic_query}"

    #Student Academic Record
    @log.info "Iterating studentAcademicRecord with query: #{@basic_query}"
    @db['studentAcademicRecord'].find(@basic_query, @basic_options) do |cur|
      cur.each do |student|
        edorg = student_edorgs(student['body']['studentId'])
        stamp_id(@db['studentAcademicRecord'], student['_id'], edorg)
      end
    end
    @db['courseTranscript'].find(@basic_query, @basic_options) do |cur|
      cur.each do |trans|
        edorg = []
        edorg << old_edorgs(@db['courseTranscript'], trans['_id'])
        edorg << old_edorgs(@db['studentAcademicRecord'], trans['body']['studentAcademicRecordId'])
        edorg = edorg.flatten.uniq
        @log.warn "No edorgs on student #{trans['body']['studentId']}?" if edorg.empty?
        @log.debug "Edorgs for sTA##{trans['_id']} is #{edorg.to_s}" unless edorg.empty?
        stamp_id(@db['courseTranscript'], trans['_id'], edorg)
      end
    end

    #StudentGradebookEntry
    @log.info "Iterating studentGradebookEntry with query: #{@basic_query}"
    @db['studentGradebookEntry'].find(@basic_query, @basic_options) do |cur|
      cur.each do |trans|
        edorg = student_edorgs(trans['body']['studentId'])
        stamp_id(@db['studentGradebookEntry'], trans['_id'], edorg)
      end
    end

    #Student Compentency
    @log.info "Iterating studentCompetency with query: #{@basic_query}"
    @db['studentCompetency'].find(@basic_query, @basic_options) do |cur|
      cur.each do |student|
       # lookup studentSectionAssociation from section and get edorgs
         section_docs = @db['section'].find({"studentSectionAssociation._id" => student['body']['studentSectionAssociationId']})
       edorg =[]
       if section_docs != nil and section_docs.count >0
         section_docs.each do |section|
            section['studentSectionAssociation'].each do |ssa|
             if ssa["_id"]==student['body']['studentSectionAssociationId']
               edorg << dig_edorg_out(ssa)
             end
           end
         end
       end
       # edorg = old_edorgs(@db['studentSectionAssociation'], student['body']['studentSectionAssociationId'])
        stamp_id(@db['studentCompetency'], student['_id'], edorg)
      end
    end

  end

  private
  def set_stamps(collection)
    Thread.current[:stamping].push collection.name
  end

  def stamp_id(collection, id, edOrg, location = :default, parentid = "")
    if edOrg.nil? or edOrg.empty?
      @log.warn "No edorgs or tenant found for #{collection.name}##{id}"
      return
    end
    begin
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

      if location == :default
        if id.is_a? Array
          id.each do |array_id|
            collection.update({"_id" => array_id}, {"$unset" => {"padding" => 1}, "$set" => {"metaData.edOrgs" => edOrgs}})
          end
          Thread.current[:cache].merge id if id.is_a? Array
        else
          collection.update({"_id" => id}, {"$unset" => {"padding" => 1}, "$set" => {"metaData.edOrgs" => edOrgs}})
          Thread.current[:cache].add id unless id.is_a? Array
        end
      else
        collection.update({"_id" => parentid, "#{location}._id" => id}, {"$set" => {"#{location}.$.metaData.edOrgs" => edOrgs}})
        Thread.current[:cache].add id unless id.is_a? Array
      end
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
    # Some base cases are to avoid rolling up "Old" data
    # we do taht by keeping an array of the collections we stamp
    #
    # If we are asking for old edorgs within that set, we refuse
    # to give it unless we have already started stamping it.
    if Thread.current[:stamping].include? collection.name and !Thread.current[:cache].include? id and !id.is_a? Array
      @log.debug "We aren't stamping #{collection.name}##{id} because of additive concerns"
      return []
    end
    if id.is_a? Array
      id.each do |i|
        if Thread.current[:cache].include? i and Thread.current[:stamping].include? collection.name

          @log.debug "We aren't stamping #{collection.name}##{id} because of additive concerns"
        end
      end
      doc = collection.find({"_id" => {'$in' => id}})
    else
      doc = [collection.find_one({"_id" => id})]
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
    @db['educationOrganization'].find(@basic_query, @basic_options) do |cur|
        cur.each do |edorg|
            id = edorg['_id']

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
    edorgQuery = {"_id" => id}
    edorg = @db['educationOrganization'].find_one(edorgQuery)
    parent_id = edorg['body']['parentEducationAgencyReference'] unless edorg.nil?

    if !parent_id.nil?
        edorgs << parent_id
        get_parent_edorgs(parent_id, edorgs)
    end

    return
  end

end
