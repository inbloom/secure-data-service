package org.slc.sli.common.util.email;

import java.lang.String;
import java.lang.System;
import java.util.*;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendEmail {

	// Set up properties only once
	Properties properties;

	// Constructor
    public SendEmail() {
        properties = System.getProperties();
        // FIXME: take mail host from SLI-wide properties
        properties.setProperty("mail.smtp.host", "mail.wgenhq.net");
    }

    // Send a simple text message
    public void sendMail(String to, String from, String subject, String body) throws MessagingException, AddressException {
    	MimeMessage message = setupMessage(to, from, subject, body);
    	message.setText(body);
        Transport.send(message);
    }

    // Send a simple text message with bcc
    public void sendMail(String to, String from, String subject, String body, String bcc) throws MessagingException, AddressException {
        MimeMessage message = setupMessage(to, from, subject, body);
        message = addBcc(message, bcc);
        message.setText(body);
        Transport.send(message);
    }

    // Send message with multiple (String) attachments
    public void sendMailWithAttachment(String to, String from, String subject, String body, List<String> attachments) throws MessagingException, AddressException {
    	MimeMessage message = setupMessage(to, from, subject, body);
        createAttachmentBody(body, attachments, message);
    }

    private void createAttachmentBody(String body, List<String> attachments, MimeMessage message) throws MessagingException, AddressException{
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        for (String attachment : attachments) {
            messageBodyPart = new MimeBodyPart();
            String filename = attachment;
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
        }
        message.setContent(multipart);
        Transport.send(message);
    }

    // Send message with multiple (String) attachments and bcc
    public void sendMailWithAttachment(String to, String from, String subject, String body, String bcc, List<String> attachments) throws MessagingException, AddressException {
        MimeMessage message = setupMessage(to, from, subject, body);
        message = addBcc(message, bcc);
        createAttachmentBody(body, attachments, message);
    }

    // Set up the To:, From:, and Subject: parts (but not the body)
    public MimeMessage setupMessage(String to, String from, String subject, String body) throws MessagingException, AddressException {
    	Session session = Session.getDefaultInstance(properties);
    	MimeMessage message = new MimeMessage(session);
    	message.setFrom(new InternetAddress(from));
    	message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    	message.setSubject(subject);
    	return message;
    }

    public MimeMessage addBcc(MimeMessage message, String bcc) throws MessagingException, AddressException {
        message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
        return message;
    }

    public static void main(String[] args) throws MessagingException {
        SendEmail se = new SendEmail();
        se.sendMail("lchen@wgen.net", "lchen@wgen.net", "Hackathon", "Hackathon is fun!");
    }
}
