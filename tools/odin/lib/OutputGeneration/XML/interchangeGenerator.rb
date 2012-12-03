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
Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }
class InterchangeGenerator

  attr_accessor :interchange, :header, :footer

  def initialize(yaml, interchange)
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
  end

  def start()
    @interchange << @header
  end

  def <<(entity)
    @entities << entity
    if @entities.size >= @batch_size
      renderBatch
      @entities = []
    end
  end

  def renderBatch
    split_entities = @entities.group_by( &:class )

    split_entities.each do |k, v|
      @interchange << (@writers[k].write(v))
    end
  end

  def finalize()
    renderBatch

    @interchange << @footer
    @interchange.flush()
    @interchange.close()

    elapsed = Time.now - @stime
    puts "\t#@entityCount written in #{elapsed} seconds."
  end

end
