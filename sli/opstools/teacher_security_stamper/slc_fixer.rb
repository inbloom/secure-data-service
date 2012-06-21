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
    #@log = logger || Logger.new(STDOUT)
    @log = Logger.new(STDOUT)

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
      @threads << Thread.new {x.report('students')    {stamp_students}}
    end

    @threads.each do |th|
      th.join
    end

    @threads = []
    Benchmark.bm(20) do |x|
      @threads << Thread.new {x.report('section')    {stamp_sections}}
      @threads << Thread.new {x.report('program')    {stamp_programs}}
      @threads << Thread.new {x.report('cohort')     {stamp_cohorts}}
      @threads << Thread.new {x.report('parent')     {stamp_parents}}
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
        teacherIds = teacherIds.flatten.uniq
        @studentId_to_teachers[studentId] = teacherIds

        @db['student'].update({'_id' => studentId, 'metaData.tenantId' => tenantId},
                              {'$set' => {'metaData.teacherContext' => teacherIds}})
      }
    }
  end

  def find_teachers_for_student_through_section(studentId)
    teachers = []
    @db['studentSectionAssociation'].find({'body.studentId'=> studentId, '$or'=>
                                         [
                                           {'body.endDate'=> {'$exists'=> false}},
                                           {'body.endDate'=> {'$gte'=> @grace_date}}
                                         ]}, @basic_options) { |ssa_cursor|
      ssa_cursor.each { |ssa|
        @db['teacherSectionAssociation'].find({'body.sectionId'=> ssa['body']['sectionId'], '$or'=>
                                         [
                                           {'body.endDate'=> {'$exists'=> false}},
                                           {'body.endDate'=> {'$gte'=> @grace_date}}
                                         ]}, @basic_options) { |tsa_cursor|
          tsa_cursor.each { |tsa|
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
    @db['studentCohortAssociation'].find({'body.studentId'=> {'$in'=> [studentId]},
                                           '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                         }, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        #@log.debug "stuCohortAssoc->cohortId #{stu_assoc['body']['cohortId'].to_s}"
        #@log.debug "found assoc - #{stu_assoc['_id']} #{stu_assoc['body']['endDate']}"
        @db['staffCohortAssociation'].find({'body.cohortId'=> stu_assoc['body']['cohortId'],
                                              '$or'=> [ {'body.studentRecordAccess'=> {'$exists'=> false}}, {'body.studentRecordAccess'=> true} ],
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
    @db['studentProgramAssociation'].find({'body.studentId'=> {'$in'=> [studentId]},
                                            '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                          }, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        #@log.debug "stuProgramAssoc->programId #{stu_assoc['body']['programId'].to_s}"
        #@log.debug "found assoc - #{stu_assoc['_id']} #{stu_assoc['body']['endDate']}"
        @db['staffProgramAssociation'].find({'body.programId'=> stu_assoc['body']['programId'],
                                              '$or'=> [ {'body.studentRecordAccess'=> {'$exists'=> false}}, {'body.studentRecordAccess'=> true} ],
                                              '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ]
                                            }, @basic_options) { |staff_assoc_cursor|
          staff_assoc_cursor.each { |staff_assoc|
            #@log.debug "staffProgramAssoc->staffid #{staff_assoc['body']['staffId'].to_s}"
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
    # TODO
#    @log.info "Finding directly referenced teacherSectionAssociations and sections"
#    @db['teacherSectionAssociation'].find({}, {fields: ['_id', 'body.teacherId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
#      cursor.each { |assoc|
#         @db['teacherSectionAssociation'].update({"_id" => assoc['_id'], 'metaData.tenantId' => assoc['metaData']['tenantId']},
#                                                 {"$set" => {"metaData.teacherContext" => [assoc['body']['teacherId']]}})
#      }
#    }
  end

  def stamp_cohorts
    @log.info "Stamping cohorts and associations"

    cohort_to_teachers = {}
    cohort_to_tenant = {}

    @db['studentCohortAssociation'].find({}, {fields: ['_id', 'body.studentId', 'body.cohortId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        @db['studentCohortAssociation'].update(make_ids_obj(assoc), {'$set' => {'metaData.teacherContext' => teachers}})
        #@log.debug "studentCohortAssociation #{assoc['_id']} teacherContext #{teachers.to_s}"
        cohort_id = assoc['body']['cohortId']
        cohort_to_tenant[cohort_id] ||= assoc['body']['tenant']
        cohort_to_teachers[cohort_id] ||= []
        cohort_to_teachers[cohort_id] += teachers
      }
    }

    cohort_to_teachers.each { |cohort, teachers|
      @db['cohort'].update({'_id'=> cohort, 'metaData.tenantId'=> cohort_to_tenant[cohort]}, {'$set' => {'metaData.teacherContext' => teachers}})
    }
  end

  def stamp_parents
    @log.info "Stamping parents and associations"

    parent_to_teachers = {}
    parent_to_tenant = {}
    @db['studentParentAssociation'].find({}, {fields: ['_id', 'body.studentId', 'body.parentId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |assoc|
        teachers = @studentId_to_teachers[assoc['body']['studentId']]
        parent_id = assoc['body']['parentId']
        parent_to_tenant[parent_id] ||= assoc['body']['tenant']
        parent_to_teachers[parent_id] ||= []
        parent_to_teachers[parent_id] += teachers
        @db['studentParentAssociation'].update(make_ids_obj(assoc), {'$set' => {'metaData.teacherContext' => teachers}})
      }
    }

    parent_to_teachers.each { |parent_id, teachers|
      @db['parent'].update({'_id' => parent_id, 'metaData.tenantId' => parent_to_tenant[parent_id]}, {'$set' => {'metaData.teacherContext' => teachers}})
    }
  end

  def stamp_programs
    # TODO
  end

  private
  def make_ids_obj(record)
    obj = {}
    obj['_id'] = record['_id']
    obj['metaData.tenantId'] = record['metaData']['tenantId']
    obj
  end
end
