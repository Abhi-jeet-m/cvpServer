package com.integra.pledgeapp.utilities;


import java.io.File;
import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;

import org.json.JSONException;
import org.json.JSONObject;


import javax.activation.*; 

public class MailHandler {

	
	
	public static void sendTextMail(String content,String subject,String receiver)
	{
	  
	     //Get the session object  
	      Properties props = System.getProperties();  
	      props.put("mail.smtp.auth", Properties_Loader.MAILSMTPAUTH);
	      props.put("mail.smtp.starttls.enable", Properties_Loader.MAILSMTPSTARTTLSENABLE);
	      props.put("mail.smtp.host", Properties_Loader.MAILSMTPHOST);
	      props.put("mail.smtp.port", Properties_Loader.MAILSMTPPORT);
	      Session session = Session.getInstance(props,
	    	         new javax.mail.Authenticator() {
	    	            protected PasswordAuthentication getPasswordAuthentication() {
	    	               return new PasswordAuthentication(Properties_Loader.MAILSENDER, Properties_Loader.MAILPASSWORD);
	    	            }
	    	         });
	  
	     //compose the message  
	      try{  
	         MimeMessage message = new MimeMessage(session);  
	         message.setFrom(new InternetAddress(Properties_Loader.MAILSENDER));  
	         message.addRecipient(Message.RecipientType.TO,new InternetAddress(receiver));  
	         message.setSubject(subject);  
	         message.setText(content);  
	  
	         // Send message  
	         Transport.send(message);  
	         System.out.println("Message sent successfully....");  
	  
	      }catch (MessagingException mex) {mex.printStackTrace();}  
	}
	
	public static void sendAttachmentMail(String content,String subject,String receiver,String cc,String filepath, JSONObject mailInfo)
	{
	      Properties props = new Properties();
	      props.put("mail.smtp.auth", Properties_Loader.MAILSMTPAUTH);
	      props.put("mail.smtp.starttls.enable", Properties_Loader.MAILSMTPSTARTTLSENABLE);
	      props.put("mail.smtp.host", Properties_Loader.MAILSMTPHOST);
	      props.put("mail.smtp.port", Properties_Loader.MAILSMTPPORT);

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(Properties_Loader.MAILSENDER, Properties_Loader.MAILPASSWORD);
	            }
	         });

	      try {
	         // Create a default MimeMessage object.
	         Message message = new MimeMessage(session);   

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(Properties_Loader.MAILSENDER));

	         // Set To: header field of the header.
	         message.setRecipients(Message.RecipientType.TO,
	            InternetAddress.parse(receiver));

	         // Set CC: header field of the header.
	         message.setRecipients(Message.RecipientType.CC,
	 	            InternetAddress.parse(cc));
	         
	         // Set Subject: header field
	         message.setSubject(subject);

	         // Create the message part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
	         messageBodyPart.setText(content);

	         // Create a multipart message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         messageBodyPart = new MimeBodyPart();
	         DataSource source = new FileDataSource(new File(filepath));
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName("SignedDocument-"+mailInfo.getString("company")+"-"+mailInfo.getString("empid")+".pdf");
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart);

	         // Send message
	         Transport.send(message);

	         System.out.println("Sent message successfully....");
	  
	      } catch (MessagingException | JSONException e) {
	         throw new RuntimeException(e);
	      }
	}
	
}
