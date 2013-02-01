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

# creates student
class Student < BaseEntity

  attr_accessor :id, :year_of, :rand, :sex, :firstName, :middleName, :lastName, :suffix,
                :birthDay, :email, :loginId, :address, :city, :state, :postalCode, :race, :hispanicLatino,
                :economicDisadvantaged, :limitedEnglish, :disability, :schoolFood
  def initialize(id, year_of)
    @id = id
    @year_of = year_of
    @rand = Random.new(@id)
    buildStudent
  end

  def buildStudent
    @sex = choose(BaseEntity.demographics['sex'])
    @prefix = sex == "Male?" ? "Mr" : "Ms"
    @firstName = choose(sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
    @middleName = choose(sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
    @lastName = choose(BaseEntity.demographics['lastNames'])
    @suffix = wChoose(BaseEntity.demographics['nameSuffix']) == "Jr" ? "Jr" : nil
    @birthDay = (@year_of + @rand.rand(365)).to_s
    @email = @rand.rand(10000).to_s + BaseEntity.demographics['emailSuffix']
    @loginId = email
    @address = @rand.rand(999).to_s + " " + choose(BaseEntity.demographics['street'])
    @city = BaseEntity.demographics['city']
    @state = BaseEntity.demographics['state']
    @postalCode = BaseEntity.demographics['postalCode']
    @race = wChoose(BaseEntity.demographics['raceDistribution'])
    @hispanicLatino = wChoose(BaseEntity.demographics['hispanicLatinoDist'])
    @economicDisadvantaged = wChoose(BaseEntity.demographics['economicDisadvantaged'])
    @limitedEnglish = wChoose(BaseEntity.demographics['limitedEnglish'])
    @disability = wChoose(BaseEntity.demographics['disability'])
    @schoolFood = wChoose(BaseEntity.demographics['schoolFood'])
  end

end
