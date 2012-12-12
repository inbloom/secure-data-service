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

  attr_accessor :id, :rand, :sex, :firstName, :middleName, :lastName, :suffix,
                :email, :loginId, :address, :city, :state, :postalCode

  def initialize(kid, type)
    @id = "#{kid.id}-#{type.to_s}"
    @kid = kid
    @rand = @kid.rand
    @type = type
    buildParent
  end

  def buildParent
    @sex = @type == :mom ? "Female": "Male"
    @prefix = @sex == "Male?" ? "Mr" : "Ms"
    @firstName = choose(@sex == "Male" ? @@demographics['maleNames'] : @@demographics['femaleNames'])
    @middleName = choose(@sex == "Male" ? @@demographics['maleNames'] : @@demographics['femaleNames'])
    @lastName = @kid.lastName
    @suffix = wChoose(@@demographics['nameSuffix']) == "Jr" ? "Jr" : nil
    @email = @rand.rand(10000).to_s + @@demographics['emailSuffix']
    @loginId = email
    @address = @kid.address
    @city = @kid.city
    @state = @kid.state
    @postalCode = @kid.postalCode
  end

end
