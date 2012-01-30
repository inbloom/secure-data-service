class Role < SessionResource
  self.site = "https://testapi1.slidev.org/api/rest/admin"
  
  def self.get_static_information
    @roles = Role.all
    @roles.each do |role|
      role.examples = nil
      role.individual = nil
      case role.name
      when /Aggregator/
        role.examples = "State Data Analyst, State DOE Representative"
        role.individual = nil
      when /Leader/
        role.examples = "School Principal, District Superintendent, State Superintendent" 
        role.individual = "student enrolled in my district(s) or school(s)"
      when /Educator/
        role.examples = "Teacher, Athletic Coach, Classroom Assistant"
        role.individual = "student enrolled in my sections"
      when /IT Administrator/
        role.examples = "SLC Operator, SEA IT Admin, LEA IT Admin"
        role.individual = "student enrolled in my district(s) or school(s)"
      end
      role.general = []
      role.restricted = []
      role.aggregate = nil
      role.rights.each do |right|
        case right
        when /AGGREGATE_READ/
          role.aggregate = "yes"
        when /READ_GENERAL/
          role.general << "R"
        when /READ_RESTRICTED/
          role.restricted << "R"
        when /WRITE_GENERAL/
          role.general << "W"
        when /WRITE_RESTRICTED/
          role.restricted << "W"
        end    
      end
      role.general = role.general.join('/')
      role.restricted = role.restricted.join('/')
    end
    @roles
  end
end
