require 'test_helper'

class ApplicationMailerTest < ActionMailer::TestCase
  def teardown
    ActionMailer::Base.deliveries.clear
  end
  
  def test_notify_developer
    app = App.new(@app_fixtures['waffles'])    
    # Send the email, then test that it got queued
    email = ApplicationMailer.notify_developer(app).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
    # Test the body of the sent email contains what we expect it to
    assert_equal [app.metaData.createdBy], email.to
  end
  
  def test_notify_developer_bad
    app = App.new(@app_fixtures['admin'])
    email = ApplicationMailer.notify_developer(app).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Messages should have been delivered"
    assert email.to.nil?, "Bad addresses don't go through"
  end
  
  def test_notify_operator
    app = App.new(@app_fixtures['admin'])
    email = ApplicationMailer.notify_operator('operator@slidev.org', app).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
  end
  
  def test_notify_operator_bad
    app = App.new(@app_fixtures['admin'])
    email = ApplicationMailer.notify_operator(nil, app).deliver
    assert !ActionMailer::Base.deliveries.empty?, "Message should have been delivered"
    assert email.to.nil?, "Bad addresses don't go through"
  end
  
end