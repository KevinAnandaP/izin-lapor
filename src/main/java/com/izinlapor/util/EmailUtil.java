package com.izinlapor.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    private static final String USERNAME = "izinlaporsystem@gmail.com";
    private static final String PASSWORD = "ieuj iivj cthb tqql";

    public static void sendNotification(String recipientEmail, String subject, String messageBody) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            System.out.println("Email recipient is empty, skipping notification.");
            return;
        }

        // Run in background thread to avoid freezing UI
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(USERNAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setText(messageBody);

                Transport.send(message);

                System.out.println("Email sent successfully to " + recipientEmail);

            } catch (MessagingException e) {
                e.printStackTrace();
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }).start();
    }
}
