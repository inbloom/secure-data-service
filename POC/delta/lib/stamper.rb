module Stamper
  require 'logger'
  require 'date'
  def find_teachers_for_student_through_section
    teachers = []
    @db['studentSectionAssociation'].find({'metaData.tenantId' => @tenant, 'body.studentId'=> @id, '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @grace_date}} ] }, @basic_options) { |ssa_cursor|
      ssa_cursor.each { |ssa|
        @db['teacherSectionAssociation'].find({'metaData.tenantId' => @tenant, 'body.sectionId'=> ssa['body']['sectionId'], '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @grace_date}} ] }, @basic_options) { |tsa_cursor|
          tsa_cursor.each { |tsa|
            teachers.push tsa['body']['teacherId']
          }
        }
      }
    }
    teachers
  end

  def find_teachers_for_student_through_cohort
    teachers = []
    @db['studentCohortAssociation'].find({'metaData.tenantId' => @tenant, 'body.studentId'=> @id, '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ] }, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        @db['staffCohortAssociation'].find({'metaData.tenantId' => @tenant, 'body.cohortId'=> stu_assoc['body']['cohortId'], 'body.studentRecordAccess'=> true, '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ] }, @basic_options) { |staff_assoc_cursor|
          staff_assoc_cursor.each { |staff_assoc|
            staff_assoc['body']['staffId'].each { |id|
              if @teacher_ids.has_key? id
                teachers.push staff_assoc['body']['staffId']
              end
            }
          }
        }
      }
    }
    teachers
  end

  def find_teachers_for_student_through_program
    teachers = []
    @db['studentProgramAssociation'].find({'metaData.tenantId' => @tenant, 'body.studentId'=> @id,'$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ] }, @basic_options) { |stu_assoc_cursor|
      stu_assoc_cursor.each { |stu_assoc|
        @db['staffProgramAssociation'].find({'metaData.tenantId' => @tenant, 'body.programId'=> {'$in'=> [stu_assoc['body']['programId']]}, 'body.studentRecordAccess'=> true, '$or'=> [ {'body.endDate'=> {'$exists'=> false}}, {'body.endDate'=> {'$gte'=> @current_date}} ] }, @basic_options) { |staff_assoc_cursor|
          staff_assoc_cursor.each { |staff_assoc|
            staff_assoc['body']['staffId'].each { |id|
              if @teacher_ids.has_key? id
                teachers.push staff_assoc['body']['staffId']
              end
            }
          }
        }
      }
    }
    teachers
  end

  def get_entity(collection, id = nil)
    id = @id if id.nil?
    @log.info "Looking for #{collection}##{id}"
    @db[collection].find_one({"_id" => id, "metaData.tenantId" => @tenant}, @basic_options)
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

        stamp_id(@db['educationOrganization'], id, id, edorg['metaData']['tenantId'])
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
  def stamp_id(updateHash)
    @db[self.class::COLLECTION].update({"_id" => @id, "metaData.tenantId" => @tenant}, updateHash)
  end
  class BaseStamper
    include Stamper
    COLLECTION = "UNDEFINED"
    attr_accessor :db, :tenant, :log, :id, :grace_period
    # We have a number of important things that happen in the initialzer
    # First, we set the database object that has the open connection to mongo
    # Then we set the tenant that we will stamp on
    # Then we set the ID of the recently updated object to restamp
    # And finally an optional logger that will be defaulted to stdout on WARN
    # level
    def initialize(db, id, tenant, grace_period = 2000, logger = nil)
      @id = id
      @grace_period = grace_period
      @grace_date = (Date.today - grace_period).to_s
      @tenant = tenant
      @basic_options = {:timeout => false, :batch_size => 100}
      @db = db
      @log = logger || Logger.new(STDOUT)
      @log.level = Logger::DEBUG if logger.nil?
      @stamps = {}
    end
    #This is the main method of any stamper class. It basically follows
    #a simple pattern of doing a pre-step to gather the edorgs and teachers to
    #stamp onto the document defined by the id and tenant.
    #subclasses will override
    def stamp
      edorgs = get_edorgs
      teachers = get_teachers

      stamp_id({"metaData.edOrgs" => edorgs})
      stamp_id({"metaData.teacherContext" => teachers})
      wrap_up
    end
    def get_edorgs
      raise "Not implemented"
    end
    def get_teachers
      raise "Not implemented"
    end
    def wrap_up
      raise "Not implemented"
    end
  end

  class StudentStamper < BaseStamper
    COLLECTION = "student"
    def get_edorgs
      edorgs = []
      @log.info "Searching StudentSchoolAssociations for studentId #{@id}"
      @db['studentSchoolAssociation'].find({"body.studentId" => @id, "metaData.tenantId" => @tenant}, @basic_options) do |cur|
        cur.each do |student|
          edorgs << student['body']['schoolId'] unless student['body'].has_key? 'exitWithdrawDate' and Date.parse(student['body']['exitWithdrawDate']) <= Date.today - @grace_period
        end
      end
      edorgs.flatten.uniq
    end

    def get_teachers
      teachers = []
      teachers << find_teachers_for_student_through_section
      teachers << find_teachers_for_student_through_cohort
      teachers << find_teachers_for_student_through_program
      teachers.flatten.uniq
    end

    def wrap_up
    end
    private
  end
end
