require 'net/smtp'

# The example below sends a message from Administrator with email address "admin@slidev.org"
# to Joe Chung with email address "joechung@slidev.org" via SMTP server "mon.slidev.org"
# using default port (25):
# 
# email = Email.new
# email.server='mon.slidev.org'
# email.sender_name='Administrator'
# email.sender_email_addr='admin@slidev.org'
# email.send_approval_email('joechung@slidev.org', 'Joe', 'Chung')

class Email

  attr_accessor :server, :port, :sender_name, :sender_email_addr
  
  def send_approval_email(email_addr, first, last)
    message = "From: #@sender_name <#@sender_email_addr>
To: #{first} #{last} <#{email_addr}>
Subject: SMTP e-mail test

This is a test e-mail message."

    Net::SMTP.start(@server, @port ||= 25) do |smtp|
      smtp.send_message message, @sender_email_addr, email_addr
    end
  end

end

