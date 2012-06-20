require 'rubygems'
require 'mongo'
require 'benchmark'
require 'set'
require 'date'
require 'logger'

class SLCFixer
  attr_accessor :count, :db, :log

  def initialize(db, logger = nil, grace_period = 2000)
    @db = db
    @students = @db['student']
    @student_hash = {}
    @count = 0
    @basic_options = {:timeout => false, :batch_size => 100}
    @log = logger || Logger.new(STDOUT)

    @teacher_ids = {}
    @db['staff'].find({type: "teacher"}, {fields: ['_id', 'metaData.tenantId']}.merge(@basic_options)) do |cursor|
      cursor.each do |teacher|
        @teacher_ids[teacher['_id']] = teacher['metaData']['tenantId']
      end
    end

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
    end

    @threads.each do |th|
      th.join
    end

    finalTime = Time.now - time
    puts "\t Final time is #{finalTime} secs"
  end

  def stamp_students

    @studentId_to_teachers = {}
    
    @log.info "Stamping students"
    @db['student'].find({}, {fields: ['_id', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
      cursor.each { |student|
        studentId = student['_id']

        teacherIds = []
        teacherIds += find_teachers_for_student_through_section(studentId)
        teacherIds += find_teachers_for_student_through_program(studentId)
        teacherIds += find_teachers_for_student_through_cohort(studentId)
        teacherIds = teacherIds.uniq

        tenantId = student['metaData']['tenantId']
        @db['student'].update({'_id' => studentId, 'metaData.tenantId' => tenantId}, 
                              {'$set' => {'metaData.teacherContext' => teacherIds}})
      }
    }
  end

  def find_teachers_for_student_through_section(studentId)
    teachers = []
    @db['studentSectionAssociation'].find({'body.studentId'=> studentId}, @basic_options) { |ssa_cursor|
      ssa_cursor.each { |ssa|
        # TODO if time not passed: if (endDate == null || endDate.isEmpty() || (dateFilter.isFirstDateBeforeSecondDate(sectionGraceDate, endDate)))
        @db['teacherSectionAssociation'].find({'body.sectionId'=> ssa['body']['sectionId']}, @basic_options) { |tsa_cursor|
          tsa_cursor.each { |tsa|
            # TODO if time not passed: if (endDate == null || endDate.isEmpty() || (dateFilter.isFirstDateBeforeSecondDate(sectionGraceDate, endDate)))
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
    @db['studentCohortAssociation'].find({'body.studentId'=> {'$in'=> [studentId]}, '$or'=> 
                                         [
                                           {'body.endDate'=> {'$exists'=> false}}, 
                                           {'body.endDate'=> {'$gte'=> @current_date}}
                                         ]}, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        #@log.debug "stuCohortAssoc->cohortId #{stu_assoc['body']['cohortId'].to_s}"
        #@log.debug "found assoc - #{stu_assoc['_id']} #{stu_assoc['body']['endDate']}"
        @db['staffCohortAssociation'].find({'body.cohortId'=> stu_assoc['body']['cohortId'], 'body.studentRecordAccess'=> true, '$or'=>
                                         [
                                           {'body.endDate'=> {'$exists'=> false}}, 
                                           {'body.endDate'=> {'$gte'=> @current_date}}
                                         ]}, @basic_options) { |staff_assoc_cursor|
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
    @db['studentProgramAssociation'].find({'body.studentId'=> {'$in'=> [studentId]}, '$or'=> 
                                         [
                                           {'body.endDate'=> {'$exists'=> false}}, 
                                           {'body.endDate'=> {'$gte'=> @current_date}}
                                         ]}, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        #@log.debug "stuProgramAssoc->programId #{stu_assoc['body']['programId'].to_s}"
        #@log.debug "found assoc - #{stu_assoc['_id']} #{stu_assoc['body']['endDate']}"
        @db['staffProgramAssociation'].find({'body.programId'=> stu_assoc['body']['programId'], 'body.studentRecordAccess'=> true, '$or'=>
                                         [
                                           {'body.endDate'=> {'$exists'=> false}}, 
                                           {'body.endDate'=> {'$gte'=> @current_date}}
                                         ]}, @basic_options) { |staff_assoc_cursor|
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
#    @log.info "Finding directly referenced teacherSectionAssociations and sections"
#    @db['teacherSectionAssociation'].find({}, {fields: ['_id', 'body.teacherId', 'metaData.tenantId']}.merge(@basic_options)) { |cursor|
#      cursor.each { |assoc|
#         @db['teacherSectionAssociation'].update({"_id" => assoc['_id'], 'metaData.tenantId' => assoc['metaData']['tenantId']}, 
#                                                 {"$set" => {"metaData.teacherContext" => [assoc['body']['teacherId']]}})
#      }
#    }
  end

  def stamp_cohorts
#    @log.info "Stamping directly referenced staffCohortAssociations and cohorts"
#    @db['staffCohortAssociation'].find({}, {fields: ['_id', 'body.staffId']}.merge(@basic_options)) { |cursor|
#      cursor.each { |assoc|
#        assoc['body']['staffId'].each { |id|
#          puts 'match' if @teacher_ids.has_key? id
#        }
#      }
#    }
  end

  def stamp_programs
  end
end
