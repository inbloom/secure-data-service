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
    app
  end
end