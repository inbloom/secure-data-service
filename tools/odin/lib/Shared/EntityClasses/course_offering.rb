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

require_relative "../data_utility.rb"
require_relative "baseEntity.rb"

# creates course offering
class CourseOffering < BaseEntity

  attr_accessor :code, :title, :ed_org_id, :session, :course

  def initialize(id, title, ed_org_id, session, course)
    @code                 = DataUtility.get_course_offering_code(id)
    @title                = title
    @ed_org_id            = ed_org_id
    @session              = {'name'=>session["name"], 'ed_org_id'=>session["ed_org_id"]}
    @course               = {'id'=>DataUtility.get_course_unique_id(course["id"]), 'ed_org_id'=>course["ed_org_id"]}
  end
   
end
