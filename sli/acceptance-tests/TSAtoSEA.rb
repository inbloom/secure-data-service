require 'json'
require 'digest/sha1'

#Hold the command line options
options = {}

if ARGV.size < 3
    puts "Usage: #{__FILE__} inputTSA outputSEA tenant"
end
@options = {}
@options[:input] = ARGV[0]
@options[:output] = ARGV[1]
@options[:tenant] = ARGV[2]
@options[:keys] = [:beginDate, :staffReference, :educationOrganizationReference, :staffClassification]

def cerate_natural_key(entity, keys = nil)
    real_keys = keys || @options[:keys]
    entity.delete(:_id)      # in case it exists but the value is nil
    hash = ""
    delimiter = "|~"
    real_keys.sort!
    #First we do entity type and tenant
    hash << entity[:metaData][:type] << delimiter << @options[:tenant] << delimiter
    real_keys.each do |nk|
        hash << entity[:body][nk] << delimiter
    end
    puts "Hash bfeore sha #{hash}"
    entity['_id'] = Digest::SHA1.hexdigest hash
    entity['_id'] += "_id"
    entity
end

def build_sea(tsa)
    body = {:beginDate => '2007-07-07', :staffReference => tsa['body']['teacherId'], 
        :educationOrganizationReference => tsa['body']['schoolId'], :staffClassification => 'Teacher'
    }
    metaData = {:type => "staffEducationOrganizationAssociation"}
    sea = {"_id" => nil, :body => body, :metaData => metaData}
    sea
end
puts "Opening file streams to #{@options[:input]} and #{@options[:output]}"
input_file = File.new(@options[:input], 'r')
output_file = File.new(@options[:output], 'a')
count = 0
while(line = input_file.gets)
    tsa = JSON.parse(line)
    sea = build_sea(tsa)
    sea = cerate_natural_key(sea)
    puts sea
    count += 1
    output_file.puts sea.to_json
end
puts "Converted #{count} teacherSchoolAssociations to staffEdorgAssignmentAssociations"
input_file.close
output_file.close