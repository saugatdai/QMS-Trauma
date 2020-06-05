package qmsCore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LogEmailSender {
	public static void send(Exception e1) {
		final String from = "saugatsigdel@sxc.edu.np";
		final String to = "saugatdai@gmail.com";
		final String password = "Nepal'123";
		final String sub = "exception";
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		String dat = df.format(date).toString().substring(0, 8);

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		// get Session
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(sub);
			message.setText("Date : time => <b>" + dat + "</b>\n" + "Exception Message => <b>" + e1.getMessage()
					+ "</b>\n\n<p style = " + "background-color : F00;" + "font-family : Helvetica,serif;"
					+ "padding: 25px;" + LogFileStore.getStackTrace(e1) + "</div>");
			// send message
			Transport.send(message);
			System.out.println("message sent successfully");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

}
