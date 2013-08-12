package com.ddumanskiy.arduino.mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * User: ddumanskiy
 * Date: 8/12/13
 * Time: 11:48 PM
 */
public class MailTLS {

    private static final Logger log = LogManager.getLogger(MailTLS.class);

    private Properties props = new Properties();
    private final String username = "doom22895@gmail.com";
    private final String password = "testjopa1234";

    public MailTLS() {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void sendMail(String sendTo, String subject, String body) {
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(sendTo));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            log.error("Error sending mail to {}", sendTo);
            log.error(e);
        }

    }

    public static void main(String[] args) {
        MailTLS mail = new MailTLS();
        mail.sendMail("doom369@gmail.com", "Hello", "Pupkin");
    }

}
