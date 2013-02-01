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

require 'date'
require 'yaml'

require_relative '../data_utility.rb'
require_relative 'baseEntity'

# creates staff
class Staff < BaseEntity

  attr_accessor :id, :staffIdentificationCode, :identificationSystem, :year_of, :rand, :sex, :firstName, :middleName, :lastName, :suffix,
                :birthDay, :email, :loginId, :address, :city, :state, :postalCode, :race, :hispanicLatino, :highestLevelOfEducationCompleted

  def initialize(id, year_of, name = nil)
    @id = DataUtility.get_staff_unique_state_id(id) if id.kind_of? Integer
    @id = id if id.kind_of? String
    @year_of = year_of
    @name = name
    if id.kind_of? String
      @rand = Random.new(year_of)
    else
      @rand = Random.new(id)
    end
    buildStaff
  end

  def buildStaff
    @staffIdentificationCode = @rand.rand(10000).to_s
    @identificationSystem = choose(BaseEntity.demographics['identificationSystem'])
    @highestLevelOfEducationCompleted = choose(BaseEntity.demographics['highestLevelOfEducationCompleted'])
 
    if @name.nil?
      @sex = choose(BaseEntity.demographics['sex'])
      @prefix = sex == "Male?" ? "Mr" : "Ms"
      @firstName = choose(sex == "Male" ? BaseEntity.demographics['maleNames'] :BaseEntity. demographics['femaleNames'])
      @middleName = choose(sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
      @lastName = choose(BaseEntity.demographics['lastNames'])
      @suffix = wChoose(BaseEntity.demographics['nameSuffix']) == "Jr" ? "Jr" : nil
    else
      @firstName, @lastName = parse_name(@name)
      if BaseEntity.demographics['maleNames'].include?(@firstName)
        @sex = "Male"
        @prefix = "Mr"
        @middleName = choose(BaseEntity.demographics['maleNames'])
        @suffix = wChoose(BaseEntity.demographics['nameSuffix']) == "Jr" ? "Jr" : nil
      elsif BaseEntity.demographics['femaleNames'].include?(@firstName)
        @sex = "Female"
        @prefix = "Ms"
        @middleName = choose(BaseEntity.demographics['femaleNames'])
        @suffix = wChoose(BaseEntity.demographics['nameSuffix']) == "Jr" ? "Jr" : nil
      else
        @sex = choose(BaseEntity.demographics['sex'])
        @prefix = sex == "Male?" ? "Mr" : "Ms"
        @firstName = choose(sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
        @middleName = choose(sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
        @lastName = choose(BaseEntity.demographics['lastNames'])
        @suffix = wChoose(BaseEntity.demographics['nameSuffix']) == "Jr" ? "Jr" : nil
      end
    end
    @birthDay = (Date.new(@year_of, 1, 1) + @rand.rand(365)).to_s
    @email = @rand.rand(10000).to_s + BaseEntity.demographics['emailSuffix']
    @loginId = email
    @address = @rand.rand(999).to_s + " " + choose(BaseEntity.demographics['street'])
    @city = BaseEntity.demographics['city']
    @state = BaseEntity.demographics['state']
    @postalCode = BaseEntity.demographics['postalCode']
    @race = wChoose(BaseEntity.demographics['raceDistribution'])
    @hispanicLatino = wChoose(BaseEntity.demographics['hispanicLatinoDist'])
  end

  # currently parses two 'word' names (first name and last name)
  def parse_name(name)
    parsed = name.split(' ')
    if parsed.size == 2
      return parsed[0], parsed[1]
    end
  end
end
