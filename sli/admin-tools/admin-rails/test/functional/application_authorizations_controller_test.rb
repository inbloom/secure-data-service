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

class ApplicationAuthorizationsControllerTest < ActionController::TestCase
  setup do
    @AppAuths = ApplicationAuthorization.all
  end

  test "should get index" do
    session[:roles] = ["LEA Administrator"]
    get :index
    assert_response :success
    assert_not_nil assigns(:application_authorizations)
  end

  test "should create application_authorization" do
    session[:roles] = ["LEA Administrator"]
    post :create, application_authorization: @appauth_fixtures['new_district']
    #assert_redirected_to application_authorization_path(assigns(:application_authorization))
    assert_redirected_to assigns(:application_authorization)
  end

  test "should fail if we are an operator" do
    session[:roles] = ["SLC Operator"]
    post :index
    assert(!session.has_key?("roles"), "Session should be reset after we are given a forbidden")
  end

  test "should update application_authorization" do
    session[:roles] = ["LEA Administrator"]
    put :update, id: "1", application_authorization: @appauth_fixtures['district1']
    assert_redirected_to application_authorizations_path
  end
end
