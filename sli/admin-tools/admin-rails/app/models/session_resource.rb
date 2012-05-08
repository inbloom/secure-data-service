class SessionResource < ActiveResource::Base
  cattr_accessor :access_token
  self.logger = Rails.logger
  self.site = APP_CONFIG['api_base']

  class << self

    def headers
      if !access_token.nil?
        @headers = {"Authorization" => "Bearer #{self.access_token}"}
      else
        @headers = {}
      end
    end

    ## Remove format from the url.
     def element_path(id, prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{collection_name}/#{id}#{query_string(query_options)}"
       Rails.logger.debug { "element_path: #{something}" }
       something
     end

     ## Remove format from the url.
     def collection_path(prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{collection_name}#{query_string(query_options)}"
       Rails.logger.debug { "collection_path: #{something}" }
       something
     end
     
     ## Remove format from the url.
     def custom_method_collection_url(method_name, options = {})
       prefix_options, query_options = split_options(options)
       something = "#{prefix(prefix_options)}#{collection_name}/#{method_name}#{query_string(query_options)}"
       Rails.logger.debug {"custom_method_collection_url: #{something}"}
       something
     end
  end
end
