package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailS {
    public static void sendEmail(String toEmail, String subject, String body) {
        // Paramètres SMTP du serveur de messagerie sortant
        String host = "smtp.live.com";
        String port = "587";

        // Création des propriétés pour la session de messagerie
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);

        // Informations d'identification
        String username = "projets2did@hotmail.com";
        String password = "did9projet";

        // Création de la session de messagerie
        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Envoi du message
            Transport.send(message);

            System.out.println("E-mail sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send the email: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String toEmail = "projets2did@hotmail.com";
        String subject = "Testing Subject";
        String body = "This is a test email.";

        sendEmail(toEmail, subject, body);
    }
}
