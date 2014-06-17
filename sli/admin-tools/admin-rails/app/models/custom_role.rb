class CustomRole < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  self.site = APP_CONFIG['api_base']
  self.collection_name = 'customRoles'

  def realm_name
    begin
      Realm.find(self.realmId).name
    rescue => e
      logger.error e.message
      logger.error e.backtrace.join("\n")
    end
  end

  def self.defaults
    defs = []
    self.find(:all, :params => {'defaultsOnly' => true}).each do |role|
      defs.push({
        groupTitle:  role.groupTitle,
        names:       role.names,
        rights:      role.rights,
        isAdminRole: role.isAdminRole,
        selfRights:  role.selfRights
      })
    end
    defs
  end

end


