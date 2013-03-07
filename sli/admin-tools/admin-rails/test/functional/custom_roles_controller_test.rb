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


require 'test_helper'

class CustomRolesControllerTest < ActionController::TestCase
  setup do
     session[:rights] = ["CRUD_ROLE"]
  end

  test "index should redirect to realms if multiple exist" do
    ActiveResource::HttpMock.respond_to do |mock|
      mock.get "/api/rest/customRoles", {"Accept" => "application/json"}, [@custom_roles_fixtures["one"], @custom_roles_fixtures["two"]].to_json
    end
    get :index
    assert_response 302
    assert_redirected_to realm_management_index_path
  end

  test "index redirect to show if one exists" do
    ActiveResource::HttpMock.respond_to do |mock|
      mock.get "/api/rest/customRoles", {"Accept" => "application/json"}, [@custom_roles_fixtures["one"]].to_json
    end
    get :index
    assert_response 302
    assert_redirected_to custom_role_path(@custom_roles_fixtures["one"]["id"])
  end

  # test "should show App" do
  #   # session[:roles] = ["Application Developer"]
  #   get :show, id: @Apps[0].id
  #   assert_redirected_to apps_path
  # end

end
