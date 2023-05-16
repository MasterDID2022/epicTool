package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.exceptions.ListeVide;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.mail.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Document;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static fr.univtln.m1infodid.projet_s2.backend.SI.*;
import static org.junit.jupiter.api.Assertions.*;

class SITest {
    String id = "340";
    String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=" + id;
    List<List<String>> contentdImageEtText = SI.extractContentFromXML(id, xmlUrl);

    SITest () throws Exception {
    }

    @Test
    void testGetImgUrlWithValidParameters () {
        String id = "42";
        String imgNumber = "3";
        String expectedUrl = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/3.jpg";
        String actualUrl = SI.getImgUrl(id, imgNumber);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testExtractTextAndImageFromXmlSize () {
        // Vérification que la taille de la liste est correcte
        assertEquals(6, contentdImageEtText.size());
    }


    @Test
    void testExtractTextAndImageFromXmlOthers () {
        // Vérification des autres elements
        for (int i = 0; i < 5; i++) {
            assertNotNull(contentdImageEtText.get(i));
        }
    }

    @Test
    void testExtractTextAndImageFromXmlValidOutput () {
        assertEquals("http://ccj-epicherchel.huma-num.fr/interface/phototheque/340/113594.jpg", contentdImageEtText.get(3).get(0));
    }

    @Test
    public void testGetXMLFromUrl() throws Exception {
        String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/340/113594.jpg";
        InputStream result = SI.getXMLFromUrl(xmlUrl);
        assertNotNull(result);
    }

    @Test
    public void testCreateXMLDoc() throws ParserConfigurationException, SAXException, IOException {
        String xml = "<root><element>test</element></root>";
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        Document result = SI.createXMLDoc(inputStream);
        assertNotNull(result);

        assertTrue(result.getDocumentElement().getNodeName().equals("root"));
    }

    @Test
    public void testExtraction() throws ParserConfigurationException, IOException, SAXException {
        List<List<String>> contentList = new ArrayList<>();
        List<String> transcriptionList = new ArrayList<>();
        String xmlString = "<root><persName>Adaline</persName></root>";

        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
        Document doc = SI.createXMLDoc(inputStream);

        Element root = doc.getDocumentElement();
        Element persName = (Element)root.getElementsByTagName("persName").item(0);

        SI.extraction(contentList, transcriptionList, persName, "", persName.getChildNodes(), 0);

        assertEquals("Adaline", contentList.get(0).get(0));
    }


    @Test
    public void testExtractFromBalise() throws ParserConfigurationException, SAXException, IOException {
        String xmlString = "<root>\n" +
                "  <test1 type=\"translation\">Contenu 1</test1>\n" +
                "  <test2 when=\"date\">Contenu 2</test2>\n" +
                "  <persName>Adaline</persName>\n" +
                "</root>\n";
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
        Document doc = SI.createXMLDoc(inputStream);

        List<List<String>> contentList = new ArrayList<>();
        List<String> transcriptionList = new ArrayList<>();
        String id = "1";
        List<List<String>> result = SI.extractFromBalise(contentList, transcriptionList, doc, id);

        assertEquals(5, result.size());
        assertEquals("Contenu 1", result.get(1).get(0));
    }


    @Test
    void testCreateEpigraphie () throws ParseException, ListeVide {
        List<List<String>>  contentList = new ArrayList<>();
        contentList.add(List.of("42"));
        contentList.add(List.of("philippe"));
        contentList.add(List.of("2022-05-05"));
        contentList.add(List.of("http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg"));
        contentList.add(List.of("Traduction"));
        contentList.add(List.of("Texte"));

        Epigraphe expectedEpigraphe = new Epigraphe();
        expectedEpigraphe.setId(42);
        expectedEpigraphe.setName("philippe");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        expectedEpigraphe.setDate(format.parse("2022-05-05"));
        expectedEpigraphe.setImgUrl("http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg");
        expectedEpigraphe.setText(List.of("Texte"));
        expectedEpigraphe.setTranslation("Traduction");

        Epigraphe resultEpigraphe = SI.CreateEpigraphie(contentList);

        assertEquals(resultEpigraphe.getId(), expectedEpigraphe.getId());
        assertEquals(resultEpigraphe.getDate(), expectedEpigraphe.getDate());
        assertEquals(resultEpigraphe.getName(), expectedEpigraphe.getName());
        assertEquals(resultEpigraphe.getImgUrl(), expectedEpigraphe.getImgUrl());
        assertEquals(resultEpigraphe.getTranslation(), expectedEpigraphe.getTranslation());
        assertEquals(resultEpigraphe.getText(), expectedEpigraphe.getText());
    }


    @Test
    void testcreateSession(){
        Properties properties = new Properties();
        String email ="projets2did@hotmail.com";
        String mdp = "did9projet";
        Session result = createSession(properties,email,mdp);
        assertEquals(properties, result.getProperties());
    }


    @Test
    void testcreateMsgCont() throws MessagingException, IOException {
        Boolean success = true;
        Session session = Session.getDefaultInstance(new Properties());
        String fromEmail ="send@hotmail.com";
        String toEmail = "receiver@hotmail.com";
        Message result = createMsgCont(success,session,fromEmail,toEmail);
        assertEquals(fromEmail,result.getFrom()[0].toString());
        assertEquals(toEmail,result.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("text/plain", result.getContentType());
    }


    @Test
    void testconfigSMTP(){
        Properties properties_r = configSMTP();
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "587");
        assertEquals(properties,properties_r);
    }


    @Test
    //@Disabled("problème d'ENVVAR")
    void testfindPassword(){
        sendMail(true, Formulaire.of(0,"","","test@gmail.com","",""));
    }

}
