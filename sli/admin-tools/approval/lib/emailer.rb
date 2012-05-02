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
#}
#email = Emailer.new email_conf
#email.send_approval_email('devldapuser@slidev.org', 'TestFN', 'TestLN')

class Emailer
  DefaultHost = "127.0.0.1"
  DefaultPort = 25
  DefaultSenderName = "Anonymous"
  DefaultSenderEmailAddr = "none@none"
  DefaultSubject = "Your account has been approved."
  DefaultContent = "Your SLI sandbox account has been approved.\n" <<
    "\n\nPlease log into the control panel to get started.\n" <<
    "\n\nhttp://controlpanel.example.com/login\n\n"

  def initialize(args = {})
    @host = args[:host] || DefaultHost
    @port = args[:port] || DefaultPort
    @sender_name = args[:sender_name] || DefaultSenderName
    @sender_email_addr = args[:sender_email_addr] || DefaultSenderEmailAddr
  end
  
  def send_approval_email(email_addr, first, last, subject = DefaultSubject, 
    content = DefaultContent)

    message = "From: #@sender_name <#@sender_email_addr>\n" +
      "To: #{first} #{last} <#{email_addr}>\n" +
      "Subject: #{subject}\n\n#{content}"

    Net::SMTP.start(@host, @port) do |smtp|
      smtp.send_message message, @sender_email_addr, email_addr
    end
  end
end
