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

require 'json'
require 'logger'
require_relative '../Shared/scenario'
require_relative 'entity_tracker'

Dir["#{File.dirname(__FILE__)}/../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# base class for data writers to inherit
# -> keeps all entities written in memory
# -> useful for small simulations
class DataWriter
  attr_accessor :tracker
  
  # default constructor for data writer class
  def initialize(scenario)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @entities = Hash.new     # leave default value of nil for hash key not existing
    @tracker = EntityTracker.new
    @scenario = Scenario.new(scenario)
  end

  def << (to_write)
    ((to_write.is_a? Array) ? to_write : [to_write]).each{|entity|
      if @scenario.include_entity?(entity)
        initialize_entity(entity)
        write_one_entity(entity) 
        @tracker.track entity
      end
    }
  end

  def write_one_entity (entity)
    @entities[entity.class.name] << entity
  end

  # if the specified entity (which should be a symbol used to identify the ed-fi entity) does not
  # currently have an entry in the @entities hash, then initialize an entry in both the @entities
  # and @counts hash
  def initialize_entity(entity)
    if @entities[entity.class.name].nil?
      @entities[entity.class.name] = []
    end
  end
  
  # displays the counts for entities created
  def display_entity_counts
    @log.info @tracker.display
  end

  # get the entities created
  def get_entities(type)
    @entities[type.name]
  end

  # gets the number of entities created of specified entity 'type'
  def get_entity_count(type)
    @tracker.count(type)
  end

  # get counts for all entities --> return counts hash
  def get_counts_for_all_entities
    @tracker.counts
  end

  # clear entity and count hash
  def finalize
    @entities.clear
  end

end
