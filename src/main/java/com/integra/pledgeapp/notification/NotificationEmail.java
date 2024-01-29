package com.integra.pledgeapp.notification;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.utilities.Properties_Loader;

public class NotificationEmail {

	static String MAIL_SMTP_AUTH = null;
	static String MAIL_SMTP_STARTTLS_ENABLE = null;
	static String MAIL_SMTP_HOST = null;
	static String MAIL_SMTP_PORT = null;
	static String MAIL_SENDER = null;
	static String MAIL_SENDER_PASSWORD = null;

	static {
		mailConfig();
	}

	private static void mailConfig() {
		MAIL_SMTP_AUTH = Properties_Loader.getMAILSMTPAUTH();
		MAIL_SMTP_STARTTLS_ENABLE = Properties_Loader.getMAILSMTPSTARTTLSENABLE();
		MAIL_SMTP_HOST = Properties_Loader.getMAILSMTPHOST();
		MAIL_SMTP_PORT = Properties_Loader.getMAILSMTPPORT();
		MAIL_SENDER = Properties_Loader.getMAILSENDER();
		MAIL_SENDER_PASSWORD = Properties_Loader.getMAILPASSWORD();
	}

	// to send email notification to BCs receiver=emp, cc=HR
	public static JSONObject sendEmail(String content, String subject, String receiver, String cc, JSONArray fileList) {
		JSONObject jsRes = new JSONObject();
		Properties props = new Properties();
		try {
			
			props.put("mail.smtp.auth", MAIL_SMTP_AUTH);
			props.put("mail.smtp.starttls.enable", MAIL_SMTP_STARTTLS_ENABLE);
			props.put("mail.smtp.host", MAIL_SMTP_HOST);
			props.put("mail.smtp.port", MAIL_SMTP_PORT);

			// Get the Session object.
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(MAIL_SENDER, MAIL_SENDER_PASSWORD);
				}
			});

			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(MAIL_SENDER));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));

			// Set CC: header field of the header.
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

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

			// if file and filename exists
			if (fileList != null) {
				if (fileList.length() > 0) {
					for (int i = 0; i < fileList.length(); i++) {
						// Part two is attachment
						messageBodyPart = new MimeBodyPart();
						JSONObject fileDetails = (JSONObject) fileList.get(i);
						DataSource source = new FileDataSource(new File(fileDetails.getString("filePath")));
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(fileDetails.getString("fileName"));
						multipart.addBodyPart(messageBodyPart);
					}
				}
			}

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			System.out.println("Email sent successfully to " + receiver + "," + cc);
			jsRes.put("status", "SUCCESS");
			jsRes.put("statusDetails", "Email sent successfully");

		} catch (MessagingException | JSONException e) {
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
				e.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return jsRes;
	}
}
