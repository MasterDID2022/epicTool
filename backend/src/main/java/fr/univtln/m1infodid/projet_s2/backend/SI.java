package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.exceptions.*;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.mail.internet.AddressException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Cette classe SI ...
 */
public class SI {
    private static String imgPath = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/";
    public static final String URL_EPICHERCHELL = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=";

    private SI () {
    }


    /**
     * @param id l'id de la fiche
     * @param imgNumber
     * @return l url de l image qui sera egale au chemin imgPath + id + imgNumber
     */
    public static String getImgUrl ( String id, String imgNumber ) {
        return imgPath + id + '/' + imgNumber + ".jpg";
    }


    /**
     * Fonction qui retourne le contenu du fichier xml rechercher à l'aide de son url
     *
     * @param xmlUrl url du fichier XML
     * @return contenu du fichier XML
     */
    public static InputStream getXMLFromUrl(String xmlUrl) throws IOException {
        URL url = new URL(xmlUrl);
        return url.openStream();
    }

    /**
     * Fonction qui permet de créer le document xml
     *
     * @param inputStream  contenu du fichier XML
     * @return document XML
     */
    public static Document createXMLDoc(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(inputStream);
    }


    /**
     * Fonction qui extrait l'image et l'ajoute à la contentList
     *
     * @param contentList  liste à laquelle on rajoute le contenu
     * @param nodeList  liste de tous les noeuds du doc XML
     * @param id  id de la fiche
     * @param a  indice de l'element 'facsimile' dans le nodeList
     */
    public static void extractImage(List<List<String>> contentList, NodeList nodeList, String id, int a){
        Node child = nodeList.item(a + 1);
        Element firstElement = (Element) child;
        //si une seule image existe
        if (firstElement.getTagName().equals("graphic"))
            contentList.add(List.of(firstElement.getAttribute("url")));
            //sinon on fait appel a notre fonction
        else if (firstElement.getTagName().equals("desc")) {
            String imgNum = firstElement.getTextContent();
            contentList.add(List.of(getImgUrl(id, imgNum)));
        }
    }


    /**
     * Fonction qui permet d'extraire des elements d'un fichier xml et les stocke dans la contentList
     *
     * @param contentList  liste pour stocker le contenu extrait
     * @param element  element XML duquel extraire le contenu
     * @param id  id de la fiche
     * @param nodeList  liste de tous les noeuds du doc XML
     * @param a  indice de l'element dans le nodeList
     */
    public static void extraction(List<List<String>> contentList, List<String> transcriptionList, Element element, String id, NodeList nodeList, int a){
        switch (element.getTagName()){
            //l'emplacement de texte sur le fichier xml
            /*case "text":
                contentList.add(nodeList.item(a + 3).getTextContent());break;*/
            //recherche transcription ligne par ligne
            case "lb":
                transcriptionList.add(nodeList.item(a).getNextSibling().getTextContent());break;
            //recherche nom auteur
            case "persName":
                contentList.add(List.of(nodeList.item(a).getTextContent()));break;
            //recherche date
            case "date":
                contentList.add(List.of(((Element) nodeList.item(a)).getAttribute("when")));break;
            //recupere l image
            case "facsimile":
                extractImage(contentList, nodeList, id, a);
                break;
        }
    }



    /**
     * Fonction qui permet d'extraire le contenu des balises d'un document xml et le stocke dans la contentList
     *
     * @param contentList  liste pour stocker le contenu extrait
     * @param doc  document XML
     * @param id  id de la fiche
     * @return liste qui contient le résultat de l'extraction
     */
    public static List<List<String>> extractFromBalise(List<List<String>> contentList, List<String> transcriptionList, Document doc, String id) {
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("*");
        contentList.add(List.of(id));
        for (int a = 0; a < nodeList.getLength(); a++) {
            Node node = nodeList.item(a);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                extraction(contentList, transcriptionList, element, id, nodeList, a);

                //recupere le contenu de traduction
                if (element.hasAttribute("type") && element.getAttribute("type").equals("translation")) {
                    contentList.add(List.of(element.getTextContent()));
                }
                if (element.hasAttribute("when") && element.getAttribute("when").equals("date")) {
                    contentList.add(List.of(element.getTextContent()));
                }
            }
        }
        contentList.add(transcriptionList);
        return contentList;
    }



    /**
     * Fonction qui permet d'extraire le contenu d'un document xml à partir d'une url et utilise les fonctions précédentes
     *
     * @param id  id de la fiche
     * @param xmlUrl  url du document XML
     * @return liste qui contient le contenu des balises
     */
    public static List<List<String>> extractContentFromXML(String id, String xmlUrl) throws Exception {
        List<List<String>> contentList = new ArrayList<>();
        List<String> transcriptionList = new ArrayList<>();
        try {
            InputStream inputStream = getXMLFromUrl(xmlUrl);
            Document doc = createXMLDoc(inputStream);
            extractFromBalise(contentList,transcriptionList,doc,id);
            inputStream.close();
        } catch (MalformedURLException e) {
            throw new UrlInvalide();
        } catch (IOException e) {
            throw new RecuperationXml();
        } catch (ParserConfigurationException | SAXException e) {
            throw new DomParser();
        } catch (Exception e) {
            throw new ExtractionXml();
        }
        return contentList;
    }


    /**
     * Fonction qui permet de renvoyer un object 'epigraphe' en remplissant ses attributs à l'aide de la liste contenant contenu du doc xml
     *
     * @param contentList liste contenant le contenu extrait d'un doc xml
     */
    public static Epigraphe CreateEpigraphie (List<List<String>> contentList) throws ListeVide {
        Epigraphe epigraphe = new Epigraphe();
        try {
            if (contentList == null || contentList.isEmpty()) {
                throw new ListeVide();
            }

            parseValue(contentList,epigraphe);

        } catch (IndexOutOfBoundsException r) {
            throw new ListeVide();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        epigraphe.setFetchDate(LocalDate.now());
        return epigraphe;
    }

    /**
     * Fonction qui permet d'extraire la date de l'epigraphe et la stocke dans l'object 'epigraphe'
     *
     * @param contentList liste contenant le contenu d'un doc xml
     * @param epigraphe object qu'on veut remplir avec le donnees extraites
     */
    public static void parseDate(List<List<String>> contentList, Epigraphe epigraphe) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (contentList.get(2).get(0).isEmpty() || contentList.get(2).get(0).isBlank())
            epigraphe.setDate(null);
        else
            epigraphe.setDate(format.parse(contentList.get(2).get(0)));
    }


    /**
     * Fonction qui permet d'extraire les infos de l'epigraphe et les affecte aux attributs de l'object 'epigraphe'
     *
     * @param contentList liste contenant le contenu d'un doc xml
     * @param epigraphe object qu'on veut remplir avec le donnees extraites
     */
    public static void parseValue(List<List<String>> contentList, Epigraphe epigraphe) throws ParseException {
        epigraphe.setId(Integer.parseInt(contentList.get(0).get(0)));
        epigraphe.setName(contentList.get(1).get(0));
        parseDate(contentList,epigraphe);
        epigraphe.setImgUrl(contentList.get(3).get(0));
        epigraphe.setTranslation(contentList.get(4).get(0));
        epigraphe.setText(contentList.get(5));
    }

    public static Epigraphe CreateEpigraphie ( int id ) throws Exception {
        return SI.CreateEpigraphie(
                SI.extractContentFromXML(String.valueOf(id), URL_EPICHERCHELL + id));
    }


    /**
     * Fonction qui permet le chargement du fichier de configuration contenant le mot de passe
     *
     * @return un objet Properties contenant les propriétés de configuration chargées depuis le fichier
     */
    public static Properties configFich(){
        Properties configProps = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream("config.properties")) {
            configProps.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return configProps;
    }


    /**
     * Fonction qui permet la création de l'objet Session avec authentification
     *
     * @param props les propriétés de config pour la session
     * @param mail l'adresse mail à utiliser pour l'authentification
     * @param pwd le mot de passe associé à l'adresse mail pour l'authentification
     * @return un objet de type Session configuré avec l'authentification
     */
    public static Session createSession(Properties props, String mail, String pwd){
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mail, pwd);
            }
        });
        return session;
    }


    /**
     * Fonction qui crée et retourne un objet Message à partir des paramètres fournis
     *
     * @param success un booléen indiquant si la demande de création de compte a été validée ou non
     * @param session l'objet Session utilisé pour la création du Message
     * @param fromEmail l'adresse mail de l'expéditeur
     * @param toEmail l'adresse mail du destinataire
     * @return un objet de type Message configuré avec les informations de l'email
     */
    public static Message createMsgCont(Boolean success, Session session, String fromEmail, String toEmail) throws MessagingException {
        Message message = new MimeMessage(session);

        // Définition de l'expéditeur, du destinataire et du sujet
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Validation du formulaire");

        // Contenu du message
        String emailContent = null;
        if (success == true)
            emailContent = "Bonjour,<br><br>Votre demande de creation de compte a ete acceptee.";
        else
            emailContent = "Bonjour,<br><br>Votre demande de creation de compte a ete refusee.";
        message.setContent(emailContent, "text/html");
        return message;
    }


    /**
     * Fonction qui permet de configurer et retourner les propriétés SMTP pour l'envoi d'emails via Hotmail/Outlook.com
     *
     * @return un objet Properties contenant les propriétés SMTP configurées
     */
    public static Properties configSMTP(){
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
     * @param success un booléen indiquant si la demande de création de compte a été validée ou non
     * @param formulaire Llobjet Formulaire contenant les informations du formulaire
     */
    public static void sendMail(Boolean success, Formulaire formulaire){
        final String fromEmail = "mail_gest@hotmail.com"; // adresse mail du gestionnaire
        final String password = configFich().getProperty("email.password");
        final String toEmail = formulaire.getEmail(); // adresse mail du destinataire

        Properties props = configSMTP();
        Session session = createSession(props,fromEmail,password);

        try {
            Message message = createMsgCont(success,session,fromEmail,toEmail);

            // Envoi du message
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès !");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}