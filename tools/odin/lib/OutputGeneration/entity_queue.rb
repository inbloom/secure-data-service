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
require 'singleton'

class EntityQueueRouter < Fiber

  def initialize(entityQueue, writer)
    @entityQueue = entityQueue
    @writer = writer
  end

  def resume
    while @entityQueue.empty? == false
      entity = @entityQueue.pop_entity()
      @writer << entity
    end
  end

end


class EntityQueue

  @entities
  @router

  def initialize
    @entities = []
    @router = nil
  end

  def writer(writer)
    @router = EntityQueueRouter.new(self, writer) do yield end
  end

  def push_entity(entity)
    @entities << entity
    if @router.nil? == false
      @router.resume
    end
  end

  def pop_entity()
    @entities.pop()
  end

  def empty?
    @entities.empty?
  end

end
