package fr.univtln.m1infodid.projet_s2.backend.server;

import fr.univtln.m1infodid.projet_s2.backend.Epigraphe;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpigrapheTest {
    //retourne si la valeur préalablement définie par la méthode "setId"
    @Test
    public void idTest() {
        Epigraphe epigraphe = Epigraphe.of();
        epigraphe.setId(1);
        assertEquals(1, epigraphe.getId());
    }

    //vérifier que la méthode "getDate" retourne la valeur précédemment définie avec la méthode "setDate".    @Test
    public void dateTest() {
        Epigraphe epigraphe = Epigraphe.of();
        Date date = new Date();
        epigraphe.setDate(date);
        assertEquals(date, epigraphe.getDate());
    }

    //vérifier que la méthode "getText" renvoie la valeur précédemment définie avec la méthode "setText".    @Test
    public void texteTest() {
        Epigraphe epigraphe = Epigraphe.of();
        epigraphe.setTexte("Le texte de l'épigraphe");
        assertEquals("Le texte de l'épigraphe", epigraphe.getTexte());
    }

    //vérifier si la méthode "getTraduction" retourne la valeur qui a été précédemment définie avec la méthode "setTraduction".
    @Test
    public void traductionTest() {
        Epigraphe epigraphe = Epigraphe.of();
        epigraphe.setTraduction("La traduction du texte de l'épigraphe");
        assertEquals("La traduction du texte de l'épigraphe", epigraphe.getTraduction());
    }

    // vérifier si la méthode "getNom" retourne la valeur qui a été précédemment définie avec la méthode "setNom".
    @Test
    public void NomTest() {
        Epigraphe epigraphe = Epigraphe.of();
        epigraphe.setNom("Nom de l'épigraphe");
        assertEquals("Nom de l'épigraphe", epigraphe.getNom());
    }

    //vérifier si la méthode "toString" reenvieee une chaîne de caractères qui contient les informations de l'objet Epigraphe sous forme d'une chaîne formatée.
    @Test
    public void toStringTest() {
        Epigraphe epigraphe = Epigraphe.off(1, new Date(), "Le texte de l'épigraphe", "La traduction du texte de l'épigraphe", "Nom de l'épigraphe", "https://example.com/image.jpg");
        String expectedString = "Epigraphie : n°1, Nom de l'épigraphe, Le texte de l'épigraphe, " + epigraphe.getDate() + ", https://example.com/image.jpg, La traduction du texte de l'épigraphe";
        assertEquals(expectedString, epigraphe.toString());
    }

}
