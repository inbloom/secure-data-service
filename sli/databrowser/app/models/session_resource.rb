=begin
#--

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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
#++
# Monkey-Patch to ActiveResource to add custom formats/media-types
module ActiveResource
  # Formats are the module in ActiveResource that it knows about internally
  module Formats
    # This is a monkey-patch to add the custom media types to ActiveResource.
    module JsonLinkFormat
      # Override to handle json
      def self.decode(json)
        Formats.remove_root(ActiveSupport::JSON.decode(json))
      end
      # Override to handle json
      def self.encode(hash, options = nil)
        ActiveSupport::JSON.encode(hash, options)
      end
      # Override to handle json
      def self.extension
        "json"
      end
      # The only difference between the standard +application/json+ media type
      # and this is here
      def self.mime_type
        "application/vnd.slc+json"
      end
    end
    module PortalCrazyFormat
      # Override to handle json
      def self.decode(json)
        #json.gsub(/\\[nt]*/, "")
        json.gsub!(/\\[tn]*/,"")[1, json.size-2].html_safe
      end
      # Override to handle json
      def self.encode(hash, options = nil)
        ActiveSupport::JSON.encode(hash, options)
      end
      # Override to handle json
      def self.extension
       "" 
      end
      # The only difference between the standard +application/json+ media type
      # and this is here
      def self.mime_type
        "application/vnd.slc+json"
      end
    end
  end
end


#This class is how we make the rails ActiveResource talk to our Api.
#Any models we make that would talk to the Api should inherit from this
#model. It handles passing your OAuth token, as well as our endpoint
#conventions, and the media-types.
class SessionResource < ActiveResource::Base
  cattr_accessor :auth_id, :url_type, :access_token
  self.format = ActiveResource::Formats::JsonLinkFormat
  self.logger = Rails.logger
  class << self
    
    # We add the oauth access token we got from handle_oauth to the header of
    # all outbound ActiveResource API calls here.
    def headers
      if !self.access_token.nil?
        @headers = {"Authorization" => "Bearer #{self.access_token}"}
      else
        @headers = {}
      end
    end
    
    # Remove format from the url.
     def element_path(id, prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{self.url_type}/#{id}#{query_string(query_options)}"
       Rails.logger.debug { "element_path: #{something}" }
       something
     end

     # Remove format from the url.
     def collection_path(prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{self.url_type}#{query_string(query_options)}"
       Rails.logger.debug { "collection_path: #{something}" }
       something
     end
     
      # Remove format from the url.
      def custom_method_collection_url(method_name, options = {})
        prefix_options, query_options = split_options(options)
        something = "#{prefix(prefix_options)}#{self.url_type}/#{method_name}#{query_string(query_options)}"
        Rails.logger.debug {"custom_method_collection_url: #{something}"}
        something
      end
  end
  

end
