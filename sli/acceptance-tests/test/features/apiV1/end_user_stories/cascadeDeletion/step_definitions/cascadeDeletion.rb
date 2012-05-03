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
  value = "dd916592-7d7e-5d27-a87d-dfc7fcb757f6" if human_readable_text == "ASSESSMENT ID"
  value = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if human_readable_text == "SCHOOL ID"
  value = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f" if human_readable_text == "SECTION ID"
  value = "627f6922-983c-4616-874a-def76a87ba70" if human_readable_text == "SECTION ASSESSMENT ASSOCIATION ID"
  
  #return this
  value
  
end
