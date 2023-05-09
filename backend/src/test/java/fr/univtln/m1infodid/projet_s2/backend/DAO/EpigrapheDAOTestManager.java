package fr.univtln.m1infodid.projet_s2.backend.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class EpigrapheDAOTestManager {
     protected EntityManagerFactory emf;
     protected EntityManager em;
     protected EpigrapheDAO epigrapheDAO;

    @BeforeEach
    public void setUp () {
        emf = Persistence.createEntityManagerFactory("EpiPU");
        em = emf.createEntityManager();
        epigrapheDAO = EpigrapheDAO.create(em);
    }

    @AfterEach
    public void tearDown () {
        if (!em.getTransaction().isActive()) em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM Epigraphe WHERE id < 0").executeUpdate();
        em.getTransaction().commit();
        em.close();
        emf.close();
    }
}
