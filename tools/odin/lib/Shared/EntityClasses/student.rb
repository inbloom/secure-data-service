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

# creates student
class Student < BaseEntity

  attr_accessor :id, :year_of, :rand, :sex, :firstName, :middleName, :lastName, :suffix,
                :birthDay, :email, :loginId, :address, :city, :state, :postalCode, :race, :hispanicLatino,
                :economicDisadvantaged, :limitedEnglish, :disability, :schoolFood,
                :studentIdentificationCode, :otherName, :telephone, :profileThumbnail,
                :oldEthnicity, :economicDisadvantaged, :studentCharacteristics,
                :languages, :homeLanguages, :disabilities, :section504Disabilities,
                :displacementStatus, :programParticipations, :learningStyles,
                :cohortYears, :studentIndicators
                
  def initialize(id, year_of)
    @id = id
    @year_of = year_of
    @rand = Random.new(@id)
    buildStudent

    optional {@studentIdentificationCode = {
        :identificationCode => "abcde",
        :identificationSystem => choose([
          "Canadian SIN",
          "District",
          "Family",
          "Federal",
          "Local",
          "National Migrant",
          "Other",
          "School",
          "SSN",
          "State",
          "State Migrant"
        ]),
      :assigningOrganizationCode => choose(["District", "School", "Other"])
      }
    }
    
    optional {@otherName = {
        :otherNameType => choose([
          "Alias",
          "Nickname",
          "Other Name",
          "Previous Legal Name"]),
        :prefix => choose([
          "Mr",
          "Mrs",
          "Ms"]),
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

    optional {@telephone = "(" + @rand.rand(1000).to_s.rjust(3, '0') + ")555-" + @rand.rand(10000).to_s.rjust(4, '0')}

    optional {@profileThumbnail = @id.to_s + " thumb"}
    
    optional {@oldEthnicity = choose([
        "American Indian Or Alaskan Native",
        "Asian Or Pacific Islander",
        "Black, Not Of Hispanic Origin",
        "Hispanic",
        "White, Not Of Hispanic Origin"])
    }
      
    optional {@studentCharacteristics = {
      :characteristic => choose([
        "Displaced Homemaker",
        "Foster Care",
        "Homeless",
        "Immigrant",
        "Migratory",
        "Pregnant",
        "Single Parent",
        "Unaccompanied Youth",
        "Unschooled Asylee",
        "Unschooled Refugee",
        ]),
      :beginDate => Date.new(20012+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28)),
      :endDate => Date.new(2013+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28)),
      :designatedBy => choose(["Teacher", "Mentor", "Parent"])
    }}

    optional {@languages = choose([
      "Spanish",
      "Vietnamese",
      "Laotian (Lao)",
      "Cambodian (Khmer)",
      "Korean",
      "Japanese",
      "French",
      "German",
      "English",
      "Nepali",
      "Norwegian"])
    }
    
    optional {@homeLanguages = choose([
      "Spanish",
      "Vietnamese",
      "Laotian (Lao)",
      "Cambodian (Khmer)",
      "Korean",
      "Japanese",
      "French",
      "German",
      "English",
      "Nepali",
      "Norwegian"])
    }

    optional {@disabilities =  {
      :disability => choose([
        "Autistic/Autism",
        "Deaf-Blindness",
        "Deafness",
        "Developmental Delay",
        "Emotional Disturbance",
        "Hearing/Auditory Impairment",
        "Infants and Toddlers with Disabilities",
        "Mental Retardation",
        "Multiple Disabilities",
        "Orthopedic Impairment",
        "Other Health Impairment"
      ]),
      :disabilityDiagnosis => choose(["Diagnosis A", "Diagnosis BBB"]),
      :orderOfDisability => @rand.rand(20) + 1
    }}

    optional {@section504Disabilities = choose([
      "Attention Deficit Hyperactivity Disorder (ADHD)",
      "Medical Condition",
      "Motor Impairment",
      "Sensory Impairment",
      "Other"])
    }

    optional {@displacementStatus = choose(["Status A", "Status BBB"])}

    optional {@programParticipations = {
      :program => choose([
        "Adult/Continuing Education",
        "Alternative Education",
        "Athletics",
        "Bilingual",
        "Bilingual Summer",
        "Career and Technical Education",
        "Cocurricular Programs"]),
      :beginDate => Date.new(20012+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28)),
      :endDate => Date.new(2013+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28)),
      :designatedBy => choose(["Teacher", "Mentor", "Parent"])
    }

    optional {@learningStyles = {
      :visualLearning => @rand.rand(40)+1,
      :auditoryLearning => @rand.rand(40)+1,
      :tactileLearning => @rand.rand(40)+1
    }
    }}

    optional {@cohortYears = {
      :schoolYear => choose([
        "2009-2010",
        "2010-2011",
        "2011-2012",
        "2012-2013",
        "2013-2014",
        "2014-2015",
        "2015-2016"]),
      :cohortYearType => choose([
        "Eighth grade",
        "Eleventh grade",
        "Fifth grade",
        "First grade",
        "Fourth grade",
        "Ninth grade",
        "Second grade",
        "Seventh grade",
        "Sixth grade",
        "Tenth grade",
        "Third grade",
        "Twelfth grade"])
    }}

    optional {@studentIndicators = {
      :indicatorGroup => choose(["Group A", "Group B", "Group C"]),
      :indicatorName => choose(["Name 1", "Name 2", "Name 3"]),
      :indicator => choose(["Indicator 1", "Indicator 2", "Indicator 3"]),
      :beginDate => Date.new(20012+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28)),
      :endDate => Date.new(2013+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28)),
      :designatedBy => choose(["Teacher", "Mentor", "Parent"])
    }}

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
