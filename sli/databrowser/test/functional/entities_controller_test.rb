=begin
#--

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


require 'test_helper'
require 'entities_helper'

class EntitiesControllerTest < ActionController::TestCase

  test "should show entity for students" do
    get :show, other: "students/#{@student_fixtures['one']['id']}".to_param
    assert_response :success
  end
  
  test "get full teacher objects through association" do
    get :show, other: "teacher-school-associations/11111111-1111-1111-1111-111111111111".to_param
    assert_response :success
  end
  
  test "search successfully" do
    get(:show, {:search_type => "teachers", :search_id => @teacher_fixtures['one']['id'], :other => "students"})
    assert_response :success
  end
  
  test "bad search goes nowhere" do
    get(:show, {:search_type => "sdfsdf", :search_id => "asdf", :other => "teachers"})
    assert_response :success
  end

end
