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


require 'csv'
require 'json'
require 'rexml/document'
include REXML

class StudentEntity
  #id,state_id,id_1_id_system,id_1_assigning_ord_code,id_1_id_code,name_verification,name_prefix,name_first,name_middle,
  #name_last,name_suffix,name_maiden,other_name_1_type,other_name_1_prefix,other_name_1_first,other_name_1_middle,
  #other_name_1_last,other_name_1_suffix,sex,birth_date,birth_city,birth_state_abbrev,birth_country_code,birth_date_entered_us,
  #birth_multiple_status,address_1_type,address_1_street_number_name,address_1_apt_number,address_1_bldg_number,address_1_city,
  #address_1_state_abbrev,address_1_postal_code,address_1_county_name,address_1_county_fips_code,address_1_country_code,
  #address_1_latitude,address_1_longitude,telephone_1_type,telephone_1_primary_indicator,telephone_1_number,email_1_type,
  #email_1_address,profile_thumbnail,hispanic_latino_ethnicity,old_ethnicity,racial_category_1,economic_disadvantaged,
  #school_food_services_eligibility,characteristic_1_characteristic,characteristic_1_begin_date,characteristic_1_end_date,
  #characteristic_1_designated_by,limited_english_proficiency,language_1,home_language_1,disability_1_disability,disability_1_diagnosis,
  #disability_1_order,section_504_disability_1,displacement_status_type,programs_1_program,programs_1_begin_date,programs_1_end_date,
  #programs_1_designated_by,learning_styles_visual,learning_styles_auditory,learning_styles_tactile,cohort_year_type,
  #cohort_year,indicator_1_name,indicator_1_indicator,indicator_1_begin_date,indicator_1_end_date,indicator_1_designated_by

  attr_accessor :myHash
  
  def initialize
    @myHash = Hash.new
  end
  
  def createJson(inputRow)
    data = Hash[  
      "_id" => Hash["$binary" => inputRow[@myHash['id']]],
      "type" => "student",
      "body" => Hash[
          "studentUniqueStateId" => inputRow[@myHash['state_id']].to_i,
          "name" => Hash[
            "firstName" => inputRow[@myHash['name_first']],
            "middleName" => inputRow[@myHash['name_middle']],
            "lastSurname" => inputRow[@myHash['name_first']]
          ]
        ]
      ]
      return data.to_json
  end
  
  def createHeaderMap(headerRow)
    headerRow.each_with_index do |x, y|
      @myHash[x] = y
    end
  end
  
end

class StudentEntity_XML
  
  def initialize(inputXML, outputJSON)
    @myXML_File = inputXML
    @myJSON_File = outputJSON
  end
  
  def getElement_or_nil(element, name)
    if(element.elements[name] == nil) 
      return nil.to_s
    else 
      return element.elements[name].text
    end
  end
  
  def createJson()
    doc = Document.new(@myXML_File)
    doc.root.elements.each do |student| 
      next if student.name != "Student"
      
      data = Hash[  
      "_id" => Hash["$binary" => student.elements["_id"].text],
      "type" => "student",
      "body" => Hash[
          "studentUniqueStateId" => student.elements["StudentUniqueStateId"].text.to_i,
          "name" => Hash[
            "firstName" => student.elements["Name"].elements["FirstName"].text,
            "middleName" => getElement_or_nil(student.elements["Name"], "MiddleName"),
            "lastSurname" => student.elements["Name"].elements["LastSurname"].text 
          ],
          "sex" => student.elements["Sex"].text,
          "birthData" => Hash[
            "birthDate" => student.elements["BirthData"].elements["BirthDate"].text
          ]
        ]
      ]
      @myJSON_File.puts(data.to_json)
    end
  end
end

class SchoolEntity_XML
  
  def initialize(inputXML, outputJSON)
    @myXML_File = inputXML
    @myJSON_File = outputJSON
  end
  
  def getElement_or_nil(element, name)
    if(element.elements[name] == nil) 
      return nil.to_s
    else 
      return element.elements[name].text
    end
  end
  
  def makeAddressArray(element)
    ar = Array.new
    element.elements.each("Address") do |add|
      ar.push(Hash[
        "type" => add.attributes["AddressType"],
        "streetNumberName" => add.elements["StreetNumberName"].text,
        "city" => add.elements["City"].text,
        "state" => add.elements["StateAbbreviation"].text,
        "postalCode" => add.elements["PostalCode"].text,
        "country" => add.elements["NameOfCounty"].text
      ]
      )
    end
    return ar
  end
  
  def makeTelephoneArray(element)
    ar = Array.new
    element.elements.each("Telephone") do |t|
      ar.push(Hash[
        "type" => t.attributes["InstitutionTelephoneNumberType"],
        "number" => t.elements["TelephoneNumber"].text
      ]
      )
    end
    return ar
  end
  
  def makeGradesArray(element)
    ar = Array.new
    element.elements.each do |e|
      ar.push(e.text)
    end
    return ar
  end
  
  def makeCategoryArray(element)
    ar = Array.new
    element.elements.each do |e|
      ar.push(e.text)
    end
    return ar
  end
  
  def createJson()
    doc = Document.new(@myXML_File)
    doc.root.elements.each do |school| 
      next if school.name != "School"
      
      data = Hash[  
      "_id" => Hash["$binary" => school.elements["_id"].text],
      "type" => "school",
      "body" => Hash[
          "stateOrganizationId" => school.elements["StateOrganizationId"].text.to_i,
          "nameOfInstitution" => school.elements["NameOfInstitution"].text,
          "organizationCategory" => school.elements["OrganizationCategory"].text,
          "addresses" => makeAddressArray(school),
          "telephone" => makeTelephoneArray(school),
          "gradesOffered" => makeGradesArray(school.elements["GradesOffered"]),
          "schoolCategories" => makeCategoryArray(school.elements["SchoolCategories"])
          
        ]
      ]
      @myJSON_File.puts(data.to_json)
    end
  end
end

class EnrollmentEntity_XML
  
  def initialize(inputXML, outputJSON)
    @myXML_File = inputXML
    @myJSON_File = outputJSON
  end
  
  def getElement_or_nil(element, name)
    if(element.elements[name] == nil) 
      return nil.to_s
    else 
      return element.elements[name].text
    end
  end
  
  def makeEdPlanArray(element)
    ar = Array.new
    element.elements.each do |e|
      ar.push(e.text)
    end
    return ar
  end
  
  def createJson()
    doc = Document.new(@myXML_File)
    doc.root.elements.each do |enrollment| 
      next if enrollment.name != "StudentSchoolAssociation"
      
      data = Hash[  
      "_id" => Hash["$binary" => enrollment.elements["_id"].text],
      "type" => "enrollment",
      "body" => Hash[
          "studentUniqueStateId" => enrollment.elements["StudentReference"].elements["StudentIdentity"].elements["StudentUniqueStateId"].text,
          "stateOrganizationId" => enrollment.elements["SchoolReference"].elements["EducationalOrgIdentity"].elements["StateOrganizationId"].text,
          "entryDate" => enrollment.elements["EntryDate"].text,
          "entryGradeLevel" => enrollment.elements["EntryGradeLevel"].text,
          "graduationPlan"  => enrollment.elements["GraduationPlan"].text,
          "educationalPlans" => makeEdPlanArray(enrollment.elements["EducationalPlans"])
          
        ]
      ]
      @myJSON_File.puts(data.to_json)
    end
  end
end


#s = StudentEntity.new
#headerRow = true

#CSV.foreach("../../cucumber-tests/src/test/resources/data/student-sample-data.csv") do |row|
#  if(headerRow)
#    s.createHeaderMap(row)
#    headerRow = false
#    next
#  end
#  
#  #puts s.createJson(row)
#  
#  #puts row[s.myHash["id"]]
#  
#end

#puts ""

puts "Start"
s2 = StudentEntity_XML.new(File.new("/Users/jshort/temp/student_sample.xml"), File.new("/Users/jshort/temp/student_out.json", "w+"))
s2.createJson()

s3 = SchoolEntity_XML.new(File.new("/Users/jshort/temp/school_sample.xml"), File.new("/Users/jshort/temp/school_out.json", "w+"))
s3.createJson()

s4 = EnrollmentEntity_XML.new(File.new("/Users/jshort/temp/student_school_assoc_sample.xml"), File.new("/Users/jshort/temp/enrollment_out.json", "w+"))
s4.createJson()




