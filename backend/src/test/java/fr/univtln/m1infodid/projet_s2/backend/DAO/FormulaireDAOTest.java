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
        Formulaire formulaire = Formulaire.of( "Nom4", "Prenom4", "email4389@test.com", "test", "Affiliation4", "Commentaire4");

        Formulaire formulaireExistant = FormulaireDAO.findByIdFormulaire(formulaire.getId());
        if (formulaireExistant != null) {
            FormulaireDAO.deleteFormulaire(formulaire.getId());
        }
        else {
            FormulaireDAO.createFormulaire(formulaire);

            Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(formulaire.getId());
            System.err.println(formulaireRecupere);
            System.err.println(formulaire);
            assertTrue(formulaire.equals(formulaireRecupere));

            FormulaireDAO.deleteFormulaire(formulaire.getId());
        }
    }

    /*
     * Teste la méthode de la mise a jour d'un formulaire dans la base de données.
     */
    @Test
    void testUpdateFormulaire() {
        Formulaire formulaire = Formulaire.of( "Nom4", "Prenom4", "email4389@test.com", "test", "Affiliation4", "Commentaire4");
        FormulaireDAO.createFormulaire(formulaire);
        formulaire.setNom("Nouveau nom");
        FormulaireDAO.updateFormulaire(formulaire);
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(formulaire.getId());
        assertEquals("Nouveau nom", formulaireRecupere.getNom());
        FormulaireDAO.deleteFormulaire(formulaire.getId());
    }

    /*
     * Teste la méthode de la suppression  d'un formulaire dans la base de données.
     */
    @Test
    void testDeleteFormulaire() {
        Formulaire formulaire = Formulaire.of( "Nom4", "Prenom4", "email438g@test.com", "test", "Affiliation4", "Commentaire4");
        FormulaireDAO.createFormulaire(formulaire);
        FormulaireDAO.deleteFormulaire(formulaire.getId());
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(formulaire.getId());
        assertNull(formulaireRecupere);
    }

    /*
    Teste la méthode de recherche d'un formulaire dans la base de données en utilisant son ID.
    */
    @Test
    void testFindByIdFormulaire() {
        Formulaire formulaire = Formulaire.of( "Nom4", "Prenom4", "email4389@test.com", "test", "Affiliation4", "Commentaire4");
        FormulaireDAO.createFormulaire(formulaire);
        Formulaire formulaireRecupere = FormulaireDAO.findByIdFormulaire(formulaire.getId());
        assertEquals(formulaire, formulaireRecupere);
        FormulaireDAO.deleteFormulaire(formulaire.getId());

    }

    /**
     * Teste la méthode de recherche de tous les formulaires dans la base de données.
     */
    @Test
    void testFindAllFormulaire() {
        Formulaire formulaire1 = Formulaire.of( "Nom5", "Prenom5", "emailhaj5@test.com", "test", "Affiliation5", "Commentaire5");
        Formulaire formulaire2 = Formulaire.of( "Nom6", "Prenom6", "email6ji@test.com", "test", "Affiliation6", "Commentaire6");
        FormulaireDAO.createFormulaire(formulaire1);
        FormulaireDAO.createFormulaire(formulaire2);
        List<Formulaire> formulaireListe = FormulaireDAO.findAllFormulaire();
        assertTrue(formulaireListe.contains(formulaire1));
        assertTrue(formulaireListe.contains(formulaire2));
        FormulaireDAO.deleteFormulaire(formulaire1.getId());
        FormulaireDAO.deleteFormulaire(formulaire2.getId());

    }

}