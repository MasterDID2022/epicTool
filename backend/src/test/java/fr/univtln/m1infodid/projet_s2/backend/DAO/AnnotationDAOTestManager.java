package fr.univtln.m1infodid.projet_s2.backend.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AnnotationDAOTestManager {

    protected EntityManager entityManager;
    protected EntityManagerFactory entityManagerFactory;
    protected AnnotationDAO annotationDAO;

    @BeforeEach
    public void start()
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU");
        entityManager = entityManagerFactory.createEntityManager();
        annotationDAO = AnnotationDAO.create(entityManager);
    }


    @AfterEach
    public void End()
    {
        entityManager.close();
        entityManagerFactory.close();
    }

}
