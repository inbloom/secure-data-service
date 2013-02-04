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

class ApplicationMailerTest < ActionMailer::TestCase
  def teardown
    ActionMailer::Base.deliveries.clear
  end

  def test_welcome_email
    user = {
        :first => "testFirst",
        :last => "testLast",
        :emailAddress => "test@test.com",
        :vendor => "testVendor",
        :password => "secret"
    }
    email = ApplicationMailer.welcome_email(user).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
  end

  def test_verify_email
    email_address = "test@test.com"
    firstName = "testFirst"
    userEmailValidationLink = "http://test.com/validationLink"
    email = ApplicationMailer.verify_email(email_address,firstName,userEmailValidationLink).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
  end

  def test_provision_email
    email_address = "test@test.com"
    firstName = "testFirst"
    serverName = "testServer"
    edorgId = "testEdorgId"
    email = ApplicationMailer.provision_email(email_address, firstName, serverName, edorgId).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
  end

  def test_notify_developer
    app = App.new(@app_fixtures['waffles'])
    # Send the email, then test that it got queued
    email = ApplicationMailer.notify_developer(app, "Test User").deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
    # Test the body of the sent email contains what we expect it to
    assert_equal [app.metaData.createdBy], email.to
  end

  def test_notify_developer_bad
    app = App.new(@app_fixtures['admin'])
    email = ApplicationMailer.notify_developer(app, "Test User").deliver
    assert ActionMailer::Base.deliveries.empty?, "Messages should NOT have been delivered"
    assert email.nil?, "Bad addresses don't go through"
  end

  def test_notify_operator
    app = App.new(@app_fixtures['admin'])
    email = ApplicationMailer.notify_operator('operator@slidev.org', app, "DevFn DevLn").deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
  end

  def test_notify_operator_bad
    app = App.new(@app_fixtures['admin'])
    email = ApplicationMailer.notify_operator(nil, app, "DevFn DevLn").deliver
    assert ActionMailer::Base.deliveries.empty?, "Message should NOT have been delivered"
    assert email.nil?, "Bad addresses don't go through"
  end

end
