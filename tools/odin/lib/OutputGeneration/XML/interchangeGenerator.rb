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

  def initialize(interchange)
    @stime = Time.now
    @entityCount = 0
    @interchange = interchange
  end

  def start()
    @interchange << @header
  end

  def <<(entities)
    @entityCount = @entityCount + entities.length
    if @entityCount % 100000 == 0
      puts "\t#@entityCount entities created."
    end

  end

  def finalize()
    @interchange << @footer
    @interchange.close()

    elapsed = Time.now - @stime
    puts "\t#@entityCount written in #{elapsed} seconds."
  end

end
