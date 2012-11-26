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

require_relative 'baseEntity'

# creates session
class Session < BaseEntity

  attr_accessor :name, :school_year, :term, :edOrgId;

  def initialize(name, year, term, interval, edOrgId)
  	@name = name
  	@school_year = year.to_s + " " + (year+1).to_s
  	@term = term
  	@interval = interval
  	@edOrgId = edOrgId
  end

  def begin_date
  	# use interval.get_begin_date
  end

  def end_date
  	# use interval.get_end_date
  end

  def num_school_days
  	#use interval.get_num_school_days
  end

end