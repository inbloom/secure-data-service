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
class Parent < BaseEntity

  attr_accessor :id, :year_of, :rand, :sex, :firstName, :middleName, :lastName, :suffix,
                :birthDay, :email, :loginId, :address, :city, :state, :postalCode, :race, :hispanicLatino,
                :economicDisadvantaged, :limitedEnglish, :disability, :schoolFood
  def initialize(id, year_of)
    @id = id
    @year_of = year_of
    @rand = Random.new(@id)
    buildParent
  end

  def buildParent
    @sex = choose(@@demographics['sex'])
    @prefix = sex == "Male?" ? "Mr" : "Ms"
    @firstName = choose(sex == "Male" ? @@demographics['maleNames'] : @@demographics['femaleNames'])
    @middleName = choose(sex == "Male" ? @@demographics['maleNames'] : @@demographics['femaleNames'])
    @lastName = choose(@@demographics['lastNames'])
    @suffix = wChoose(@@demographics['nameSuffix']) == "Jr" ? "Jr" : nil
    @birthDay = (@year_of + @rand.rand(365)).to_s
    @email = @rand.rand(10000).to_s + @@demographics['emailSuffix']
    @loginId = email
    @address = @rand.rand(999).to_s + " " + choose(@@demographics['street'])
    @city = @@demographics['city']
    @state = @@demographics['state']
    @postalCode = @@demographics['postalCode']
    @race = wChoose(@@demographics['raceDistribution'])
    @hispanicLatino = wChoose(@@demographics['hispanicLatinoDist'])
    @economicDisadvantaged = wChoose(@@demographics['economicDisadvantaged'])
    @limitedEnglish = wChoose(@@demographics['limitedEnglish'])
    @disability = wChoose(@@demographics['disability'])
    @schoolFood = wChoose(@@demographics['schoolFood'])
  end

end
