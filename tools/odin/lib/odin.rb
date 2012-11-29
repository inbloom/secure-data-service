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
# Enable tailcall optimizations to reduce overall stack size.
#RubyVM::InstructionSequence.compile_option = {
#    :tailcall_optimization => true,
#    :trace_instruction => false
#}

require 'digest/md5'
require 'digest/sha1'
require 'logger'
require "rexml/document"
require 'thwait'
require 'yaml'

require_relative 'EntityCreation/world_builder.rb'
require_relative 'EntityCreation/work_order_processor.rb'
require_relative 'OutputGeneration/XML/studentParentInterchangeGenerator'
require_relative 'OutputGeneration/XML/validator'
require_relative 'Shared/util'
require_relative 'Shared/demographics'
require_relative 'Shared/EntityClasses/student'

# offline data integration nexus --> ODIN
class Odin
  def initialize
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO
  end

  def generate( scenario )

    Dir["#{File.dirname(__FILE__)}/Shared/interchangeGenerators/*.rb"].each { |f| load(f) }

    scenarioYAML, prng = getScenario(scenario)

    Dir.mkdir('../generated') if !Dir.exists?('../generated')

    time = Time.now
    
    # Create a snapshot of the world
    edOrgs = WorldBuilder.new.build(prng, scenarioYAML)
    display_world_summary(edOrgs)

    # Batch size:  should be able ot optimize write time vs memory utilization.
    batchSize = 10000
    #
    # | Batch Size | Time / 1M Students | Peak Memory |  d(time)  |  d(mem)  |
    # |      1     |       320 sec      |    32 Mb    |     -     |     -    |
    # |    10000   |       288 sec      |   148 Mb    |  -32 sec  | +116 Mb  |
    # |    25000   |       281 sec      |   246 Mb    |  -39 sec  | +214 Mb  |
    # |   100000   |       277 sec      |   460 Mb    |  -43 sec  | +428 Mb  |
    #
    
    WorkOrderProcessor.run scenarioYAML, batchSize

    finalTime = Time.now - time
    @log.info "Total generation time: #{finalTime} secs"

    genCtlFile
    genDataZip
  end

  # displays brief summary of the world just created
  def display_world_summary(world) 
    @log.info "Summary of World:"
    @log.info " - state education agencies: #{world["seas"].size}"
    @log.info " - local education agencies: #{world["leas"].size}"
    @log.info " - elementary schools:       #{world["elementary"].size}"
    @log.info " - middle     schools:       #{world["middle"].size}"
    @log.info " - high       schools:       #{world["high"].size}"
  end

  def validate()
    valid = true
    Dir["#{File.dirname(__FILE__)}/../generated/*.xml"].each { |f| valid = valid && validate_file(f) }
    return valid
  end

  # Generates a MD5 hash of the generated xml files.
  def md5()
    hashes = []
    Dir["#{File.dirname(__FILE__)}/../generated/*.xml"].each { |f|
      hashes.push( Digest::MD5.hexdigest( f ))
    }
    
    return Digest::MD5.hexdigest( hashes.to_s )
  end
end

def getScenario(scenario_name)
  configYAML = YAML.load_file(File.join(File.dirname(__FILE__),'/../config.yml'))
  [load_scenario(scenario_name, configYAML), Random.new(configYAML['seed'])]
end
