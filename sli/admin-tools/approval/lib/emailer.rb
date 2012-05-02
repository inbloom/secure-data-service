require 'net/smtp'

# The example below sends a message from Administrator with email address "admin@slidev.org"
# to Joe Chung with email address "joechung@slidev.org" via SMTP server "mon.slidev.org"
# using default port (25):
# 
#email_conf = {
#  :host => 'mon.slidev.org',
#  :port => 25,
#  :sender_name => 'Test Sender',
#  :sender_email_addr => 'devldapuser@slidev.org',
#  :subject => 'This is a test',
#  :content => 'Hello, World!'
#}
#email = Email.new email_conf
#email.send_approval_email('devldapuser@slidev.org', 'TestFN', 'TestLN')

class Emailer
  DefaultHost = "127.0.0.1"
  DefaultPort = 25
  DefaultSenderName = "Anonymous"
  DefaultSenderEmailAddr = "none@none"
  DefaultSubject = ""
  DefaultContent = ""

  def initialize(args = {})
    @host = args[:host] || DefaultHost
    @port = args[:port] || DefaultPort
    @sender_name = args[:sender_name] || DefaultSenderName
    @sender_email_addr = args[:sender_email_addr] || DefaultSenderEmailAddr
    @subject = args[:subject] || DefaultSubject
    @content = args[:content] || DefaultContent
  end
  
  def send_approval_email(email_addr, first, last)
    message = "From: #@sender_name <#@sender_email_addr>\n" +
      "To: #{first} #{last} <#{email_addr}>\n" +
      "Subject: #@subject\n\n#@content"

    Net::SMTP.start(@host, @port) do |smtp|
      smtp.send_message message, @sender_email_addr, email_addr
    end
  end
end

