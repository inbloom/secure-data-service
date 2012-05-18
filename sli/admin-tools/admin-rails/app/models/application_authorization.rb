class ApplicationAuthorization < SessionResource
  self.collection_name = "applicationAuthorization"
  schema do
    string "authId", "authType"
  end
  
  def apps_for_auths
    apps = App.all
    authorizedApps = self.appIds.map{|appId| apps.detect{|app| app.id == appId}}
    unauthorizedApps = apps.select {|app| self.appIds.find_index(app.id) == nil}
    apps = authorizedApps + unauthorizedApps
    apps
  end


end