package fr.univtln.m1infodid.projet_s2.backend.DAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
 class AnnotationDAOTest extends AnnotationDAOTestManager {

    @Test
    void findByIdTest() {
        Annotation annotation = Annotation.of(23);
        annotationDAO.persist(annotation);
        assertEquals(annotation,annotationDAO.findById(annotation.getIdAnnotation()));
        assertDoesNotThrow(() ->annotationDAO.remove(annotation));
    }

    @Test
    void findAllTest() {
        List<Annotation> annotationList;
        List<Annotation> annotationList1;

        annotationList = em.createQuery("SELECT A FROM Annotation as A", Annotation.class).getResultList();
        annotationList1 = annotationDAO.findAll();
        assertEquals(annotationList,annotationList1);
    }

    @Test
    void persistAnnotationTest() {
        Annotation annotation = annotationDAO.persist(Annotation.of(55));
        assertEquals(annotation,annotationDAO.findById(annotation.getIdAnnotation()));
        assertDoesNotThrow(() ->annotationDAO.remove(annotation));
    }

    @Test
    void removeAnnotation() {
        List<Annotation> size_before = annotationDAO.findAll();
        Annotation annotation = Annotation.of(33);
        annotationDAO.persist(annotation);
        assertDoesNotThrow(() -> annotationDAO.remove(annotation));
        assertEquals(size_before,annotationDAO.findAll());
    }

}
