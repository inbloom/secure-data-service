require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'


Transform /^<([^>]*)>$/ do |human_readable_text|

  #expose URIs
  value = "assessments"                          if human_readable_text == "ASSESSMENT URI"
  value = "schools"                              if human_readable_text == "SCHOOL URI"
  value = "sections"                             if human_readable_text == "SECTION URI"
  value = "sectionAssessmentAssociations"        if human_readable_text == "SECTION ASSESSMENT ASSOCIATION URI"
  
  #IDs
  value = "b5f684d4-9a12-40c3-a59e-0c0d1b971a1e" if human_readable_text == "ASSESSMENT ID"
  value = "eb3b8c35-f582-df23-e406-6947249a19f2" if human_readable_text == "SCHOOL ID"
  value = "1e1cdb04-2094-46b7-8140-e3e481013480" if human_readable_text == "SECTION ID"
  value = "1580e803-04ea-4720-8439-5f0f4834decd" if human_readable_text == "SECTION ASSESSMENT ASSOCIATION ID"
  
  #return this
  value
  
end
