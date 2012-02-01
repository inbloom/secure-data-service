class Realm < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/pub"
end
