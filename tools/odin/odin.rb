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
require 'yaml'
require_relative 'util.rb'


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







