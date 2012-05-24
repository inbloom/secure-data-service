class Logout < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/system/session".gsub("v1/", "")
  self.element_name = "logout"
  self.collection_name = "logout"
end
