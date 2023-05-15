package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailS {
    public static void sendEmail(String toEmail, String subject, String body) {
        // Paramètres SMTP du serveur de messagerie sortant
        String host = "smtp.gmail.com";
        String port = "587";

        // Création des propriétés pour la session de messagerie
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Création de la session de messagerie
        Session session = Session.getInstance(props);

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("elmoukhhajar1234@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Envoi du message
            Transport.send(message);

            System.out.println("E-mail sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String toEmail = "elmoukhhajar1234@gmail.com";
        String subject = "Testing Subject";
        String body = "This is a test email.";

        sendEmail(toEmail, subject, body);
    }
}
