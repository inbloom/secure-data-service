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

# Create an Enum as a key-value store
# stolen shamelessly from: http://code.dblock.org/how-to-define-enums-in-ruby
module Enum
 
  def initialize(key, value)
    @key = key
    @value = value
  end
 
  def key
    @key
  end
 
  def value
    @value
  end
  
  def self.included(base)
    base.extend(ClassMethods)    
  end
 
  module ClassMethods
    def define(key, value)
      @hash ||= {}
      @hash[key] = self.new(key, value)
    end
 
    def const_missing(key)
      return nil if @hash[key] == nil
      @hash[key].value
    end    
 
    def each
      @hash.each do |key, value|
        yield key, value
      end
    end
 
    def all
      @hash.values
    end

    def get_key(value)
      items = []
      @hash.values.each { |item| items << item if item.value == value }
      return nil if items.size == 0 or items.size > 1
      items.first.key
    end
 
    def all_to_hash
      hash = {}
      each do |key, value|
        hash[key] = value.value
      end
      hash
    end
  end
end