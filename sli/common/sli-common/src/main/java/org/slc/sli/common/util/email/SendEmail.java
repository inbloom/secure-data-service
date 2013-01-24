package org.slc.sli.common.util.email;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail {
	   
//	   public String emailTo="";
//	   public String emailFrom="";
//	   public String emailSubject="";
//	   public String emailBody="";
	   
	   public void sendMail(String to, String from, String subject, String body)
	   {    
	      // Recipient's email ID needs to be mentioned.
	      //String to = "lchen@wgen.net";

	      // Sender's email ID needs to be mentioned
	      //String from = "lchen@wgen.net";

	      // Assuming you are sending email from localhost
	      String host = "mail.wgenhq.net";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject(subject);

	         // Now set the actual message
	         message.setText(body);

	         // Send message
	         Transport.send(message);
	         //System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	    	  mex.getMessage();
	         //mex.printStackTrace();
	      }
	   }
	   
	   public static void main(String [] args){
		   SendEmail se = new SendEmail(); 
		   se.sendMail("lchen@wgen.net","lchen@wgen.net","Hackathon","Hackathon is fun!");
		   
	   }

}
