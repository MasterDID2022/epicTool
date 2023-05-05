package fr.univtln.m1infodid.projets2;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SITest {
    String id = "340";
    String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=" + id;
    ArrayList<String> contentdImageEtText = SI.extractTextAndImageFromXml(id, xmlUrl);

    @Test
    public void testGetImgUrlWithValidParameters() {
        String id = "42";
        String imgNumber = "3";
        String expectedUrl = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/3.jpg";
        String actualUrl = SI.getImgUrl(id, imgNumber);
        assertEquals(expectedUrl, actualUrl);
    }
    @Test
    public void testExtractTextAndImageFromXmlSize() {

        // Vérification que la taille de la liste est correcte
        assertEquals(3, contentdImageEtText.size());
    }
    @Test
    public void testExtractTextAndImageFromXmlUrl() {
        // Vérification que le premier element est de type url
        String regex = "^https?://.+";
        assertEquals(true,contentdImageEtText.get(0).matches(regex) );
    }
    @Test
    public void testExtractTextAndImageFromXmlOthers() {
        // Vérification des deux autres elements
        assertTrue( contentdImageEtText.get(1) instanceof String);
        assertTrue( contentdImageEtText.get(2) instanceof String);
    }
    @Test
    public void testExtractTextAndImageFromXmlValidOutput() {
        assertEquals("http://ccj-epicherchel.huma-num.fr/interface/phototheque/340/113594.jpg", contentdImageEtText.get(0));
        //assertEquals("TALIS ▴ ER ▴ AT", contentdImageEtText.get(1));
        //assertEquals("Tel il était!", contentdImageEtText.get(2));
    }

    @Test
    public void testExtractTextAndImageFromXmlInvalidUrl() {
        String id = "340";
        String invalidUrl = "http://ccj-epicherchel.huma-num.fr/invalid-url";
        ArrayList<String> contentdImageEtText = SI.extractTextAndImageFromXml(id, invalidUrl);
        assertTrue(contentdImageEtText.isEmpty());
    }

}