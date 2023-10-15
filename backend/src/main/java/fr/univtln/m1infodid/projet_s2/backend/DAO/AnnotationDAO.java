package fr.univtln.m1infodid.projet_s2.backend.DAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AnnotationDAO implements AutoCloseable{

    private final EntityManager entityManager;
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

    public List<Annotation> findAnnotationsOfEpigraphe ( int idEpi ) {
        TypedQuery<Annotation> query = entityManager.createQuery("SELECT M FROM Annotation as M where epigraphe.id = " + idEpi, Annotation.class);
        return query.getResultList();
    }

    public Annotation findByIdEpiAndMail ( int idEpi, String email ) {
        TypedQuery<Annotation> query = entityManager.createQuery("SELECT M FROM Annotation as M join Utilisateur U on M.utilisateur.id = U.id where M.epigraphe.id = :id and U.email = :email", Annotation.class);
        query.setParameter("email", email);
        query.setParameter("id", idEpi);
        return query.getSingleResult();
    }

    public Annotation persist(Annotation annotation) {
        Epigraphe epigraphe = annotation.getEpigraphe();
        try {
            EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(entityManager); //NOSONAR (L'entity manager sera fermé par la classe)
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
