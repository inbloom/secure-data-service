class AdminDelegation < SessionResource

  self.site = "#{APP_CONFIG['api_base']}"
  self.collection_name = "adminDelegation"
  schema do
    string "localEdOrgId"
    boolean "ingestDataEnabled", "appApprovalEnabled", "viewSecurityEventsEnabled"
  end

end
