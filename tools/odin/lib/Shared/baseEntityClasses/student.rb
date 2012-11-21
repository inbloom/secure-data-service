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


require_relative './baseEntity.rb'

class Student < BaseEntity
  
  attr_accessor :id, :sex, :birthDay

  def initialize(id, year_of, demographics, rand)
    @id = id
    @demographics = demographics
    @rand = rand
    @sex = choose(["Male", "Female"])
    @birthDay = year_of + @rand.rand(365)
  end

  def firstName
    choose(sex == "Male" ? @demographics.maleNames : @demographics.femaleNames)
  end

  def lastName
    choose(@demographics.lastNames)
  end

  def address
    @rand.rand(999).to_s + " " + choose(["North Street", "South Lane", "East Rd", "West Blvd"])
  end

  def city
    @demographics.city
  end

  def state
    @demographics.state
  end

  def postalCode
    @demographics.postalCode
  end

  def email
    @rand.rand(10000).to_s + "@fakemail.com"
  end

  def hispanicLatino
    #choose([true, false])
    wChoose(@demographics.hispanicLatinoDist)
  end

  def economicDisadvantaged
    #choose([true, false])
    wChoose(@demographics.economicDisadvantaged)
  end

  def schoolFood
    #choose(['Free', 'Full price', 'Reduced price', 'Unknown'])
    wChoose(@demographics.schoolFood)
  end

  def limitedEnglish
    #choose(['Limited', 'Limited Monitored 1', 'Limited Monitored 2', 'NotLimited'])
    wChoose(@demographics.limitedEnglish)
  end
  
  def disability
    #choose([true, false])
    wChoose(@demographics.disability)
  end

  def race
    #choose(['American Indian - Alaskan Native', 'Asian', 'Black - African American', 'Native Hawaiian - Pacific Islander', 'White'])
    wChoose(@demographics.raceDistribution)
  end
  
  def genRace
    wChoose(@demographics.raceDistribution)
  end
  
  def distributionTester(inMethod, tracer, lo, hi, iters)
    i = 0
    hit = 0
    while i < iters do
      if inMethod.call == tracer
        hit += 1
      end
      i += 1
    end
    
    if hit.between?(lo, hi)
      puts "THE HIT RATIO WAS WITHIN EXPECTED VALUES. #{tracer} was #{hit}/#{iters}."
      return "true"
    else
      puts "FAIL. #{tracer} came back #{hit} times out of #{iters}"
      return "false"
    end
  end
end
