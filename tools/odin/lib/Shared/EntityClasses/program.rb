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

require_relative '../data_utility.rb'
require_relative 'baseEntity.rb'
require_relative 'enum/ProgramSponsorType.rb'
require_relative 'enum/ProgramType.rb'

# creates a program
class Program < BaseEntity
  
  attr_accessor :id, :type, :sponsor, :services

  def initialize(prng, id, type = nil, sponsor = nil)
    @rand = prng

    @id = id

    if type.nil? 
      @type = choose(ProgramType.all).value
    else
      @type = ProgramType.to_string(type)
    end
    
    if sponsor.nil?
      @sponsor = choose(ProgramSponsorType.all).value
    else
      @sponsor = ProgramSponsorType.to_string(sponsor)
    end
    
    @services = ["srv:" + id.to_s]
  end
end
