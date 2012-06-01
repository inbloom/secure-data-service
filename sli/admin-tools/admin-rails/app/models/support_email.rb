class SupportEmail < SessionResource
  self.collection_name = "email"
  self.site = "#{APP_CONFIG['api_base']}/v1/system/support/"
end