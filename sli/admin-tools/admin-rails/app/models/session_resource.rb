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
      something = "#{prefix(prefix_options)}#{collection_name}/#{id}#{query_string(query_options)}"
      logger.debug { "element_path: #{something}" }
      something
    end

    ## Remove format from the url.
    def collection_path(prefix_options = {}, query_options = nil)
      prefix_options, query_options = split_options(prefix_options) if query_options.nil?
      something = "#{prefix(prefix_options)}#{collection_name}#{query_string(query_options)}"
      logger.debug { "collection_path: #{something}" }
      something
    end

    ## Remove format from the url.
    def custom_method_collection_url(method_name, options = {})
      prefix_options, query_options = split_options(options)
      something = "#{prefix(prefix_options)}#{collection_name}/#{method_name}#{query_string(query_options)}"
      logger.debug {"custom_method_collection_url: #{something}"}
      something
    end

  end

  def to_json(options={})
    self.attributes.to_json(options)
  end
end
