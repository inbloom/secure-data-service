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

require 'yaml'
require_relative '../demographics'

# base entity
class BaseEntity

  def self.initializeDemographics(demographics, choices)
    @@d = Demographics.new(demographics, choices)
  end

  def self.demographics
    @@d
  end

  def self.set_scenario(scenario)
    @@scenario = scenario
  end

  def choose(options)
    options[@rand.rand(options.size) - 1]
  end

  def wChoose(distribution)
    r = @rand.rand weight_total(distribution)
    distribution.each do |element, weight|
      if r < weight
      return element
      end
      r -= weight
    end
  end

  def wChooseUsingRand(prng, distribution)
    r = prng.rand weight_total(distribution)
    distribution.each do |element, weight|
      if r < weight
      return element
      end
      r -= weight
    end
  end

  def weight_total(distribution)
    sum = 0
    distribution.each do |_, weight|
      sum +=weight
    end
    sum
  end
  
  def bit_choose() 
    rand(2) == 1
  end

  def bit_choose()
    @rand.rand(2) == 1
  end

end
