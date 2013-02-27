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

require_relative "../date_interval.rb"
require_relative "baseEntity"
require_relative "enum/SchoolTerm.rb"
require_relative "enum/GradingPeriodType.rb"

# creates session
class Session < BaseEntity

  attr_accessor :name, :school_year, :ed_org_id, :begin_date, :end_date, :num_school_days, :holidays,
                :calendarDateReference

  def initialize(name, year, term, interval, ed_org_id, grading_periods)
    @rand = Random.new(name.hash + year.hash)
    @name            = name
  	@school_year     = year.to_s + "-" + (year+1).to_s
  	@term            = term
    @begin_date      = interval.get_begin_date
    @end_date        = interval.get_end_date
    @num_school_days = interval.get_num_school_days
    @holidays        = interval.get_holidays
  	@ed_org_id       = ed_org_id
    @grading_periods = grading_periods

    optional {@calendarDateReference = {
        :date => Date.new(2012+@rand.rand(3), 1+@rand.rand(12), 1+@rand.rand(28)),
        :ed_org_id => ed_org_id
      }
    }

  end

  def term
    SchoolTerm.to_string(@term)
  end

  def grading_periods
    periods = []
    @grading_periods.each do |grading_period|
      interval             = grading_period["interval"]
      period               = Hash.new
      period["type"]       = GradingPeriodType.to_string(grading_period["type"])
      period["begin_date"] = interval.get_begin_date.to_s
      period["ed_org_id"]  = grading_period["ed_org_id"]
      periods              << period
    end
    periods
  end
end
