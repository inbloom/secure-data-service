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

Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# base class for ed-fi xml interchange generators
class InterchangeGenerator

  attr_accessor :interchange, :header, :footer

  def initialize(yaml, interchange)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @interchange = interchange
    @batch_size = yaml['BATCH_SIZE']
    if @batch_size.nil?
      @batch_size = 10000
    end

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
      @interchange << (@writers[k].write(v))
    end
  end

  def finalize
    render_batch
    @interchange << @footer
    @interchange.close()
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

end
