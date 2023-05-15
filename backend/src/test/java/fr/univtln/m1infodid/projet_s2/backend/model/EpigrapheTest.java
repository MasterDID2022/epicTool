package fr.univtln.m1infodid.projet_s2.backend.model;

import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EpigrapheTest {
    //retourne si la valeur préalablement définie par la méthode "setId"
    @Test
    void idTest () {
        Epigraphe epigraphe = new Epigraphe();
        epigraphe.setId(1);
        assertEquals(1, epigraphe.getId());
    }

    @Test
        //vérifier que la méthode "getDate" retourne la valeur précédemment définie avec la méthode "setDate".    @Test
    void dateTest () {
        Epigraphe epigraphe = new Epigraphe();

        Date date = new Date();
        epigraphe.setDate(date);
        assertEquals(date, epigraphe.getDate());
    }

    //vérifier que la méthode "getText" renvoie la valeur précédemment définie avec la méthode "setText".    @Test
    @Test
    void texteTest () {
        Epigraphe epigraphe = new Epigraphe();
        epigraphe.setText(List.of("Le texte de l'épigraphe"));
        assertEquals("Le texte de l'épigraphe", epigraphe.getText().get(0));
    }

    //vérifier si la méthode "getTraduction" retourne la valeur qui a été précédemment définie avec la méthode "setTraduction".
    @Test
    void traductionTest () {
        Epigraphe epigraphe = new Epigraphe();
        epigraphe.setTranslation("La traduction du texte de l'épigraphe");
        assertEquals("La traduction du texte de l'épigraphe", epigraphe.getTranslation());
    }

    // vérifier si la méthode "getName" retourne la valeur qui a été précédemment définie avec la méthode "setName".
    @Test
    void NomTest () {
        Epigraphe epigraphe = new Epigraphe();
        epigraphe.setName("Nom de l'épigraphe");
        assertEquals("Nom de l'épigraphe", epigraphe.getName());
    }

    //vérifier si la méthode "toString" reenvieee une chaîne de caractères qui contient les informations de l'objet Epigraphe sous forme d'une chaîne formatée.
    @Test
    //
    void toStringTest () {
        Epigraphe epigraphe = Epigraphe.of(1, "https://example.com/image.jpg", new Date(), LocalDate.now(),"La traduction du texte de l'épigraphe", "Nom de l'épigraphe", List.of("Le texte de l'épigraphe"));
        String expectedString = "Epigraphie : n°1, Nom de l'épigraphe, [Le texte de l'épigraphe], " + epigraphe.getDate() + ", https://example.com/image.jpg, La traduction du texte de l'épigraphe";
        assertEquals(expectedString, epigraphe.toString());
    }

}
