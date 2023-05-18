package fr.univtln.m1infodid.projet_s2.backend.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Polygone;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotationDAOTest extends AnnotationDAOTestManager {

    @Test
    void findByIdTest () {
        AnnotationDAO annotationDAO = AnnotationDAO.create(entityManager);
        Annotation annotation = Annotation.of(23);
        annotationDAO.persist(annotation);
        assertEquals(annotation, annotationDAO.findById(annotation.getIdAnnotation()));
        assertDoesNotThrow(() -> annotationDAO.remove(annotation));
    }

    @Test
    void findAllTest () {
        AnnotationDAO annotationDAO = AnnotationDAO.create(entityManager);
        List<Annotation> annotationList;
        List<Annotation> annotationList1;

        annotationList = entityManager.createQuery("SELECT A FROM Annotation as A", Annotation.class).getResultList();
        annotationList1 = annotationDAO.findAll();
        assertEquals(annotationList, annotationList1);
    }


    @Test
    void removeAnnotation () {
        List<Annotation> size_before = annotationDAO.findAll();
        Annotation annotation = Annotation.of(33);
        annotationDAO.persist(annotation);
        assertDoesNotThrow(() -> annotationDAO.remove(annotation));
        assertEquals(size_before, annotationDAO.findAll());
    }

    @Test
    void persistAnnotationTest () {
        Annotation annotation = Annotation.of(33);
        List<Polygone> l = annotation.getListCoordonesPoly();
        l.add(Polygone.create(0, 0, 1, 2));
        annotation.setListCoordonesPoly(l);
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU"); EntityManager em = emf.createEntityManager(); AnnotationDAO annotationDAO = AnnotationDAO.create(em)) {
            Assertions.assertDoesNotThrow(() -> annotationDAO.persist(annotation));
            Assertions.assertDoesNotThrow(() -> annotationDAO.remove(annotation));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void GetAnnotationFromJSONTest () {
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertDoesNotThrow(() -> mapper.readValue("{\"idEpigraphe\":33,\"listePoly\":[[0.0,0.0,1.0,2.0]]}]}", Annotation.class));
    }
}
