module Stamper
  require 'logger'
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
    attr_accessor :db, :tenant, :log, :id
    # We have a number of important things that happen in the initialzer
    # First, we set the database object that has the open connection to mongo
    # Then we set the tenant that we will stamp on
    # Then we set the ID of the recently updated object to restamp
    # And finally an optional logger that will be defaulted to stdout on WARN
    # level
    def initialize(db, tenant, id, logger = nil)
      @id = id
      @tenant = tenant
      @basic_options = {:timeout => false, :batch_size => 100}
      @db = db
      @log = logger || Logger.new(STDOUT)
      @log.level = Logger::WARN if logger.nil?
      @stamps = {}
    end
    #This is the main method of any stamper class. It basically follows
    #a simple pattern of doing a pre-step to gather the edorgs and teachers to
    #stamp onto the document defined by the id and tenant.
    #subclasses will override
    def stamp
      stamp_id({"metaData.edOrgs" => get_edorgs})
      stamp_id({"metaData.teacherContext" => get_teachers})
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
    end
    def get_teachers
    end
    def wrap_up
    end
  end
end
