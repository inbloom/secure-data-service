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



require_relative 'baseEntity'
class Parent < BaseEntity

  attr_accessor :id, :rand, :sex, :firstName, :middleName, :lastName, :suffix,
                :email, :loginId, :address, :city, :state, :postalCode,
                :otherName, :telephone

  def initialize(kid, type)
    @id = Parent.parentId(kid, type)
    @kid = kid
    @rand = @kid.rand
    @type = type
    buildParent
  
    optional {@otherName = {
        :otherNameType => choose([
          "Alias",
          "Nickname",
          "Other Name",
          "Previous Legal Name"]),
        :prefix => choose([
          "Colonel",
          "Dr",
          "Mr",
          "Mrs",
          "Ms",
          "Reverend",
          "Sr",
          "Sister"]),
        :firstName => choose(@sex == "Male" ? BaseEntity.demographics['maleNames'] :BaseEntity. demographics['femaleNames']),
        :middleName => choose(@sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames']),
        :lastName => choose(BaseEntity.demographics['lastNames']),
        :suffix => choose([
          "Jr",
          "Sr",
          "II",
          "III",
          "IV",
          "V",
          "VI",
          "VII",
          "VIII"])
      }
    }
      
    optional {@telephone = {
        :telephoneNumber => "(" + @rand.rand(1000).to_s.rjust(3, '0') + ")555-" + @rand.rand(10000).to_s.rjust(4, '0'),
        :telephoneNumberType => choose([
          "Fax",
          "Emergency 1",
          "Emergency 2",
          "Home",
          "Mobile",
          "Other",
          "Unlisted",
          "Work"]),
      :primaryTelephoneNumberIndicator => choose([false, true])
      }
    }

  end

  def buildParent
    @sex = @type == :mom ? "Female": "Male"
    @prefix = @sex == "Male?" ? "Mr" : "Ms"
    @firstName = choose(@sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
    @middleName = choose(@sex == "Male" ? BaseEntity.demographics['maleNames'] : BaseEntity.demographics['femaleNames'])
    @lastName = @kid.lastName
    @suffix = wChoose(BaseEntity.demographics['nameSuffix']) == "Jr" ? "Jr" : nil
    @email = @rand.rand(10000).to_s + BaseEntity.demographics['emailSuffix']
    @loginId = email
    @address = @kid.address
    @city = @kid.city
    @state = @kid.state
    @postalCode = @kid.postalCode
  end

  def self.parentId(kid, type)
    "#{kid.id}-#{type.to_s}"
  end

end
