require 'net/imap'
require './emailer'

DefaultEmailAddr = 'devldapuser@slidev.org'
DefaultUser = 'devldapuser'
DefaultPassword = 'Y;Gtf@w{'
DefaultSenderName = 'Test Sender'

time = Time.now.getutc

email_conf = {
  :host => 'mon.slidev.org',
  :sender_name => DefaultSenderName,
  :sender_email_addr => DefaultEmailAddr
}
email = Emailer.new email_conf
email.send_approval_email(DefaultEmailAddr, 'TestFN', 'TestLN', 'This is a test', "Time: #{time}")
puts "Approval email sent"

imap = Net::IMAP.new('mon.slidev.org', 993, true, nil, false)
imap.authenticate('LOGIN', DefaultUser, DefaultPassword)
imap.examine('INBOX')

found = false
imap.search(["FROM", DefaultSenderName]).each do |message_id|
  content = imap.fetch(message_id, "BODY[TEXT]")[0].attr["BODY[TEXT]"]
  if(content.include?("#{time}"))
    found = true
  end
  break if found
end
imap.disconnect

raise "No email contains #{time}" if !found
puts "Test ran successfully"
