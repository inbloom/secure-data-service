class PortalHeader < SessionResource
  self.site = APP_CONFIG['portal_url']
  self.url_type = "get-header"
  self.element_name = 'get-header'
  self.collection_name = 'get-header'
end
