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

require 'mustache'

class BaseEntity < Mustache
  self.template_path = File.dirname(__FILE__) + "/../baseEntityTemplates"

  def choose(options)
    options[@rand.rand(options.size) - 1]
  end
  
  def wChoose(distribution)
    wArray = []
    distribution.each do |element, weight|
      weight.times {wArray << element}
    end 
    choose(wArray)
  end
  
end