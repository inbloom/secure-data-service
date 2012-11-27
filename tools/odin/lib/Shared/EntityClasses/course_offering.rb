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

require_relative "../data_utility.rb"
require_relative "baseEntity.rb"

# creates course offering
class CourseOffering < BaseEntity

  attr_accessor :title, :ed_org_id

  def initialize(id, title, ed_org_id, session, course)
    @id                   = id
    @title                = title
    @ed_org_id            = ed_org_id
    @session              = Hash.new
    @session["name"]      = session["name"]
    @session["ed_org_id"] = session["ed_org_id"]
    @course               = Hash.new
    @course["id"]         = DataUtility.get_course_unique_id(course["id"])
    @course["ed_org_id"]  = course["ed_org_id"]
  end

  def code
    DataUtility.get_course_offering_code(@id)
  end
   
  def session
    @session
  end
   
  def course
    @course
  end
end
