=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


module ActiveResource
  module Formats
    module JsonLinkFormat
      def self.decode(json)
        Formats.remove_root(ActiveSupport::JSON.decode(json))
      end
      def self.encode(hash, options = nil)
        ActiveSupport::JSON.encode(hash, options)
      end
      def self.extension
        "json"
      end
      def self.mime_type
        "application/vnd.slc+json"
      end
    end
  end
end

class SessionResource < ActiveResource::Base
  cattr_accessor :auth_id, :url_type, :access_token
  self.format = ActiveResource::Formats::JsonLinkFormat
  self.logger = Rails.logger
  class << self
    
    def headers
      if !self.access_token.nil?
        @headers = {"Authorization" => "Bearer #{self.access_token}"}
      else
        @headers = {}
      end
    end
    
    ## Remove format from the url.
     def element_path(id, prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{self.url_type}/#{id}#{query_string(query_options)}"
       Rails.logger.debug { "element_path: #{something}" }
       something
     end

     ## Remove format from the url.
     def collection_path(prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{self.url_type}#{query_string(query_options)}"
       Rails.logger.debug { "collection_path: #{something}" }
       something
     end
     
      ## Remove format from the url.
      def custom_method_collection_url(method_name, options = {})
        prefix_options, query_options = split_options(options)
        something = "#{prefix(prefix_options)}#{self.url_type}/#{method_name}#{query_string(query_options)}"
        Rails.logger.debug {"custom_method_collection_url: #{something}"}
        something
      end
  end
  

end
