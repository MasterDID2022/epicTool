package fr.univtln.m1infodid.projet_s2.backend.server;

import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.exceptions.ListeVide;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SITest {
    String id = "340";
    String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=" + id;
    ArrayList<String> contentdImageEtText = SI.extractTextAndImageFromXml(id, xmlUrl);

    SITest() throws Exception {
    }

    @Test
    void testGetImgUrlWithValidParameters() {
        String id = "42";
        String imgNumber = "3";
        String expectedUrl = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/3.jpg";
        String actualUrl = SI.getImgUrl(id, imgNumber);
        assertEquals(expectedUrl, actualUrl);
    }
    @Test

    void testExtractTextAndImageFromXmlSize() {

        // Vérification que la taille de la liste est correcte
        assertEquals(6, contentdImageEtText.size());
    }
    /*@Test
    void testExtractTextAndImageFromXmlUrl() {
        // Vérification que le troisieme element est de type url
        String regex = "^https?://.+";
        assertEquals(true,contentdImageEtText.get(3).matches(regex) );
    }*/
    @Test
    void testExtractTextAndImageFromXmlOthers() {
        // Vérification des autres elements
        for(int i =0; i<5; i++) {
            assertNotNull(contentdImageEtText.get(i));
        }
    }
    @Test
    void testExtractTextAndImageFromXmlValidOutput() {
        assertEquals("http://ccj-epicherchel.huma-num.fr/interface/phototheque/340/113594.jpg", contentdImageEtText.get(3));
        //assertEquals("TALIS ▴ ER ▴ AT", contentdImageEtText.get(1));
        //assertEquals("Tel il était!", contentdImageEtText.get(2));
    }
    /*@Test
    void testExtractTextAndImageFromXmlInvalidUrl() throws Exception {
        String id = "340";
        String invalidUrl = "http://ccj-epicherchel.huma-num.fr/invalid-url";
        ArrayList<String> contentdImageEtText = SI.extractTextAndImageFromXml(id, invalidUrl);
        assertTrue(contentdImageEtText.isEmpty());
    }*/
    @Test
    void testCreateEpigraphie() throws ParseException, ListeVide {
        ArrayList<String> contentList = new ArrayList<>();
        contentList.add("42");
        contentList.add("philippe");
        contentList.add("2022-05-05");
        contentList.add("http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg");
        contentList.add("Texte");
        contentList.add("Traduction");

        Epigraphe expectedEpigraphe = new Epigraphe();
        expectedEpigraphe.setId(42);
        expectedEpigraphe.setName("philippe");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        expectedEpigraphe.setDate(format.parse("2022-05-05"));
        expectedEpigraphe.setImgUrl("http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg");
        expectedEpigraphe.setText("Texte");
        expectedEpigraphe.setTranslation("Traduction");

        Epigraphe resultEpigraphe = SI.CreateEpigraphie(contentList);

        assertEquals(Integer.parseInt(contentList.get(0)), expectedEpigraphe.getId());
        assertEquals(resultEpigraphe.getDate(), expectedEpigraphe.getDate());
        assertEquals(resultEpigraphe.getName(), expectedEpigraphe.getName());
        assertEquals(resultEpigraphe.getTranslation(), expectedEpigraphe.getTranslation());
        assertEquals(resultEpigraphe.getText(), expectedEpigraphe.getText());
        assertEquals(resultEpigraphe.getName(), expectedEpigraphe.getName());
        assertEquals(resultEpigraphe.getTranslation(), expectedEpigraphe.getTranslation());
        assertEquals(resultEpigraphe.getText(), expectedEpigraphe.getText());
    }

}