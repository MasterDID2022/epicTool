package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;

public class AnnotationDAO extends GenericDAO<Annotation, Integer> {

    private AnnotationDAO ( EntityManager entityManager ) {
        super(Annotation.class, entityManager);
    }

    public static AnnotationDAO create ( EntityManager entityManager ) {
        return new AnnotationDAO(entityManager);
    }
}
