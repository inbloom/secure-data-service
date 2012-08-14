class PortalFooter < SessionResource
  self.site = APP_CONFIG['portal_url']
  self.element_name = 'get-footer'
  self.url_type = "get-footer"
  self.collection_name = 'get-footer'
end
