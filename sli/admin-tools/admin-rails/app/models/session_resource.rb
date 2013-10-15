=begin

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
      # Always exclude links
      query_options[:excludeFields] = "links" if !query_options.has_key?(:excludeFields)
      something = "#{prefix(prefix_options)}#{collection_name}/#{id}#{query_string(query_options)}"
      logger.debug { "element_path: #{something}" }
      something
    end

    ## Remove format from the url.
    def collection_path(prefix_options = {}, query_options = nil)
      prefix_options, query_options = split_options(prefix_options) if query_options.nil?
      # Always exclude links
      query_options[:excludeFields] = "links" if !query_options.has_key?(:excludeFields)
      something = "#{prefix(prefix_options)}#{collection_name}#{query_string(query_options)}"
      logger.debug { "collection_path: #{something}" }
      # raise [ something.to_s(), prefix_options.to_s(), query_options.to_s() ].to_s()
      something
    end

    ## Remove format from the url.
    def custom_method_collection_url(method_name, options = {})
      prefix_options, query_options = split_options(options)
      # Always exclude links
      query_options[:excludeFields] = "links" if !query_options.has_key?(:excludeFields)
      something = "#{prefix(prefix_options)}#{collection_name}/#{method_name}#{query_string(query_options)}"
      logger.debug {"custom_method_collection_url: #{something}"}
      something
    end

  end

   # Util method to get an unlimited number of records in chunks since the API limits responses to a hardwired count
   def self.findAllInChunks(parameters)
     results = []
     offset = 0

     # API hard limit
     maxEntity = 20000
     parameters["limit"] = maxEntity

     # Always exclude links
     parameters[:excludeFields] = "links" if !parameters.has_key?(:excludeFields)
     
     begin
         parameters["offset"] = offset
         chunk = find(:all, :params => parameters)
         countInChunk = chunk.count
         offset += countInChunk
         results.concat(chunk)
     end until countInChunk < maxEntity
     return results
   end
  
  def to_json(options={})
    self.attributes.to_json(options)
  end
end
