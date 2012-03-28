class Check < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/system/session".gsub("v1/", "")
  self.element_name = "check"
  self.collection_name = "check"
end
