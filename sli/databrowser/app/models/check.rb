class Check < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/system/session".gsub("v1/", "")
  self.url_type = "check"
  self.format = ActiveResource::Formats::JsonFormat
end