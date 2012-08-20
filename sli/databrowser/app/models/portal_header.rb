require 'session_resource'
class PortalHeader < ActiveResource::Base
  self.site = APP_CONFIG['portal_url']
  self.element_name = 'get-header'
  self.collection_name = 'get-header'
  self.format = ActiveResource::Formats::PortalCrazyFormat
  def self.headers
    @headers = {}
  end
end
