package fr.univtln.m1infodid.projet_s2.backend.DAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
@Disabled(" Pour lancer ce test veuillez remplacer la update par create-drop dans le persistence.xml")
public class AnnotationDAOTest extends AnnotationDAOTestManager {

    @Test
    void findByIdTest() throws SQLException {
        AnnotationDAO annotationDAO = AnnotationDAO.create(entityManager);
        Annotation annotation = Annotation.of(23);
        annotationDAO.persist(annotation);
        assertEquals(annotation,annotationDAO.findById(annotation.getIdAnnotation()));
        annotationDAO.remove(annotation);
    }

    @Test
    void findAllTest() throws SQLException {
        AnnotationDAO annotationDAO = AnnotationDAO.create(entityManager);
        List<Annotation> annotationList;
        List<Annotation> annotationList1;

        annotationList = entityManager.createQuery("SELECT A FROM Annotation as A", Annotation.class).getResultList();
        annotationList1 = annotationDAO.findAll();
        Annotation annotation = annotationDAO.persist( Annotation.of(55));

        assertEquals(annotationList,annotationList1);
    }

    @Test
    void persistAnnotationTest() throws SQLException {
        Annotation annotation = annotationDAO.persist( Annotation.of(55));
        assertEquals(annotation,annotationDAO.findById(annotation.getIdAnnotation()));
        annotationDAO.remove(annotation);
    }

    @Test
    void removeTestAnnotation() throws SQLException {
        List<Annotation> size_before = annotationDAO.findAll();
        Annotation annotation = Annotation.of(33);
        annotationDAO.persist(annotation);
        assertDoesNotThrow(() -> annotationDAO.remove(annotation));
        assertEquals(size_before,annotationDAO.findAll());
    }

}
