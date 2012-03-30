# lazy initialization of parsed_results so we don't reparse for every "Then.."
def parsed_results
  if @format == "application/vnd.slc+xml" or @format == "application/xml"
    @parsed_results = REXML::Document.new(@res.body)
    @prefix = "Entities/section/"
  elsif @format == "application/vnd.slc+json" or @format == "application/json"
    @parsed_results ||= JSON.parse(@res.body)
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
end

# Function data_builder
# Inputs: None
# Output: Data object in json or XML format depending on what the @format variable is set to
# Returns: Nothing, see Output
# Description: Helper function to create json or XML data structures to PUT or POST 
#                   to reduce replication of code
def data_builder
  if @format == "application/vnd.slc+json" or @format == "application/json"
  data = Hash[
    "uniqueSectionCode" => @section_code,
    "sequenceOfCourse" => @course_seq,
    "educationalEnvironment" => @edu_env,
    "mediumOfInstruction" => @medium,
    "populationServed" => @pop,
    "availableCredit" => @credit
  ]
  data = data.to_json
  elsif @format == "application/vnd.slc+xml" or @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent => 2)
    data = builder.section { |b| 
      b.uniqueSectionCode(@section_code)
      b.sequenceOfCourse(@course_seq)
      b.educationalEnvironment(@edu_env)
      b.populationServed(@pop)
      b.availableCredit(@credit)  
      }
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
  data
end

def getKeyValue(key)
  value = -1
  if @format == "application/vnd.slc+json" or @format == "application/json"
    value = @parsed_results[key]
  elsif @format == "application/vnd.slc+xml" or @format == "application/xml"
    @parsed_results.elements.each (@prefix + key) { |element| value = element.text }
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
  return value
end

