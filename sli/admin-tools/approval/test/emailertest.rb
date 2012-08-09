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


require "test/unit"
require 'rumbster'
require 'message_observers'
require '../lib/emailer'

class TestEmails < Test::Unit::TestCase
  DefaultPort = 2525
  DefaultSenderEmailAddr = 'admin@slidev.org'
  DefaultReceiverEmailAddr = 'devldapuser@slidev.org'

  def setup
    @rumbster = Rumbster.new(DefaultPort)
    @message_observer = MailMessageObserver.new
    @rumbster.add_observer @message_observer
    @rumbster.start

    replacer = {
      '__URI__' => 'http://localhost:8080'
    }
    emailer_conf = {
      :sender_email_addr => DefaultSenderEmailAddr,
      :port              => DefaultPort,
      :replacer          => replacer
    }
    @emailer = Emailer.new emailer_conf
  end

  def teardown
    @rumbster.stop
    sleep 1
  end

  def test_email_is_sent
    @emailer.send_approval_email({
      :email_addr => DefaultReceiverEmailAddr,
      :name       => 'TestFN TestLN'
    })

    assert_equal 1, @message_observer.messages.size
    assert_equal [DefaultReceiverEmailAddr], @message_observer.messages.first.to
    assert_equal [DefaultSenderEmailAddr], @message_observer.messages.first.from
  end

  def test_email_replacer
    content = "Landing Zone: __URI__/landing_zone\n\nApp Registration: __URI__/apps"
    assert content.include?('__URI__')
    result = @emailer.replace(content)
    puts result
    assert result.include?('http://localhost:8080')
    assert !result.include?('__URI__')
  end
end
