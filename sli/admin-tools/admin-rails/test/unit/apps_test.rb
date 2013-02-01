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

  test "app version validation" do
    app = build_app
    app.version = nil
    assert !app.valid?, "App shouldn't be valid with a nil version"
    app.version = ""
    assert !app.valid?, "App shouldn't be valid with an empty string"
    app.version = "123445123412341235123412351512323513413413412351362354134663241"
    assert !app.valid?, "App should be less than 25 characters"
    app.version = "Super duper!"
    assert !app.valid?, "App can't contain special characters"
    app.version = "Waffles"
    assert app.valid?, "App is valid with a string"
  end


  # test "save app" do
  #   app = App.new(@app_fixtures["new"])
  #   assert(app.valid?, "This should be valid")
  #   assert_nothing_raised(Exception) { app.save }
  # end

  private
  def build_app
    app = App.new
    app.name = "name"
    app.description = "description"
    app.redirect_uri = "https://derp"
    app.application_url = "https://derp"
    app.vendor = "McDonalds"
    app.version = "1.0"
    app.administration_url = "https://morederp"
    app.image_url = "https://morederp"
    app
  end
end
