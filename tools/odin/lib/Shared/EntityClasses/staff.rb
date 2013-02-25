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
                :birthDay, :email, :loginId, :address, :city, :state, :postalCode, :race, :hispanicLatino, :highestLevelOfEducationCompleted,
                :otherName, :telephone, :yearsOfPriorProfessionalExperience, :oldEthnicity, :yearsOfPriorTeachingExperience, :credentials

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
    
    optional {@oldEthnicity = choose([
        "American Indian Or Alaskan Native",
        "Asian Or Pacific Islander",
        "Black, Not Of Hispanic Origin",
        "Hispanic",
        "White, Not Of Hispanic Origin"])
    }
      
    optional {@yearsOfPriorProfessionalExperience = @rand.rand(20)}
      
    optional {@yearsOfPriorTeachingExperience = @rand.rand(20)}
    
    optional {@credentials = {
        :credentialType => choose([
          "Certification",
          "Endorsement",
          "Licensure",
          "Other",
          "Registration"]),
      :credentialFieldDescription => choose([
          "Mathematics",
          "Physics",
          "Early Childhood Education"]),
        :level => choose([
          "All Level (Grade Level PK-12)",
          "All-Level (Grade Level EC-12)",
          "Early Childhood (PK-K)",
          "Elementary (Grade Level 1-6)",
          "Elementary (Grade Level 1-8)",
          "Elementary (Grade Level 4-8)",
          "Elementary (Grade Level EC-4)",
          "Elementary (Grade Level EC-6)",
          "Elementary (Grade Level PK-5)",
          "Elementary (Grade Level PK-6)",
          "Grade Level NA",
          "Junior High (Grade Level 6-8)",
          "Secondary (Grade Level 6-12)",
          "Secondary (Grade Level 8-12)"]),
        :teachingCredentialType => choose([
          "Emergency",
          "Emergency Certified",
          "Emergency Non-Certified",
          "Emergency Teaching",
          "Intern",
          "Master",
          "Nonrenewable",
          "One Year",
          "Other",
          "Paraprofessional",
          "Professional",
          "Probationary",
          "Provisional",
          "Regular",
          "Retired",
          "Specialist",
          "Substitute",
          "TeacherAssistant",
          "Temporary",
          "Special Assignment",
          "Standard",
          "Standard Professional",
          "Temporary Classroom",
          "Temporary Exemption",
          "Unknown",
          "Unknown Permit",
          "Vocational",
          "Standard Paraprofessional",
          "Probationary Extension",
          "Probationary Second Extension",
          "Visiting International Teacher",
          "District Local"]),
        :credentialIssuanceDate => Date.new(2000+@rand.rand(12), 1+@rand.rand(12), 1+@rand.rand(28)),
        :credentialExpirationDate => Date.new(2013+@rand.rand(15), 1+@rand.rand(12), 1+@rand.rand(28)),
        :teachingCredentialBasis => choose([
          "4-year bachelor's degree",
          "5-year bachelor's degree",
          "Master's degree",
          "Doctoral degree",
          "Met state testing requirement",
          "Special/alternative program completion",
          "Relevant experience",
          "Credentials based on reciprocation with another state"])
      }
    }

  end

  # currently parses two 'word' names (first name and last name)
  def parse_name(name)
    parsed = name.split(' ')
    if parsed.size == 2
      return parsed[0], parsed[1]
    end
  end
end
