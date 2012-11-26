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
#require_relative '../Shared/EntityClasses/student.rb'
require_relative '../Shared/EntityClasses/baseEntity.rb'
require_relative '../Shared/EntityClasses/studentSchoolAssociation.rb'
require_relative '../Shared/EntityClasses/studentSectionAssociation.rb'

class StudentBuilder < BaseEntity
  
  class << self; attr_accessor :demographics end
  @@demographics = YAML.load_file File.join("#{File.dirname(__FILE__)}", "../Shared/choices.yml")   
  def self.demographics; @@demographics end
  
  attr_accessor :id, :rand, :work_order, :student_writer, :enrollment_writer, :sex, :birthDay,
                :firstName, :lastName, :address, :city, :state, :postalCode, :email, :hispanicLatino,
                :economicDisadvantaged, :schoolFood, :limitedEnglish, :race

  def initialize(work_order, year_of, files)
    @id = work_order[:id]
    @rand = Random.new(@id)
    @work_order = work_order
    @year_of = year_of
    @student_writer = files[:studentParent]
    @enrollment_writer = files[:enrollment]
  end

  def student
    # TODO : this method should go away, kept only for reference
    #@sex = choose(sex)
    #@birthDay = Date.new(@year_of) + @rand.rand(365)
    #@firstName =  choose(sex == "Male" ? maleNames : femaleNames)
    #@lastName = choose(lastNames)
    #@address = @rand.rand(999).to_s + " " + choose(["North Street", "South Lane", "East Rd", "West Blvd"])
    #@city = city
    #@state = state
    #@postalCode = postalCode
    #@email = email
    #@hispanicLatino = wChoose(hispanicLatinoDist)
    #@economicDisadvantaged = choose([true, false])
    #@schoolFood = wChoose(schoolFood)
    #@limitedEnglish =  wChoose(limitedEnglish)
    #@disability = wChoose(disability)
    #@race =  wChoose(raceDistribution)
  end
  
  def build
    # Pull student information into hashmap to be called by studentGenerator.rb
    #s = Student.new(@id, @work_order[:birth_day_after], @work_order[:demographics], @rand)
    #@student_writer.write(s.render)
    @work_order[:sessions].each{ |session|
      gen_enrollment(session)
    }
  end

  def gen_enrollment(session)
    school_id = session[:school]
    schoolAssoc = StudentSchoolAssociation.new(@id, school_id, @rand)
    @enrollment_writer.write(schoolAssoc.render)
    session[:sections].each{ |section|
      gen_section(section)
    }
  end

  def gen_section(section)
    sectionAssoc = StudentSectionAssociation.new(@id, section[:id], section[:edOrg], @rand)
    @enrollment_writer.write(sectionAssoc.render)
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
    Date.new(@year_of) + @rand.rand(365)
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
  
end
