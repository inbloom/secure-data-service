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

require_relative 'baseEntity'
class AssessmentItem < BaseEntity

  attr_accessor :id, :identificationCode, :itemCategory, :maxRawScore, :correctResponse, :nomenclature, :assessment
  def initialize(id, year_of)
    @id = id
    @year_of = year_of
    @rand = Random.new(@id)
    build
  end

  def build

    @identificationCode = @rand.rand(10000).to_s
    @itemCategory = @rand.rand(10000).to_s
    @maxRawScore = @rand.rand( 100 ).to_i
    @correctResponse = @rand.rand(10000).to_s
  end

end