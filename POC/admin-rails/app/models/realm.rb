class Realm < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/"
  self.element_name = "realm"
  self.collection_name = "realm"
end
