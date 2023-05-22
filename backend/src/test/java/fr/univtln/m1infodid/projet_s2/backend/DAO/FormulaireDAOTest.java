package fr.univtln.m1infodid.projet_s2.backend.DAO;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
@Slf4j

class FormulaireDAOTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        emf = Persistence.createEntityManagerFactory("EpiPU");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        emf.close();
    }

    /**
     * Teste la méthode de création d'un formulaire dans la base de données.
     */

    @Test
    void testCreateFormulaire() {
        if (FormulaireDAO.findByIdFormulaire(101) != null)
            FormulaireDAO.deleteFormulaire(101);
        Formulaire formulaire = Formulaire.of(101, "Nom", "Prenom", "email@test.com", "hh","Affiliation", "Commentaire");
        FormulaireDAO.createFormulaire(formulaire);
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(101);
        System.err.println(formulaireRecupere);
        System.err.println(formulaire);
        assertTrue( formulaire.equals(formulaireRecupere) );
        FormulaireDAO.deleteFormulaire(101);
    }
    /*
     * Teste la méthode de la mise a jour d'un formulaire dans la base de données.
     */
    @Test
    void testUpdateFormulaire() {
        Formulaire formulaire = Formulaire.of(101, "Nom2", "Prenom2", "email2@test.com", "ll", "Affiliation2", "Commentaire2");
        FormulaireDAO.createFormulaire(formulaire);
        formulaire.setNom("Nouveau nom");
        FormulaireDAO.updateFormulaire(formulaire);
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(101);
        assertEquals("Nouveau nom", formulaireRecupere.getNom());
        FormulaireDAO.deleteFormulaire(101);
    }

    /*
     * Teste la méthode de la suppression  d'un formulaire dans la base de données.
     */
    @Test
    void testDeleteFormulaire() {
        Formulaire formulaire = Formulaire.of(101, "Nom3", "Prenom3", "email3@test.com", "lol","Affiliation3", "Commentaire3");
        FormulaireDAO.createFormulaire(formulaire);
        FormulaireDAO.deleteFormulaire(101);
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(3);
        assertNull(formulaireRecupere);
    }

    /*
    Teste la méthode de recherche d'un formulaire dans la base de données en utilisant son ID.
    */
    @Test
    void testFindByIdFormulaire() {
        Formulaire formulaire = Formulaire.of( 101,"Nom4", "Prenom4", "email4@test.com",  "lol", "Affiliation4", "Commentaire4");
        FormulaireDAO.createFormulaire(formulaire);
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(101);
        assertEquals(formulaire, formulaireRecupere);
        FormulaireDAO.deleteFormulaire(101);

    }

    /**
    * Teste la méthode de recherche de tous les formulaires dans la base de données.
    */
    @Test
    void testFindAllFormulaire() {
        Formulaire formulaire1 = Formulaire.of(100, "Nom5", "Prenom5", "email5@test.com", "lol", "Affiliation5", "Commentaire5");
        Formulaire formulaire2 = Formulaire.of(101, "Nom6", "Prenom6", "email6@test.com", "lol", "Affiliation6", "Commentaire6");
        FormulaireDAO.createFormulaire(formulaire1);
        FormulaireDAO.createFormulaire(formulaire2);
        List<Formulaire> formulaireListe = FormulaireDAO.findAllFormulaire();
        assertTrue(formulaireListe.contains(formulaire1));
        assertTrue(formulaireListe.contains(formulaire2));
        FormulaireDAO.deleteFormulaire(100);
        FormulaireDAO.deleteFormulaire(101);

    }

}
