package org.slc.sli.common.util.email;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendEmail {

	public void sendMail(String to, String from, String subject, String body) throws MessagingException
	{    
		// Assuming you are sending email from localhost
		String host = "mail.wgenhq.net";
		
		// Get system properties
		Properties properties = System.getProperties();
		
		// Setup mail server
		// FIXME: take mail host from SLI-wide properties
		properties.setProperty("mail.smtp.host", host);
		
		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);
		
		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);
		
		// Set From: header field of the header.
		message.setFrom(new InternetAddress(from));
		
		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		
		// TODO: allow a Cc: or Bcc: to the system operator for Customer Support monitoring
		
		// Set Subject: header field
		message.setSubject(subject);
		
		// Now set the actual message
		message.setText(body);
		
		// Send message
		Transport.send(message);
	}	
	   	
	public static void main(String [] args) throws MessagingException {
		SendEmail se = new SendEmail(); 
		se.sendMail("lchen@wgen.net","lchen@wgen.net","Hackathon","Hackathon is fun!");
	}
}
