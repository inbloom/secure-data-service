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

# This model represents talking to any Api endpoint and making adjustments
# to the data we get back so that we can do more clever rendering of that
# data.
class Entity < SessionResource
  self.site = APP_CONFIG['api_base']
  add_response_method :http_response
  
  # This method makes a call to get whatever it is we need from the Api
  # and proceeds to split the data up into two objects. Simple is for
  # the table view, and complex is a dump of the data
  def self.get_simple_and_complex(parameters)
    base = get("", parameters)
    entity = []
    if base.is_a?(Array) and !base.empty?
      type = nil
      return entity if base.first.nil?
      type = VIEW_CONFIG[base.first['entityType']] if base.first.has_key? 'entityType'
      base.each do |single|
        one = Hash.new
        one[:simple] = build_simple_hash(type, single)
        one[:complex] = single
        entity.push(one)
      end
    else
      entity.push({:complex => base, :simple => nil})
    end
    entity
  end

  # We use this method to go through the data, detecting it's type
  # and then building the tabular information we need to display.
  def self.build_simple_hash(type, hash)
    return nil if hash.nil?
    type = get_basic_types(hash) if type.nil?
    one = {}
    type.each do |item|
      final_type = item.split('/').last
      one[final_type] = value_for_simple_view(item, hash)
    end
    one
  end
  
  
  # This method looks through what we have configured as the path to get the
  # value out of our configuration and then digs through our hashmap to get
  # there.
  def self.value_for_simple_view (type, hash)
    return nil if hash.nil? or type.nil?
    return hash[type] unless type.include? '/'
    temp_hash = hash
    type_split = type.split '/'
    type_split.each do |split|
      if temp_hash.is_a?(Array)
        temp_hash = temp_hash.first[split]
      elsif temp_hash.has_key? split
        temp_hash = temp_hash[split]
      else
        return nil
      end
    end
    temp_hash
  end
  
  # If we don't have any preconfigured table rows we take the first 5 keys out
  # of our complex entity and use those instead
  def self.get_basic_types(hash)
    types = []
    hash.keys.each do |key|
      if !hash[key].is_a?(Hash) and !hash[key].is_a?(Array) and key != 'entityType'
        types.push(key)
      end
    end
    types.slice(0, 5)
  end
end
