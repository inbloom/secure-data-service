=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require "rexml/document"
require 'digest/md5'

################### Should be moved into utilities file #########################################
def stripInterchange(interchangeName)
  interchangeName[11..-5]
end

def genCtlFile(dir='generated')
  File.open("generated/ControlFile.ctl", 'w') do |f|
    Dir["#{File.dirname(__FILE__)}/#{dir}/*.xml"].each do |int|
      # I don't like how I get the ingestion file type below
      f.write "edfi-xml,#{stripInterchange(int[int.rindex('/')+1..-1])},#{int[int.rindex('/')+1..-1]},#{Digest::MD5.file(int).hexdigest}\n"
    end
  end
end

###########################################################################################################

require 'yaml'
Dir["#{File.dirname(__FILE__)}/interchangeGenerators/*.rb"].each { |f| load(f) }

configYAML = YAML.load_file(File.join(File.dirname(__FILE__),'config.yml'))

scenarioYAML = YAML.load_file(File.join(File.dirname(__FILE__), 'scenarios', configYAML['scenario'] ))
if ARGV.count == 1
  scenarioYAML['studentCount'] = ARGV[0].to_i
end

prng = Random.new(configYAML['seed'])
Dir.mkdir('generated') if !Dir.exists?('generated')

time = Time.now
pids = []
    
pids << fork {  StudentParentGenerator.new.write(prng, scenarioYAML)           }
pids << fork {  EducationOrganizationGenerator.new.write(prng, scenarioYAML)   }
pids << fork {  StudentEnrollmentGenerator.new.write(prng, scenarioYAML)       }
Process.waitall

finalTime = Time.now - time
puts "\t Final time is #{finalTime} secs"





genCtlFile







