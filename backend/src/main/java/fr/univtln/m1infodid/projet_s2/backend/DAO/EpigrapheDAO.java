package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;

@Slf4j

public class EpigrapheDAO implements AutoCloseable {

    private EntityManager entityManager;

    public static EpigrapheDAO create ( EntityManager entityManager ) {
        return new EpigrapheDAO(entityManager);
    }

    private EpigrapheDAO ( EntityManager entityManager ) {
        this.entityManager = entityManager;
    }


    /**
     * List of all the epigraphs.
     *
     * @return list of the epigraphs
     */
    public List<Epigraphe> findAll () {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Epigraphe> cq = cb.createQuery(Epigraphe.class);
        Root<Epigraphe> rootEntry = cq.from(Epigraphe.class);
        CriteriaQuery<Epigraphe> all = cq.select(rootEntry);

        TypedQuery<Epigraphe> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }

    /**
     * Find an epigraph using its id.
     *
     * @return the epigraph
     */
    public Epigraphe findById ( int id ) {
        return entityManager.find(Epigraphe.class, id);
    }


    /**
     * Persist a new epigraph.
     *
     * @return the epigraph persisted
     */
    public Epigraphe persistID ( int id ) {
        Epigraphe epi = new Epigraphe();
        epi.setId(id);
        entityManager.getTransaction().begin();
        entityManager.persist(epi);
        entityManager.getTransaction().commit();
        log.info("Epigraph " + id + " was persisted.");
        return epi;
    }


    /**
     * Remove an epigraph using its id.
     */
    public void remove ( int id ) throws SQLException {
        entityManager.getTransaction().begin();
        entityManager.remove(findById(id));
        entityManager.getTransaction().commit();
        log.info("Epigraph " + id + " was removed.");
    }


    @Override
    public void close () throws Exception {
        entityManager.close();
    }
}