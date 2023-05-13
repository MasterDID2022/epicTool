package fr.univtln.m1infodid.projet_s2.backend.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class FormulaireDAOTestManager {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    protected static FormulaireDAO formulaireDAO;
    @BeforeAll
    static void setUpBeforeClass() {
        emf = Persistence.createEntityManagerFactory("EpiPU");
    }
    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        formulaireDAO = FormulaireDAO.create(em);
    }
    @AfterEach
    void end(){
        em.close();
    }
    @AfterAll
    static void tearDownAfterClass() {
        emf.close();
    }
}
