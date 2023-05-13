package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j

class FormulaireDAOTest extends FormulaireDAOTestManager {



    /**
     * Teste la méthode de création d'un formulaire dans la base de données.
     */

    @Test
    void testpersist() {
        if (formulaireDAO.findById(101) != null)
            formulaireDAO.removeById(101);
        Formulaire formulaire = Formulaire.of(101, "Nom", "Prenom", "email@test.com", "Affiliation", "Commentaire");
        formulaireDAO.persist(formulaire);
        Formulaire formulaireRecupere = formulaireDAO.findById(101);
        System.err.println(formulaireRecupere);
        System.err.println(formulaire);
        assertEquals(formulaire, formulaireRecupere);
        formulaireDAO.removeById(101);
    }
    /*
     * Teste la méthode de la mise a jour d'un formulaire dans la base de données.
     */
    @Test
    void testupdate() {
        Formulaire formulaire = Formulaire.of(101, "Nom2", "Prenom2", "email2@test.com", "Affiliation2", "Commentaire2");
        formulaireDAO.persist(formulaire);
        formulaire.setNom("Nouveau nom");
        formulaireDAO.update(formulaire);
        Formulaire formulaireRecupere = formulaireDAO.findById(101);
        assertEquals("Nouveau nom", formulaireRecupere.getNom());
        formulaireDAO.removeById(101);
    }

    /*
     * Teste la méthode de la suppression  d'un formulaire dans la base de données.
     */
    @Test
    void testremoveById() {
        Formulaire formulaire = Formulaire.of(101, "Nom3", "Prenom3", "email3@test.com", "Affiliation3", "Commentaire3");
        formulaireDAO.persist(formulaire);
        formulaireDAO.removeById(101);
        Formulaire formulaireRecupere = formulaireDAO.findById(3);
        assertNull(formulaireRecupere);
    }

    /*
    Teste la méthode de recherche d'un formulaire dans la base de données en utilisant son ID.
    */
    @Test
    void testfindById() {
        Formulaire formulaire = Formulaire.of( 101,"Nom4", "Prenom4", "email4@test.com", "Affiliation4", "Commentaire4");
        formulaireDAO.persist(formulaire);
        Formulaire formulaireRecupere = formulaireDAO.findById(101);
        assertEquals(formulaire, formulaireRecupere);
        formulaireDAO.removeById(101);

    }

    /**
    * Teste la méthode de recherche de tous les formulaires dans la base de données.
    */
    @Test
    void testFindAllFormulaire() {
        Formulaire formulaire1 = Formulaire.of(100, "Nom5", "Prenom5", "email5@test.com", "Affiliation5", "Commentaire5");
        Formulaire formulaire2 = Formulaire.of(101, "Nom6", "Prenom6", "email6@test.com", "Affiliation6", "Commentaire6");
        formulaireDAO.persist(formulaire1);
        formulaireDAO.persist(formulaire2);
        List<Formulaire> formulaireListe = formulaireDAO.findAll();
        assertTrue(formulaireListe.contains(formulaire1));
        assertTrue(formulaireListe.contains(formulaire2));
        formulaireDAO.removeById(100);
        formulaireDAO.removeById(101);

    }

}
