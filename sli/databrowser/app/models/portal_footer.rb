require 'session_resource'
class PortalFooter < ActiveResource::Base
  self.site = APP_CONFIG['portal_url']
  self.element_name = 'get-footer'
  self.collection_name = 'get-footer'
  self.format = ActiveResource::Formats::PortalCrazyFormat
  def self.headers
    @headers = {}
  end
end
