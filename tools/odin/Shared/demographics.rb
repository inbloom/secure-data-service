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

require 'yaml'

class Demographics

  def initialize(demographics = './defaultDemographics.yml', choices = 'choices.yml')
    @choices = YAML.load_file File.join("#{File.dirname(__FILE__)}", "choices.yml")
  end

  def maleNames
    @choices['maleNames']
  end

  def femaleNames
    @choices['femaleNames']
  end
  
  def lastNames
    @choices['lastNames']
  end
end
