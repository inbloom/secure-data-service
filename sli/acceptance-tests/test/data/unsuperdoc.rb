#!/usr/env/bin ruby

require "json"

def to_json_with_no_nil(hash) 
  hash.reject! { |k, v| v.nil? }
  hash.to_json
end

def stripParentId(id, parentId = nil) 
  if parentId.nil?
    id[id.size/2..-1]
  else
    id.gsub(parentId, "")
  end
end

def unsuperdoc(superDocFile, newSuperDocFile, newSubdocFile, subdoc_type, parentId_field=nil)
  while (line = superDocFile.gets)
    begin
      superdoc = JSON.parse(line)
      next unless superdoc[subdoc_type]
    rescue JSON::ParserError
      puts "Problem with superdoc file :("
      puts "line is #{line}"
      next
    end

    subdoc = superdoc[subdoc_type]
    
    # nil subdoc section in the superdoc
    superdoc[subdoc_type] = nil
    newSuperDocFile.puts to_json_with_no_nil(superdoc)

    # convert each subdoc into correct format
    subdoc.each {|doc|
      parentId = doc["body"][parentId_field] unless parentId_field.nil?
      if (doc["_id"].size / superdoc["_id"].size == 2)
        # kitty catted string
        doc["_id"] = stripParentId(doc["_id"], parentId) 
      end
      newSubdocFile.puts doc.to_json
    }
  end
end

if (ARGV.size < 3) 
  puts "Usage: ruby unsuperdoc.rb superDocJSONFile newSuperJSONFile subdoc_type [parentId]"
  exit
end

superdoc = ARGV[0]
newsuperdoc = ARGV[1]
subdoc = ARGV[2]
parentId = ARGV[3]

puts "Unsuperdocing #{subdoc} from #{superdoc}..."
superDocFile = File.new(superdoc)
newSuperDocFile = File.new(newsuperdoc, "w")
newSubDocFile = File.new("#{subdoc}_fixture.json.#{Time.new.strftime("%Y%m%d_%H%M%S")}", "w")

unsuperdoc(superDocFile, newSuperDocFile, newSubDocFile, subdoc, parentId)

[superDocFile, newSuperDocFile, newSubDocFile].each { |f| f.close }
puts "new superdoc file is: #{newsuperdoc}"
puts "new subdoc file is: #{newSubDocFile.path}"
