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

require_relative 'baseEntity'
require_relative '../../EntityCreation/student_builder'
#require_relative '../demographics'

class Student < BaseEntity
  
  attr_accessor :id, :sex, :birthDay, :firstName, :lastName, :address, :city, :state, :postalCode,
                :email, :hispanicLatino, :economicDisadvantaged, :schoolFood, :limitedEnglish, :race

  def initialize(id, year_of, builder, rand)
    # TODO : most, if not all of this information, should be set by the entity creator code.
    @id = id
    @rand = rand
    @sex = choose(StudentBuilder.sex)
    @birthDay = Date.new(year_of) + @rand.rand(365)
    @firstName =  choose(sex == "Male" ? StudentBuilder.maleNames : StudentBuilder.femaleNames)
    @lastName = choose(StudentBuilder.lastNames)
    @address = @rand.rand(999).to_s + " " + choose(["North Street", "South Lane", "East Rd", "West Blvd"])
    @city = StudentBuilder.city
    @state =  StudentBuilder.state
    @postalCode = StudentBuilder.postalCode
    @email = StudentBuilder.email
    @hispanicLatino = wChoose(StudentBuilder.hispanicLatinoDist)
    @economicDisadvantaged = choose([true, false])
    @schoolFood = wChoose(StudentBuilder.schoolFood)
    @limitedEnglish =  wChoose(StudentBuilder.limitedEnglish)
    @disability = wChoose(StudentBuilder.disability)
    @race =  wChoose(StudentBuilder.raceDistribution)
  end


end
