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


class ApplicationAuthorization < SessionResource
  cattr_accessor :cur_edorg

  self.collection_name = "applicationAuthorization"
  schema do
    string "appId"
    boolean "authorized"
  end

 class << self

    def element_path(id, prefix_options = {}, query_options = nil)
      prefix_options, query_options = split_options(prefix_options) if query_options.nil?
      if cur_edorg != nil
        query_options[:edorg] = cur_edorg
      end
      something = "#{prefix(prefix_options)}#{collection_name}/#{id}#{query_string(query_options)}"
      logger.debug { "element_path: #{something}" }
      something
    end

 end

end
