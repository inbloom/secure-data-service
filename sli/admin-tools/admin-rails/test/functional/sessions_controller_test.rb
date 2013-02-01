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
require 'net/http'



class SessionsControllerTest < ActionController::TestCase
  test "destroy session" do
    @http_mock = mock('Net:HTTP')
    Net::HTTP.expects(:new).returns(@http_mock)
    @http_mock.expects(:use_ssl=).with(true)
    @http_mock.expects(:verify_mode=).with(OpenSSL::SSL::VERIFY_NONE)
    @http_mock.expects(:request)
    get :destroy
    assert_response :success
  end

end
