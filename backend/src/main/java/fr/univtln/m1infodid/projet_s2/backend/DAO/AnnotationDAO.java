package fr.univtln.m1infodid.projet_s2.backend.DAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AnnotationDAO implements AutoCloseable{

    private EntityManager entityManager;
    private AnnotationDAO ( EntityManager entityManager ) {
        this.entityManager = entityManager;
    }

    public static AnnotationDAO create ( EntityManager entityManager ) {
        return new AnnotationDAO(entityManager);
    }


    public Annotation findById(int id)
    {
        return entityManager.find(Annotation.class,id);
    }

    public List<Annotation> findAll()
    {
        TypedQuery<Annotation> query = entityManager.createQuery("SELECT M FROM Annotation as M ",Annotation.class);
        return query.getResultList();
    }


    public Annotation persist(Annotation annotation) {
        Epigraphe epigraphe = annotation.getEpigraphe();
        try {
            EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(entityManager); //NOSONAR (L'entity manager sera fermé par la classe)
            //l'entity manager sera fermé par la suite.
            annotation.setEpigraphe(epigrapheDAO.getEpigraphe(epigraphe.getId()));
        }
        catch (Exception e)  {
            return null;
        }
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.persist(annotation);
        entityTransaction.commit();
        return annotation;
    }

    public void remove(int id)
    {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.remove(findById(id));
        entityTransaction.commit();
    }

    public void remove(Annotation annotation)
    {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.remove(annotation);
        entityTransaction.commit();
    }

    @Override
    public void close() throws Exception {
        entityManager.close();

    }
}
