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


require 'net/smtp'

# The example below sends a message from Administrator with email address "admin@slidev.org"
# to Joe Chung with email address "joechung@slidev.org" via SMTP server "mon.slidev.org"
# using default port (25):
#
#email_conf = {
#  :host => 'mon.slidev.org',
#  :port => 25,
#  :sender_name => 'Administrator',
#  :sender_email_addr => 'admin@slidev.org',
#}
#emailer = Emailer.new email_conf
#email = {
#  :email_addr => 'joechung@slidev.org',
#  :name => 'Joe Chung'
#}
#emailer.send_approval_email email

class Emailer
  #DefaultHost = "127.0.0.1"
  #DefaultPort = 25
  #DefaultSenderName = "Anonymous"
  #DefaultSenderEmailAddr = "none@none"
  #DefaultSubject = "Your account has been approved."
  #DefaultContent = "Your SLI sandbox account has been approved.\n" <<
  #  "\n\nPlease log into the control panel to get started.\n" <<
  #  "\n\nhttp://controlpanel.example.com/login\n\n"
  #DefaultReplacer = {}
  #
  #def initialize(args = {})
  #  @host = args[:host] || DefaultHost
  #  @port = args[:port] || DefaultPort
  #  @sender_name = args[:sender_name] || DefaultSenderName
  #  @sender_email_addr = args[:sender_email_addr] || DefaultSenderEmailAddr
  #  @replacer = args[:replacer] || DefaultReplacer
  #end
  #
  #def replace(content)
  #  result = content
  #  @replacer.each_pair do |k,v|
  #    result = result.gsub(k, v)
  #  end
  #  result
  #end
  #
  #def send_approval_email(args = {})
  #  email_addr = args[:email_addr]
  #  name       = args[:name]
  #  subject    = args[:subject] || DefaultSubject
  #  content    = args[:content] || DefaultContent
  #
  #  if content
  #    content = replace(content)
  #  end
  #
  #  message = "From: #@sender_name <#@sender_email_addr>\n" +
  #    "To: #{name} <#{email_addr}>\n" +
  #    "Subject: #{subject}\n\n#{content}"
  #
  #  Net::SMTP.start(@host, @port) do |smtp|
  #    smtp.send_message message, @sender_email_addr, email_addr
  #  end
  #end
end
