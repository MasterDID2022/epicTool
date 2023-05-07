package fr.univtln.m1infodid.projets2;

import fr.univtln.m1infodid.projets2.exceptions.ListeVide;
import fr.univtln.m1infodid.projets2.exceptions.SaxErreur;
import fr.univtln.m1infodid.projets2.exceptions.UrlInvalide;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Test
    void testExtractTextAndImageFromXmlUrl() {
        // Vérification que le troisieme element est de type url
        String regex = "^https?://.+";
        assertEquals(true,contentdImageEtText.get(3).matches(regex) );
    }
    @Test
    void testExtractTextAndImageFromXmlOthers() {
        // Vérification des autres elements
        assertTrue( contentdImageEtText.get(0) instanceof String);
        assertTrue( contentdImageEtText.get(1) instanceof String);
        assertTrue( contentdImageEtText.get(2) instanceof String);
        assertTrue( contentdImageEtText.get(4) instanceof String);
        assertTrue( contentdImageEtText.get(5) instanceof String);
    }
    @Test
    void testExtractTextAndImageFromXmlValidOutput() {
        assertEquals("http://ccj-epicherchel.huma-num.fr/interface/phototheque/340/113594.jpg", contentdImageEtText.get(3));
        //assertEquals("TALIS ▴ ER ▴ AT", contentdImageEtText.get(1));
        //assertEquals("Tel il était!", contentdImageEtText.get(2));
    }

    @Test
    void testExtractTextAndImageFromXmlWithInvalidXmlSaxError() {
        String id = "123";
        String invalidXmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=" + id + "1";
        assertThrows(SaxErreur.class, () -> {
            SI.extractTextAndImageFromXml(id, invalidXmlUrl);
        });
    }
    @Test
    void testExtractTextAndImageFromXmlWithInvalidUrl() {
        String id = "123";
        String invalidUrl = "path/to/invalid/xml/file.xml" + id + "1";
        assertThrows(UrlInvalide.class, () -> {
            SI.extractTextAndImageFromXml(id, invalidUrl);
        });
    }
    @Test
    void testCreateEpigraphieFromEmptyListe(){
        ArrayList<String> contentList = new ArrayList<>();
        assertThrows(ListeVide.class, () -> {
            SI.CreateEpigraphie(contentList);
        });

    }

    @Test
    void testCreateEpigraphie() throws ParseException, ListeVide {
        ArrayList<String> contentList = new ArrayList<>();
        contentList.add("42");
        contentList.add("philippe");
        contentList.add("2022-05-05");
        contentList.add("http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg");
        contentList.add("Texte");
        contentList.add("Traduction");

        Epigraphe expectedEpigraphe = Epigraphe.of();
        expectedEpigraphe.setId(42);
        expectedEpigraphe.setNom("philippe");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        expectedEpigraphe.setDate(format.parse("2022-05-05"));
        expectedEpigraphe.setImgUrl("http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg");
        expectedEpigraphe.setTexte("Texte");
        expectedEpigraphe.setTraduction("Traduction");

        Epigraphe resultEpigraphe = SI.CreateEpigraphie(contentList);

        assertEquals(Integer.parseInt(contentList.get(0)), expectedEpigraphe.getId());
        assertEquals(resultEpigraphe.getDate(), expectedEpigraphe.getDate());
        assertEquals(resultEpigraphe.getNom(), expectedEpigraphe.getNom());
        assertEquals(resultEpigraphe.getTraduction(), expectedEpigraphe.getTraduction());
        assertEquals(resultEpigraphe.getTexte(), expectedEpigraphe.getTexte());
    }

}