#!/usr/env/bin ruby

require "json"
require 'digest/sha1'

DELIMITER = '|~'
SUFFIX = "_id"

@SUBDOCS = ["assessmentItem","objectiveAssessment","assessmentPeriodDescriptor"]
@NATURAL_KEYS_MAP = { 
    "assessment"=>["academicSubject","version","assessmentTitle","gradeLevelAssessed"],
    "assessmentItem" => ["identificationCode","assessmentId"], 
    "objectiveAssessment" => ["identificationCode","assessmentId","subObjectiveAssessment","objectiveAssessments"],
    "assessmentPeriodDescriptor" => ["codeValue"] 
}

def generate_did(type,tenantId,naturalKeys,doc)
  key_string = type + DELIMITER + tenantId + DELIMITER
  naturalKeys.sort.each do |key|
    unless doc[key].nil? || (doc[key].kind_of?(Array) && doc[key].empty?)
        key_string = key_string + doc[key].to_s + DELIMITER
    end
  end 
  hash = Digest::SHA1.hexdigest key_string
  hash + SUFFIX
end

def unsuperdoc(superDocFile, newSuperDocFile, subdoc_type, parentId_field, tenantId="Midgar")
  superDocFile.each_line do |line|
    begin
      superdoc = JSON.parse(line)
      type = superdoc["type"]
      # nil subdoc section in the superdoc
      subdoc = superdoc["body"][subdoc_type]
      superdoc["_id"] = generate_did(type, tenantId,@NATURAL_KEYS_MAP[type], superdoc["body"])
      unless subdoc
        newSuperDocFile.puts superdoc
        next
      end
    rescue JSON::ParserError
      puts "Problem with superdoc file :("
      puts "line is #{line}"
      next
    end

    superdoc["body"].delete(subdoc_type)

    # convert each subdoc into an entity 
    subentities = subdoc.map {|doc|
      {"type"=>subdoc_type,
       "_id"=>superdoc["_id"]+generate_did(subdoc_type, tenantId, @NATURAL_KEYS_MAP[subdoc_type], doc),
       "body"=>doc.merge(parentId_field=>superdoc["_id"])
      }
    }
    superdoc[subdoc_type] = subentities
    newSuperDocFile.puts superdoc
  end
end

if (ARGV.size < 3) 
  puts "Usage: ruby unsuperdoc.rb superDocJSONFile subdoc_type parentId [tenantId]"
  exit
end

superdoc = ARGV[0]
subdoc = ARGV[1]
parentId = ARGV[2]
tenantId = ARGV[3]

puts "Unsuperdocing #{subdoc} from #{superdoc}..."
superDocFile = File.new(superdoc)
newSuperDocFile = File.new("#{superdoc}.converted.#{Time.new.strftime("%Y%m%d_%H%M%S")}", "w")

unsuperdoc(superDocFile, newSuperDocFile, subdoc, parentId, (tenantId || "Midgar"))

[superDocFile, newSuperDocFile].each { |f| f.close }
puts "new superdoc file is: #{newSuperDocFile.path}"
