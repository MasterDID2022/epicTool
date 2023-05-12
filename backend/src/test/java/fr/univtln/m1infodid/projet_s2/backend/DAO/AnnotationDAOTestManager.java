package fr.univtln.m1infodid.projet_s2.backend.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AnnotationDAOTestManager {

    protected static EntityManager entityManager;
    protected static EntityManagerFactory entityManagerFactory;
    protected static AnnotationDAO annotationDAO;

    @BeforeAll
    public static void begin () {
        entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU");
    }

    @AfterAll
    static void endall () throws Exception {
        if (entityManagerFactory.isOpen())
            entityManagerFactory.close();
    }

    @BeforeEach
    void begineach () {
        entityManager = entityManagerFactory.createEntityManager();
        annotationDAO = AnnotationDAO.create(entityManager);
    }

    @AfterEach
    void end () throws Exception {
        annotationDAO.close();

    }
}
