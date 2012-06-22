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


class Entity < SessionResource
  self.site = APP_CONFIG['api_base']
  add_response_method :http_response
  
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
