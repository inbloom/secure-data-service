require 'rubygems'
require 'mongo'
require 'benchmark'
require 'set'
require 'date'
require 'logger'

class SLCFixer
  attr_accessor :db, :log

  def initialize(db, logger = nil, grace_period = 2000)
    @db = db
    @basic_options = {:timeout => false, :batch_size => 100}
    @count = 0
    @log = logger || Logger.new(STDOUT)
    # @log = Logger.new(STDOUT)
    @log.level ||= Logger::WARN

    @teacher_ids = {}
    @db['staff'].find({type: "teacher"}, {fields: ['_id', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |teacher|
        @teacher_ids[teacher['_id']] = teacher['metaData']['tenantId']
      end
    end

    @studentId_to_teachers = {}
    @studentId_to_tenant = {}

    @current_date = Date.today.to_s
    @grace_date = (Date.today - grace_period).to_s
  end

  def start
    time = Time.now

    @threads = []
    Benchmark.bm(20) do |x|
      x.report('students')    {stamp_students}
      @threads << Thread.new {x.report('section')    {stamp_sections}}
      @threads << Thread.new {x.report('program')    {stamp_programs}}
      @threads << Thread.new {x.report('cohort')     {stamp_cohorts}}
      @threads << Thread.new {x.report('parent')     {stamp_parents}}
      @threads << Thread.new {x.report('assessments')     {stamp_assessments}}
      @threads << Thread.new {x.report('disciplines')     {stamp_disciplines}}
      @threads << Thread.new {x.report('other')     {stamp_other}}
      @threads << Thread.new {x.report('student_associations')     {stamp_student_associations}}
      @threads << Thread.new {x.report('teacher')     {stamp_teacher}}
      @threads << Thread.new {x.report('studentSectionGradebookEntry')     {stamp_gradebook}}
      @threads << Thread.new {x.report('school')     {stamp_schools}}
    end

    @threads.each do |th|
      th.join
    end

    finalTime = Time.now - time
    puts "\t Final time is #{finalTime} secs"
  end

  def stamp_students

    @log.info "Stamping students"
    @db['student'].find({}, {fields: ['_id', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |student|
        studentId = student['_id']
        tenantId = student['metaData']['tenantId']
        @studentId_to_tenant[studentId] = tenantId

        teacherIds = []
        teacherIds += find_teachers_for_student_through_section(studentId)
        teacherIds += find_teachers_for_student_through_program(studentId)
        teacherIds += find_teachers_for_student_through_cohort(studentId)
        teacherIds = teacherIds.flatten
        teacherIds = teacherIds.uniq
        @studentId_to_teachers[studentId] = teacherIds
        stamp_full_context(@db['student'], studentId, tenantId, teacherIds)
      }
    }

    @studentId_to_teachers.delete_if {|_,v| v.nil?}
  end

  def find_teachers_for_student_through_section(studentId)
    teachers = []
    @db['studentSectionAssociation'].find({'body.studentId'=> studentId, 
                                            '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @grace_date}} ]
                                          }, @basic_options) { |ssa_cursor|
      ssa_cursor.each { |ssa|
        @db['teacherSectionAssociation'].find({'body.sectionId'=> ssa['body']['sectionId'], 
                                                '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @grace_date}} ]
                                              }, @basic_options) { |tsa_cursor|
          tsa_cursor.each { |tsa|
            #@log.debug "teacherSectionAssoc->teacherId #{tsa['body']['teacherId'].to_s}"
            #@log.debug "found assoc - #{tsa['_id']} #{tsa['body']['endDate']}"
            teachers.push tsa['body']['teacherId']
          }
        }
      }
    }
    #@log.debug "section teachers for #{studentId}: #{teachers.to_s}"
    teachers
  end

  def find_teachers_for_student_through_cohort(studentId)
    teachers = []
    @db['studentCohortAssociation'].find({'body.studentId'=> studentId,
                                           '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                         }, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        #@log.debug "stuCohortAssoc->cohortId #{stu_assoc['body']['cohortId'].to_s}"
        #@log.debug "found assoc - #{stu_assoc['_id']} #{stu_assoc['body']['endDate']}"
        @db['staffCohortAssociation'].find({'body.cohortId'=> stu_assoc['body']['cohortId'],
                                              #'$or'=> [ {'body.studentRecordAccess'=> {'$exists'=> false}}, {'body.studentRecordAccess'=> true} ],
                                              'body.studentRecordAccess'=> true,
                                              '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                           }, @basic_options) { |staff_assoc_cursor|
          staff_assoc_cursor.each { |staff_assoc|
            #@log.debug "staffCohortAssoc->staffid #{staff_assoc['body']['staffId'].to_s}"
            #@log.debug "found assoc - #{staff_assoc['_id']} #{staff_assoc['body']['endDate']}"
            staff_assoc['body']['staffId'].each { |id|
              if @teacher_ids.has_key? id
                teachers.push staff_assoc['body']['staffId']
              end
            }
          }
        }
      }
    }
    #@log.debug "cohort teachers for #{studentId}: #{teachers.to_s}"
    teachers
  end

  def find_teachers_for_student_through_program(studentId)
    teachers = []
    @db['studentProgramAssociation'].find({'body.studentId'=> studentId,
                                            '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                          }, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        #@log.debug "stuProgramAssoc->programId #{stu_assoc['body']['programId'].to_s}"
        #@log.debug "found assoc - #{stu_assoc['_id']} #{stu_assoc['body']['endDate']}"
        @db['staffProgramAssociation'].find({'body.programId'=> {'$in'=> [stu_assoc['body']['programId']]},
                                              #'$or'=> [ {'body.studentRecordAccess'=> {'$exists'=> false}}, {'body.studentRecordAccess'=> true} ],
                                              'body.studentRecordAccess'=> true,
                                              '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                            }, @basic_options) { |staff_assoc_cursor|
          staff_assoc_cursor.each { |staff_assoc|
            #@log.debug "staffProgramAssoc->staffid #{staff_assoc['body']['staffId']}"
            #@log.debug "found assoc - #{staff_assoc['_id']} #{staff_assoc['body']['endDate']}"
            staff_assoc['body']['staffId'].each { |id|
              if @teacher_ids.has_key? id
                teachers.push staff_assoc['body']['staffId']
              end
            }
          }
        }
      }
    }
    #@log.debug "program teachers for #{studentId}: #{teachers.to_s}"
    teachers
  end

  def stamp_sections
    @log.info "Stamping sections and associations"
    section_to_teachers = {}
    @db['teacherSectionAssociation'].find({}, @basic_options) do |cursor|
      cursor.each do |tsa|
        stamp_context(@db['teacherSectionAssociation'], tsa, tsa['body']['teacherId'])
        sections = get_existing_context(@db['section'], tsa['body']['sectionId'])
        sections << tsa['body']['teacherId']
        sections.uniq!
        section_to_teachers[tsa['body']['sectionId']] ||= []
        section_to_teachers[tsa['body']['sectionId']] += sections
        stamp_full_context(@db['section'], tsa['body']['sectionId'], tsa['metaData']['tenantId'], sections)
      end
    end
    
    @db['studentSectionAssociation'].find({}, @basic_options) do |cursor|
      cursor.each do |ssa|
        students = @studentId_to_teachers[ssa['body']['studentId']]
        stamp_context(@db['studentSectionAssociation'], ssa, students)
        sections = get_existing_context(@db['section'], ssa['body']['sectionId'])
        sections += students unless students.nil?
        sections.uniq!
        section_to_teachers[ssa['body']['sectionId']] ||= []
        section_to_teachers[ssa['body']['sectionId']] += sections
        
        stamp_full_context(@db['section'], ssa['body']['sectionId'], ssa['metaData']['tenantId'], sections)
        @db['grade'].find({"body.studentSectionAssociationId" => ssa['_id']}, @basic_options) { |cursor| cursor.each {|grade| stamp_context(@db['grade'], grade, sections)}}
        @db['studentCompetency'].find({"body.studentSectionAssociationId" => ssa['_id']}, @basic_options) { |cursor| cursor.each {|competency| stamp_context(@db['studentCompetency'], competency, sections)}}
      end
    end

    @db[:gradebookEntry].find({}, {fields: ['body.sectionId', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |item|
        teachers = section_to_teachers[item['body']['sectionId']].flatten.uniq unless section_to_teachers[item['body']['sectionId']].nil?
        stamp_context(@db[:gradebookEntry], item, teachers)
      end
    end


    #Session related stuff
    #Map section to session
    session_to_teachers = {}
    session_to_tenant = {}
    @db[:section].find({}, @basic_options) do |cursor|
      cursor.each do |item|
        coursesOffering = get_existing_context(@db['courseOffering'], item['body']['courseOfferingId'])
        coursesOffering += item['metaData']['teacherContext'] unless !item.include? 'metaData' or !item['metaData'].include? 'teacherContext'
        coursesOffering.uniq!
        stamp_full_context(@db['courseOffering'], item['body']['courseOfferingId'], item['metaData']['tenantId'], coursesOffering)
        session_id = item['body']['sessionId']
        session_to_teachers[session_id] ||= []
        session_to_teachers[session_id].push item['metaData']['teacherContext']
        session_to_tenant[session_id] = item['metaData']['tenantId']        
      end
    end
    
    session_to_teachers.each { |session,teachers|
      teachers = teachers.flatten
      teachers = teachers.uniq
      teachers.reject!(&:nil?)
      stamp_full_context(@db['session'], session, session_to_tenant[session], teachers)
    }
    
    
    @db['session'].find({}, @basic_options) do |cursor|
      cursor.each do |session|
        next unless session.include? 'metaData' and session['metaData'].include? 'teacherContext' and !session['metaData']['teacherContext'].nil?  
        coursesOffering = get_existing_context(@db['courseOffering'], session['body']['courseOfferingId'])
        #puts "#{session['metaData']['teacherContext']}"
        coursesOffering += session['metaData']['teacherContext'] if session.include? 'metaData' and session['metaData'].include? 'teacherContext'
        coursesOffering.uniq!
        stamp_full_context(@db['gradingPeriod'], session['body']['gradingPeriodReference'], session['metaData']['tenantId'], session['metaData']['teacherContext'])
        @db['schoolSessionAssociation'].find({"body.sessionId" => session['_id']}, @basic_options) {|cursor| cursor.each {|ssa| stamp_context(@db['schoolSessionAssociation'], ssa, session['metaData']['teacherContext'])}}
        @db['courseOffering'].find({'body.sessionId'=> session['_id']}, @basic_options) {|cursor| cursor.each {|co| stamp_context(@db['courseOffering'], co, coursesOffering)}}
      end
    end
    
    course_to_teachers = {}
    course_to_tenant = {}
    @db['courseOffering'].find({}, @basic_options) { |cursor|
      cursor.each { |co|
        course_id = co['body']['courseId']
        course_to_teachers[course_id] ||= []
        course_to_teachers[course_id].push co['metaData']['teacherContext']
        course_to_tenant[course_id] = co['metaData']['tenantId']        
      }
    }
    
    course_to_teachers.each { |course,teachers|
      teachers = teachers.flatten
      teachers = teachers.uniq
      stamp_full_context(@db['course'], course, course_to_tenant[course], teachers)  
    }
    
  end

  def stamp_cohorts
    @log.info "Stamping cohorts and associations"

    cohort_to_teachers = {}
    cohort_to_tenant = {}

    @db['studentCohortAssociation'].find({}, {fields: ['_id', 'body.studentId', 'body.cohortId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        #@log.debug "studentCohortAssociation #{assoc['_id']} teacherContext #{teachers.to_s}"
        stamp_context(@db['studentCohortAssociation'], assoc, teachers)

        cohort_id = assoc['body']['cohortId']
        cohort_to_tenant[cohort_id] ||= assoc['metaData']['tenantId']
        cohort_to_teachers[cohort_id] ||= []
        cohort_to_teachers[cohort_id] += teachers unless teachers.nil?
        cohort_to_teachers[cohort_id] = cohort_to_teachers[cohort_id].flatten
        cohort_to_teachers[cohort_id] = cohort_to_teachers[cohort_id].uniq
      }
    }

    cohort_to_teachers.each { |cohort, teachers|
      stamp_full_context(@db['cohort'], cohort, cohort_to_tenant[cohort], teachers)
    }

    @db['staffCohortAssociation'].find({}, @basic_options) { |cursor|
      cursor.each { |assoc|
        teachers = []
        assoc['body']['cohortId'].each { |cohort| teachers += cohort_to_teachers[cohort] unless cohort_to_teachers[cohort].nil? }
        teachers = teachers.flatten
        teachers = teachers.uniq
        stamp_context(@db['staffCohortAssociation'], assoc, teachers)
      }
    }
  end

  def stamp_parents
    @log.info "Stamping parents and associations"
    @db['studentParentAssociation'].find({}, {fields: ['_id', 'body.studentId', 'body.parentId', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']] || []
        teachers += get_existing_context(@db['parent'], assoc['body']['parentId'])
        teachers.uniq!
        stamp_context(@db['studentParentAssociation'], assoc, teachers)
        stamp_full_context(@db['parent'], assoc['body']['parentId'], assoc['metaData']['tenantId'], teachers)
      end
    end
  end

  def stamp_programs
    @log.info "Stamping programs and associations"

    program_to_teachers = {}
    program_to_tenant = {}

    @db['studentProgramAssociation'].find({}, {fields: ['_id', 'body.studentId', 'body.programId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        #@log.debug "studentProgramAssociation #{assoc['_id']} teacherContext #{teachers.to_s}"
        stamp_context(@db['studentProgramAssociation'], assoc, teachers)

        program_id = assoc['body']['programId']
        program_to_tenant[program_id] ||= assoc['metaData']['tenantId']
        program_to_teachers[program_id] ||= []
        program_to_teachers[program_id] += teachers unless teachers.nil?
      }
    }

    program_to_teachers.each do |program, teachers|
      teachers = teachers.flatten
      teachers = teachers.uniq
      stamp_full_context(@db['program'], program, program_to_tenant[program], teachers)
    end

    @db['staffProgramAssociation'].find({}, @basic_options) do |cursor|
      cursor.each do |assoc|
        teachers = []
        assoc['body']['programId'].each { |program| teachers += program_to_teachers[program] unless program_to_teachers[program].nil? }
        teachers = teachers.flatten
        teachers = teachers.uniq
        stamp_context(@db['staffProgramAssociation'], assoc, teachers)
      end
    end
  end
  
  def stamp_attendance
    @log.info "Stamping attendance"
    @db['attendance'].find({}, {fields: ['body.studentId', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |attendance|
        teachers = @studentId_to_teachers[attendance['body']['studentId']].flatten.uniq
        teachers = teachers.flatten
        teachers = teachers.uniq
        stamp_context(@db['attendance'], attendance, teachers)
      end
    end
  end
  
  def stamp_disciplines
    @log.info "Stamping disciplineAction and disciplineIncident"
    @db['disciplineAction'].find({}, {fields: ['body.studentId', 'body.staffId', 'body.disciplineIncidentId', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |action|
        teachers = []
        action['body']['staffId'].each {|id| teachers << id}
        action['body']['studentId'].each {|id| teachers += @studentId_to_teachers[id] unless @studentId_to_teachers[id].nil?}
        teachers = teachers.flatten.uniq
        stamp_context(@db['disciplineAction'], action, teachers)
        action['body']['disciplineIncidentId'].each do |id|
          stamp_full_context(@db['disciplineIncident'], id, action['metaData']['tenantId'], teachers)
        end
      end
    end

    #TODO look more closely at these relationships and make sure they are stamped correctly.
    @log.info "Stamping studentDisciplineIncidentAssociation"
    @db['studentDisciplineIncidentAssociation'].find({}, @basic_options) do |cursor|
      cursor.each do |assoc|
        stamp_context(@db['studentDisciplineIncidentAssociation'], assoc, @studentId_to_teachers[assoc['body']['studentId']].flatten.uniq) if @studentId_to_teachers.has_key? assoc['body']['studentId']
        # @db['studentDisciplineIncidentAssociation'].update(make_ids_obj(assoc), {"$set" => {'metaData.teacherContext' => @studentId_to_teachers[assoc['body']['studentId']].flatten.uniq }}) unless @studentId_to_teachers[assoc['body']['studentId']].nil?
      end
    end
  end

  def stamp_assessments
    @log.info "Stamping assessment associations"
    assessment_to_teachers = {}
    assessment_to_tenant = {}

    @db['studentAssessmentAssociation'].find({}, {fields: ['_id', 'body.studentId', 'body.assessmentId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        #@log.debug "studentAssessmentAssociation #{assoc['_id']} teacherContext #{teachers.to_s}"
        stamp_context(@db['studentAssessmentAssociation'], assoc, teachers)

        assessment_id = assoc['body']['assessmentId']
        assessment_to_tenant[assessment_id] ||= assoc['metaData']['tenantId']
        assessment_to_teachers[assessment_id] ||= []
        assessment_to_teachers[assessment_id] += teachers unless teachers.nil?
      }
    }

    #not stamping assessments because they are public
    #assessment_to_teachers.each { |assessment, teachers|
    #  teachers = teachers.flatten
    #  teachers = teachers.uniq
    #  @db['assessment'].update({'_id'=> assessment, 'metaData.tenantId'=> assessment_to_tenant[assessment]}, {'$set' => {'metaData.teacherContext' => teachers}})
    #}
    # TODO sectionAssesmentAssociation?
  end

  def stamp_student_associations
    @log.info "Stamping student associations"

    @db['studentSchoolAssociation'].find({}, {fields: ['_id', 'body.studentId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        stamp_context(@db['studentSchoolAssociation'], assoc, teachers)
      }
    }

    @db['studentTranscriptAssociation'].find({}, @basic_options) do |cursor|
      cursor.each do |assoc|
        student_id = assoc['body']['studentId']
        record_id = assoc['body']['studentAcademicRecordId']
        tenant_id = assoc['metaData']['tenantId']
        teachers = []
        teachers += @studentId_to_teachers[student_id] unless student_id.nil? || @studentId_to_teachers[student_id].nil?
        unless record_id.nil?
          @db['studentAcademicRecord'].find({'_id'=>record_id, 'metaData.tenantId'=>tenant_id}, @basic_options) { |cursor|
            cursor.each { |record|
              student_id = record['body']['studentId']
              teachers += @studentId_to_teachers[student_id] unless student_id.nil? or @studentId_to_teachers[student_id].nil?
            }
          }
          teachers = teachers.flatten
          teachers = teachers.uniq
        end

        stamp_context(@db['studentTranscriptAssociation'], assoc, teachers)
      end
    end
    @studentId_to_teachers.each { |student,teachers|
      #TODO Add tenantId
      @db['studentTranscriptAssociation'].update({'body.studentId'=> student}, {'$set' => {'metaData.teacherContext' => teachers}})
    }
  end

  def stamp_other
    stamp_direct_student_association :reportCard
    stamp_direct_student_association :studentAcademicRecord
    stamp_direct_student_association :attendance
  end

  # Stamp non-association types that reference students through studentId
  def stamp_direct_student_association(type)
    @log.info "Stamping #{type}"
    @db[type].find({}, {fields: ['body.studentId', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |item|
        teachers = @studentId_to_teachers[item['body']['studentId']].flatten.uniq unless @studentId_to_teachers[item['body']['studentId']].nil?
        stamp_context(@db[type], item, teachers)
      end
    end
  end

  def stamp_gradebook
    @log.info "Stamping student section gradebook entry"

    @db['studentSectionGradebookEntry'].find({}, {fields: ['_id', 'body.studentId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        stamp_context(@db['studentSectionGradebookEntry'], assoc, teachers)
      }
    }
  end

  def stamp_teacher
    @log.info "Stamping teachers and staff and associations"

    school_to_teachers = {}
    teacher_to_schools = {}
    teacher_to_tenant = {}
    assoc_to_teacher = {}
    @db['teacherSchoolAssociation'].find({'$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]},
                                         {fields: ['_id', 'body.teacherId', 'body.schoolId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        school_id = assoc['body']['schoolId']
        teacher_id = assoc['body']['teacherId']
        school_to_teachers[school_id] ||= []
        school_to_teachers[school_id].push teacher_id
        teacher_to_schools[teacher_id] ||= []
        teacher_to_schools[teacher_id].push school_id

        tenant_id = assoc['metaData']['tenantId']
        teacher_to_tenant[teacher_id] = tenant_id

        assoc_to_teacher[assoc['_id']] = teacher_id
      }
    }

    # tag teachers
    @db['staff'].find({'type'=>'teacher'}, {fields: ['_id', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |teacher|
        teacher_id = teacher['_id']
        teachers = [teacher_id]

        schools = teacher_to_schools[teacher_id]
        unless schools.nil?
          schools.each { |school| teachers += school_to_teachers[school] unless school_to_teachers[school].nil?}
          teachers = teachers.flatten
          teachers = teachers.uniq
        end

        #@db['teacher'].update({'_id'=> teacher_id, 'metaData.tenantId'=> teacher_to_tenant[teacher_id]}, {'$set' => {'metaData.teacherContext' => teachers}})
        stamp_context(@db['staff'], teacher, teachers)
      }
    }

    # tag teacherSchoolAssociations
    assoc_to_teacher.each { |assoc_id,teacher_id|
      teachers = [teacher_id]
      schools = teacher_to_schools[teacher_id]
      unless schools.nil?
        schools.each { |school| teachers += school_to_teachers[school] unless school_to_teachers[school].nil?}
        teachers = teachers.flatten
        teachers = teachers.uniq
      end
      stamp_full_context(@db['teacherSchoolAssociation'], assoc_id, teacher_to_tenant[teacher_id], teachers)
    }

    # tag staff ed orgs
    # NOTE staff ed org tagging on basis that teachers are connected to ed orgs through teacherSchoolAssociation and staff through staffEdOrgAssociation
    staff_to_schools = {}
    staff_to_tenant = {}
    @log.info "Stamping staffEducationOrganizationAssociation"
    @db['staffEducationOrganizationAssociation'].find({}, @basic_options) do |cursor|
      cursor.each do |assoc|
        staff_id = assoc['body']['staffReference']
        ed_org_id = assoc['body']['educationOrganizationReference']
        tenant_id = assoc['metaData']['tenantId']
        stamp_context(@db['staffEducationOrganizationAssociation'], assoc, staff_id)
        if school_to_teachers.has_key? ed_org_id
          staff_to_schools[staff_id] ||= []
          staff_to_schools[staff_id].push ed_org_id
          staff_to_tenant[staff_id] = tenant_id
        end
      end
    end

    # tag non-teacher staff
    staff_to_schools.each { |staff_id, schools|
      teachers = []
      schools.each { |school| teachers << school_to_teachers[school] }
      teachers = teachers.flatten
      teachers = teachers.uniq
      stamp_full_context(@db['staff'], staff_id, staff_to_tenant[staff_id], teachers)
    }
  end

  def stamp_schools
    @log.info "Stamping schools"
    @db['educationOrganization'].find({'type'=>'school'}, {fields: ['_id', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |school|
        teachers = {}
        @db['teacherSchoolAssociation'].find({'body.schoolId'=>school['_id'], '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]},
                                             {fields: ['body.teacherId']}.merge(@basic_options)) { |cursor|
          cursor.each { |tsa|
            teacher_id = tsa['body']['teacherId']
            teachers[teacher_id] = true
          }
        }
        stamp_context(@db['educationOrganization'], school, teachers.keys)
        #@log.debug "school #{school['_id']} teachers #{teachers.keys}" 
      }
    }
  end

  private
  def make_ids_obj(record)
    obj = {}
    obj['_id'] = record['_id']
    if record.include? 'metaData' and record['metaData'].include? 'tenantId'
      obj['metaData.tenantId'] = record['metaData']['tenantId']
    else
      obj['metaData.tenantId'] = nil
    end
    obj
  end
  def get_existing_context(collection, id)
    context = []
    collection.find({"_id" => id}, {fields: ['metaData.teacherContext']}.merge(@basic_options)) {|cursor| cursor.each {|coll| context += coll['metaData']['teacherContext'] unless coll['metaData']['teacherContext'].nil?}}
    context
  end
  def stamp_context(collection, object, teachers)
    obj = make_ids_obj(object)
    stamp_full_context(collection, obj['_id'], obj['metaData.tenantId'], teachers)
  end
  def stamp_full_context(collection, id, tenant, teachers)
    #puts "stamping " + collection.name + ": " + id.to_s + " with tenant " + tenant + " for teachers " + teachers.to_s
    if tenant.nil?
      @log.warn "No tenant for #{collection.name}##{id}"
      # return
    end
    begin
      if !id.is_a? Array
        id = [id]
      end
      id.each do |i|
        collection.update({"_id" => i, "metaData.tenantId" => tenant}, {'$set' => {'metaData.teacherContext' => teachers}})
        @count += 1
        @log.info {"Stamping #{collection.name}"} if @count % 200 == 0
      end
    rescue Exception => e
      @log.warn e.message
    end
  end
end
