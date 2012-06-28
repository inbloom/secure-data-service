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


require 'test_helper'

class AppTest < ActiveSupport::TestCase
  test "get apps" do
    apps = App.all
    assert_not_nil(apps)
    assert_equal(apps.size, 2)
  end
  
  test "new app" do
    app = App.new
    assert_not_nil(app)
  end
  
  test "apps can filter the admin app" do
    full = App.all
    filtered = App.all_but_admin
    assert(full.size == filtered.size + 1, "We shouldn't have one less app")
    filtered.each do |app|
      assert(!app.respond_to?(:endpoints), "No app should have endpoints")
    end
  end
  
  # test "save app" do
  #   app = App.new(@app_fixtures["new"])
  #   assert(app.valid?, "This should be valid")
  #   assert_nothing_raised(Exception) { app.save }
  # end
end