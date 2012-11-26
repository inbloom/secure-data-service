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
require_relative 'baseEntity'
#require_relative '../../EntityCreation/student_builder'
#require_relative '../Shared/EntityClasses/student.rb'

class Student < BaseEntity
  
  class << self; attr_accessor :demographics end
  @@demographics = YAML.load_file File.join("#{File.dirname(__FILE__)}", "../choices.yml")   
  def self.demographics; @@demographics end
  
  attr_accessor :id, :year_of, :rand, :sex, :firstName, :middleName, :lastName, :suffix, 
                :birthDay, :email, :address, :city, :state, :postalCode, :race, :hispanicLatino,
                :economicDisadvantaged, :limitedEnglish, :disability, :schoolFood          

  def initialize(id, year_of)
    @id = id
    @year_of = year_of
    @rand = Random.new(@id)
  end

  def sex
    choose(@@demographics['sex'])
  end
  
  def prefix
    sex == "Male?" ? "Mr." : "Ms."
  end
    
  def firstName
    choose(sex == "Male" ? @@demographics['maleNames'] : @@demographics['femaleNames'])
  end
  
  def middleName
    choose(sex == "Male" ? @@demographics['maleNames'] : @@demographics['femaleNames'])
  end
  
  def lastName
    choose(@@demographics['lastNames'])
  end
  
  def suffix
    wChoose(@@demographics['nameSuffix'])
  end
  
  def birthDay
    @year_of + @rand.rand(365)
  end
    
  def email
    @rand.rand(10000).to_s + @@demographics['emailSuffix']
  end
  
  def loginId
    email
  end
  
  def address
   @rand.rand(999).to_s + " " + choose(@@demographics['street'])
  end
  
  def city
    @@demographics['city']
  end
  
  def state
    @@demographics['state']
  end
  
  def postalCode
    @@demographics['postalCode']
  end
    
  def race
    wChoose(@@demographics['raceDistribution'])
  end
  
  def hispanicLatino
    wChoose(@@demographics['hispanicLatinoDist'])
  end
  
  def economicDisadvantaged
    wChoose(@@demographics['economicDisadvantaged'])
  end
  
  def limitedEnglish
    wChoose(@@demographics['limitedEnglish'])
  end
  
  def disability
    wChoose(@@demographics['disability'])
  end
  
  def schoolFood
    wChoose(@@demographics['schoolFood'])
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
      puts "PASS: #{tracer} was #{hit}/#{iters}, expected %0.1f \%" % ((((hi.to_f+lo)/2)/iters) * 100) 
      return "true"
    else
      puts "FAIL: #{tracer} was #{hit}/#{iters}, expected %0.1f \%" % ((((hi.to_f+lo)/2)/iters) * 100)
      return "false"
    end
  end
  
  # TODO : most, if not all of this information, should be set by the entity creator code.
  # TODO:  placeholder method until this is completed.
  def randomize
    @sex = @build.sex
    @prefix = @build.prefix
    @firstName = @build.firstName
    @middleName = @build.middleName
    @lastName = @build.lastName
    @suffix = @build.suffix
    @birthDay = @build.birthDay
    @email = @build.email
    @address = @build.address
    @city = @build.city
    @state = @build.state
    @postalCode = @build.postalCode
    @race = @build.race
    @hispanicLatino = @build.hispanicLatino
    @economicDisadvantaged = @build.economicDisadvantaged
    @limitedEnglish = @build.limitedEnglish
    @disability = @build.disability
    @schoolFood = @build.schoolFood
  end

end
