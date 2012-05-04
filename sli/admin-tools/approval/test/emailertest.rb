require "test/unit"
require 'rumbster'
require 'message_observers'
require './emailer'

class TestEmails < Test::Unit::TestCase
  DefaultPort = 2525
  DefaultSenderEmailAddr = 'admin@slidev.org'
  DefaultReceiverEmailAddr = 'devldapuser@slidev.org'

  def setup
    @rumbster = Rumbster.new(DefaultPort)
    @message_observer = MailMessageObserver.new
    @rumbster.add_observer @message_observer
    @rumbster.start
  end

  def teardown
    @rumbster.stop
  end

  def test_email_is_sent
    email_conf = {
      :sender_email_addr => DefaultSenderEmailAddr,
      :port => DefaultPort
    }
    email = Emailer.new email_conf
    email.send_approval_email(DefaultReceiverEmailAddr, 'TestFN', 'TestLN')

    assert_equal 1, @message_observer.messages.size
    assert_equal [DefaultReceiverEmailAddr], @message_observer.messages.first.to
    assert_equal [DefaultSenderEmailAddr], @message_observer.messages.first.from
  end
end
