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

require 'logger'
require_relative '../../../lib/Shared/deferred_garbage_collector'

Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# base class for ed-fi xml interchange generators
class InterchangeGenerator

  attr_accessor :interchange, :header, :footer

  # Initializes the generator with a given +yaml+ source
  # and a File-like +interchange+ that holds the output
  #
  def initialize(yaml, interchange)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @interchange = interchange
    @batch_size = yaml['BATCH_SIZE'] || 10000
    @gc = DeferredGarbageCollector.new(1.0)
    set_delete_options(yaml)

    @stime = Time.now
    @entities = []
    @writers = Hash.new
    @header = ""
    @footer = ""
    @has_entities
    @log.info "initialized interchange generator using file handle: #{@interchange.path}"
  end

  def start
    @interchange << @header
    @interchange << @delete_header
  end

  def <<(entity)
    @has_entities = true
    @entities << entity
    if @entities.size >= @batch_size
      render_batch
      @entities = []
    end
  end

  def render_batch
    split_entities = @entities.group_by( &:class )
    split_entities.each do |k, v|
      @interchange << @writers[k].write(v)
    end
    @gc.collect
  end

  def finalize
    render_batch
    @interchange << @delete_footer
    @interchange << @footer
    @interchange.close
    elapsed = Time.now - @stime
    if @has_entities
      @log.info "interchange: #{@interchange.path} in #{elapsed} seconds."
    else
      @log.info "no entities for #{@interchange.path}"
      File.delete @interchange
    end
  end

  def can_write?(entity)
    @writers[entity].nil? == false
  end

  def set_delete_options(yaml)
    deleteType = yaml['DELETE']
    @delete_header = case deleteType
                     when "safe" then "<Action ActionType=\"DELETE\">\n"
                     when "force" then "<Action ActionType=\"DELETE\" Force=\"true\">\n"
                     when "cascade" then "<Action ActionType\"DELETE\" Cascade=\"true\">\n"
                     else ""
                     end
    @delete_footer = deleteType.nil? ? "" : "</Action>\n"
  end

end
