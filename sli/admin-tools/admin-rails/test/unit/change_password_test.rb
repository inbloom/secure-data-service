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

class ChangePasswordTest < ActiveSupport::TestCase
  def setup

  end
  def test_initialise
    change_password=ChangePassword.new(
        :old_pass=> 'test1234' ,
        :new_pass => 'test4321',
        :confirmation => 'test4321'
    )
    assert_equal('test1234',change_password.old_pass)
    assert_equal('test4321',change_password.new_pass)
    assert_equal('test4321',change_password.confirmation)
    assert(change_password.valid?)
  end

  def test_confirmation_password
    change_password=ChangePassword.new(
        :old_pass=> 'test1234' ,
        :new_pass => 'test4321',
        :confirmation => 'test431'
    )
    assert(!change_password.valid?)
  end

  def test_confirmation_password
    change_password=ChangePassword.new(
        :old_pass=> 'test1234' ,
        :new_pass => 'test1234',
        :confirmation => 'test1234'
    )
    assert(!change_password.valid?)
  end
end
