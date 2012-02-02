class Realm < SessionResource

  self.site = "#{APP_CONFIG['api_base']}"
  self.collection_name = "realm"

end
