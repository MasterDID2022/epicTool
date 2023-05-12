package fr.univtln.m1infodid.projet_s2.frontend.frontend;

import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiTest {

    /*
    Cette méthode teste la méthode sendRequestOf() de la classe Api en envoyant une requête avec un ID valide.
    *Elle vérifie que le résultat n'est pas nul.
     */
    @Test
    public void testSendRequestOf() {
        Integer id = 1;
        String result = Api.sendRequestOf(id);
        Assertions.assertNotNull(result);
    }

    /*
    *teste la méthode sendRequestOf() de la classe Api en envoyant une requête avec un ID invalide.
    *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
    public void testSendRequestOfWithInvalidId() {
        Integer id = -1;
        String result = Api.sendRequestOf(id);
        assertEquals("", result);
    }

    /*
    *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations vide.
    *Elle vérifie que le résultat n'est pas nul.
     */
    @Test
    public void testPostAnnotations() {
        String annotationsJson = "{\"annotations\": []}";
        String result = Api.postAnnotations(annotationsJson);
        Assertions.assertNotNull(result);
    }

    /*
    *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations invalide.
    *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
    public void testPostAnnotationsWithInvalidJson() {
        String annotationsJson = "{\"annotations\" []}";
        String result = Api.postAnnotations(annotationsJson);
        assertEquals("", result);
    }

    /*
     *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations nulle.
     *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
    public void testPostAnnotationsWithNullJson() {
        String annotationsJson = null;
        String result = Api.postAnnotations(annotationsJson);
        assertEquals("", result);
    }

    /*
    *Test réussi - vérification de la valeur de retour
     */
    @Test
    public void testSendRequestOfSuccess() {
        Integer id = 42;
        String expectedResult = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/42/88617.jpg";
        String actualResult = Api.sendRequestOf(id);
        assertEquals(expectedResult, actualResult);
    }

    /*
    *Test en cas d'échec de la requête (404 Not Found)
     */
    @Test
    public void testSendRequestOfRequestFailure() {
        Integer id = 100000;
        String expectedResult = "";
        String actualResult = Api.sendRequestOf(id);
        assertEquals(expectedResult, actualResult);
    }





}
