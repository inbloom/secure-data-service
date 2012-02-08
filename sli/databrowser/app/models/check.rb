class Check < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/system/session"
  self.url_type = "check"
end