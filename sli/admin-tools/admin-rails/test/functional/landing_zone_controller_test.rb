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

class LandingZoneControllerTest < ActionController::TestCase
  # test "should get provision" do
  #   post :provision
  #   assert_response :redirect, @response.body
  # end

  test "should get index" do
    get :index, {}, { :roles => ["Ingestion User"] }
    assert_response :success, @response.body
  end

  test "should fail due to lack of permissions" do
    get :index, {}, { :roles => ["Developer"] }
    assert_response 403
  end
end
