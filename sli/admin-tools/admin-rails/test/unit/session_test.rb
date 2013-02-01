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

class EulaTest < ActiveSupport::TestCase

  test "session valid method" do
    fail_params = { :some_key => "some value" }
    success_params = { :guuid => "anything" }

    assert_equal false, Session.valid?(fail_params)
    assert_equal true, Session.valid?(success_params)
  end
end
