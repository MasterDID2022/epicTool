package fr.univtln.m1infodid.projet_s2.frontend.frontend;

import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

  class ApiTest {

    /*
    Cette méthode teste la méthode sendRequestOf() de la classe Api en envoyant une requête avec un ID valide.
    *Elle vérifie que le résultat n'est pas nul.
     */
    @Test
      void testSendRequestOf() {
        Integer id = 1;
        List<String> result = Api.sendRequestOf(id);
        Assertions.assertNotNull(result);
    }

    /*
    *teste la méthode sendRequestOf() de la classe Api en envoyant une requête avec un ID invalide.
    *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
     void testSendRequestOfWithInvalidId() {
        Integer id = -1;
        List<String> result = Api.sendRequestOf(id);
        assertEquals(0, result.size());
    }

    /*
    *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations vide.
    *Elle vérifie que le résultat n'est pas nul.
     */
    @Test
     void testPostAnnotations() {
        String annotationsJson = "{\"annotations\": []}";
        String result = Api.postAnnotations(annotationsJson);
        Assertions.assertNotNull(result);
    }

    /*
    *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations invalide.
    *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
     void testPostAnnotationsWithInvalidJson() {
        String annotationsJson = "{\"annotations\" []}";
        String result = Api.postAnnotations(annotationsJson);
        assertEquals("", result);
    }

    /*
     *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations nulle.
     *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
     void testPostAnnotationsWithNullJson() {
        String annotationsJson = null;
        String result = Api.postAnnotations(annotationsJson);
        assertEquals("", result);
    }

    /*
    *Test réussi - vérification de la valeur de retour
     */
    @Test
     void testSendRequestOfSuccess() {
        Integer id = 42;
        List<String> expectedResult = List.of("étude Philippe Leveau", 
                                                "Thu Jul 12 00:00:00 GMT 2018", 
                                                "\nAux dieux Mânes, à Caius Iulius Neptunalis a vécu 25 ans. Caius Iulius Atticus à son frère très vertueux.            ", 
                                                "http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg",
                                                "D M",
                                                "C • IVL • NEPTVNALI",
                                                "VIX • ANNIS • XXV",
                                                "C • IVLIVS • ATTICVS",
                                                "FRATRI • PIISSIMO",
                                                "S • T • T • L                ");
        List<String> actualResult = Api.sendRequestOf(id);
        assertEquals(expectedResult, actualResult);
    }

    /*
    *Test en cas d'échec de la requête (404 Not Found)
     */
    @Test
     void testSendRequestOfRequestFailure() {
        Integer id = 100000;
        List<String> actualResult = Api.sendRequestOf(id);
        assertEquals(0, actualResult.size());
    }

    @ParameterizedTest
    @CsvSource({
            "Tom@gmail.com:/ ,{[39?",
            "Tom@gmail.com:/: ,{[39?",
            "Tom@gmail.com:Seraph"
    })
    void testPostLoginOK(String credentials) {
       String loginCredential = Base64.getEncoder().encodeToString((credentials).getBytes());
       String response = Api.postLogin(loginCredential);
       assertFalse(response.isEmpty());
       assertEquals("Bienvenue Tom@gmail.com !", response);
    }

    @Test
     void testpostEmptyPassword(){
        String loginCredential = Base64.getEncoder().encodeToString( "Tom@gmail.com:".getBytes());
        String reponse = Api.postLogin(loginCredential);
        assertTrue(reponse.isEmpty());
    }






}
