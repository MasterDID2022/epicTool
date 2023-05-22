package fr.univtln.m1infodid.projet_s2.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.backend.DAO.AnnotationDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.FormulaireDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import fr.univtln.m1infodid.projet_s2.backend.model.Utilisateur;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


@Slf4j
public class Facade {


    /**
     * Contructeur prive de facade ne devrait jamais être instancié
     */
    private Facade () {
        throw new IllegalStateException("ne devrait pas etre instancié");
    }

    public static List<Annotation> getAnnotationOfEpigraphe ( int id ) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
             EntityManager em = emf.createEntityManager();
             AnnotationDAO annotationDAO = AnnotationDAO.create(em)) {
            log.info(annotationDAO.findAnnotationsOfEpigraphe(id).toString());
            return annotationDAO.findAnnotationsOfEpigraphe(id);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }    private static String secretKey = getSecretKey();

    public static void updateEpigraphe ( Annotation that, Integer idEpigraphe, String emailUtilisateur ) {
        log.info("Epigraphe" + idEpigraphe);
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
             EntityManager em = emf.createEntityManager();
             EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(em);
             UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(em)) {
            that.setEpigraphe(epigrapheDAO.getEpigraphe(idEpigraphe));
            em.getTransaction().begin();
            Utilisateur u = utilisateurDAO.findByEmail(emailUtilisateur).orElseThrow(Exception::new);
            that.setUtilisateur(u);
            if (that.getEpigraphe().getAnnotations().contains(that)) {
                that.getEpigraphe().getAnnotations().remove(that);
                em.flush();
                that.getEpigraphe().getAnnotations().add(that);
            } else that.getEpigraphe().addAnnotation(that);
            em.merge(that.getEpigraphe());
            em.getTransaction().commit();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private static String getSecretKey () {
        if (System.getenv("SECRET_KEY") == null) {
            setSecretKey("leNomDuChienEstLongCarC'ESTainsiqu'ILlef@ut");
        }
        return System.getenv("SECRET_KEY");
    }

    /**
     * Fonction pour mettre une fausse clef pour les
     * tests unitaire
     */
    public static void setSecretKey ( String motDePassePourTest ) {
        secretKey = motDePassePourTest;
    }

    /**
     * Fonction qui permet la création de l'objet Session avec authentification
     *
     * @param props les propriétés de config pour la session
     * @param mail  l'adresse mail à utiliser pour l'authentification
     * @param pwd   le mot de passe associé à l'adresse mail pour l'authentification
     * @return un objet de type Session configuré avec l'authentification
     */
    public static Session createSession ( Properties props, String mail, String pwd ) {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication () {
                return new PasswordAuthentication(mail, pwd);
            }
        });
    }

    /**
     * Fonction qui crée et retourne un objet Message à partir des paramètres
     * fournis
     *
     * @param success   un booléen indiquant si la demande de création de compte a
     *                  été validée ou non
     * @param session   l'objet Session utilisé pour la création du Message
     * @param fromEmail l'adresse mail de l'expéditeur
     * @param toEmail   l'adresse mail du destinataire
     * @return un objet de type Message configuré avec les informations de l'email
     */
    public static Message createMsgCont ( Boolean success, Session session, String fromEmail, String toEmail ) throws MessagingException, IOException {
        Message message = new MimeMessage(session);
        // Définition de l'expéditeur, du destinataire et du sujet
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        // Création du contenu du message
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart textPart = new MimeBodyPart();
        String emailContent;
        if (Boolean.TRUE.equals(success)) {
            message.setSubject("Demande de création de compte acceptée");
            emailContent = "Bonjour,<br><br>Votre demande de création de compte a été acceptée. Vous pouvez désormais accéder à notre plateforme. Merci et bienvenue !"
                    +
                    "<br><br> Cordialement,<br>L'équipe de notre plateforme.";
        } else {
            message.setSubject("Demande de création de compte refusée");
            emailContent = "Bonjour,<br><br>Nous regrettons de vous informer que votre demande de création de compte a été refusée. Malheureusement, nous ne sommes pas en mesure de vous accorder l'accès à notre plateforme pour le moment. Nous vous remercions tout de même pour votre intérêt."
                    +
                    "<br><br>Cordialement,<br>L'équipe de notre plateforme.";
        }
        textPart.setContent(emailContent, "text/html;charset=utf-8");
        multipart.addBodyPart(textPart);
        MimeBodyPart imagePart = new MimeBodyPart();
        String path = Objects.requireNonNull(SI.class.getResource("/plaque_epigraphe.png")).getPath();
        imagePart.attachFile(path);
        multipart.addBodyPart(imagePart);
        message.setContent(multipart);
        return message;
    }

    /**
     * Fonction qui permet de configurer et retourner les propriétés SMTP pour
     * l'envoi d'emails via Hotmail/Outlook.com
     *
     * @return un objet Properties contenant les propriétés SMTP configurées
     */
    public static Properties configSMTP () {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");
        return props;
    }

    /**
     * Fonction qui permet l'envoie d'un email de validation du formulaire spécifié
     *
     * @param success un booléen indiquant si la demande de création de compte a
     *                été validée ou non
     * @param mail    du demandeur
     */
    public static void sendMail ( Boolean success, String mail ) {
        final String fromEmail = "projetsdids23@hotmail.com"; // adresse mail du gestionnaire
        final String password = System.getenv("MY_PASSWORD");
        Properties props = configSMTP();
        Session session = createSession(props, fromEmail, password);
        try {
            Message message = createMsgCont(success, session, fromEmail, mail);

            // Envoi du message
            Transport.send(message);
            log.info("E-mail envoyé avec succès !");
        } catch (MessagingException | IOException e) {
            log.error("Une erreur s'est produite lors de l'envoi du message : " + e.getMessage());
        }
    }

    /**
     * Récupère la liste des utilisateurs à partir de la base de données.
     *
     * @return une liste d'objets Utilisateur
     */
    public static List<Utilisateur> obtenirUtilisateurs () {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU");
             EntityManager entityManager = entityManagerFactory.createEntityManager();
             UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)) {
            utilisateurs = utilisateurDAO.findAll();
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération des utilisateurs", e);
        }
        return utilisateurs;
    }

    /**
     * Convertit une liste d'utilisateurs en une chaîne de caractères JSON.
     * Chaque utilisateur est représenté par un objet JSON contenant l'ID et l'email.
     *
     * @param utilisateurs la liste d'utilisateurs à convertir
     * @return une chaîne de caractères représentant le JSON des utilisateurs
     */
    public static String convertirUtilisateursEnJSON ( List<Utilisateur> utilisateurs ) {
        List<String> listUtilisateurs = new ArrayList<>();
        for (Utilisateur utilisateur : utilisateurs) {
            String jsonUtilisateur = "{\"id\": \"" + utilisateur.getId() + "\", \"email\": \"" + utilisateur.getEmail() + "\"}";
            listUtilisateurs.add(jsonUtilisateur);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(listUtilisateurs);
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la conversion des utilisateurs en JSON", e);
            return "";
        }
    }

    /**
     * Récupère la liste des utilisateurs à partir de la base de données et les renvoie sous forme de JSON.
     * Chaque utilisateur est représenté par un objet JSON contenant l'ID et l'email.
     *
     * @return une chaîne de caractères représentant le JSON des utilisateurs
     */
    public static String recupereUtilisateurs () {
        List<Utilisateur> utilisateurs = obtenirUtilisateurs();
        return convertirUtilisateursEnJSON(utilisateurs);
    }

    /**
     * Convertit une liste de formulaire en une chaîne de caractères JSON.
     * Chaque formulaire est représenté par un objet JSON contenant l'ID et l'email.
     *
     * @return une chaîne de caractères représentant le JSON des formulaires
     */
    public static String recupererFormulaires () {
        List<Formulaire> formulaires = FormulaireDAO.findFormulaireNotValidated();
        List<String> listFormulaires = new ArrayList<>();
        for (Formulaire formulaire : formulaires) {
            String jsonForm = "{\"id\": \"" + formulaire.getId() + "\", \"email\": \"" + formulaire.getEmail() + "\"}";
            listFormulaires.add(jsonForm);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(listFormulaires);
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la conversion des utilisateurs en JSON", e);
            return "";
        }
    }

    /**
     * Genere une clee, à partir de la variable statique
     *
     * @return Key pour les token
     */
    public static Key generateKey () {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }




}
