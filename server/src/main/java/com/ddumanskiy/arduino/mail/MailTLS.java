package com.ddumanskiy.arduino.mail;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * User: ddumanskiy
 * Date: 8/12/13
 * Time: 11:48 PM
 */
public class MailTLS {

    private static final Logger log = LogManager.getLogger(MailTLS.class);

    private final static Properties props = new Properties();
    private final static String username = "doom22895@gmail.com";
    private final static String password = "testjopa1234";

    private static String body;

    static  {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        body = readFileInAString();
    }

    public static void sendMail(String sendTo, String subject, String id) {
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
            message.setText(body.replace("{ID}", id));

            Transport.send(message);
        } catch (MessagingException e) {
            log.error("Error sending mail to {}", sendTo);
            log.error(e);
        }

    }

    private static String readFileInAString() {
        try {
            return IOUtils.toString(MailTLS.class.getResourceAsStream("/mail-body.txt"), "UTF-8");
        } catch (IOException e) {
            log.error("Error reading mail body from file.", e);
        }
        return "";
    }

    public static void main(String[] args) {
        MailTLS.sendMail("doom369@gmail.com", "Hello", UUID.randomUUID().toString());
    }

}
