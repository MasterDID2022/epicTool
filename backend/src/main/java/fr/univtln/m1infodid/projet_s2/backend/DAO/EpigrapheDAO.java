package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j

public class EpigrapheDAO implements AutoCloseable {

    private EntityManager entityManager;



    private EpigrapheDAO ( EntityManager entityManager ) {
        this.entityManager = entityManager;
    }

    public static EpigrapheDAO create ( EntityManager entityManager ) {
        return createEpigrapheDAO(entityManager);
    }

    private static EpigrapheDAO createEpigrapheDAO ( EntityManager entityManager ) {
        return new EpigrapheDAO(entityManager);
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


    public void persist(Epigraphe epigraphe){
        entityManager.persist(epigraphe);
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


    public void remove(Epigraphe epigraphe) throws SQLException {
        remove(epigraphe.getId());
    }

    public Epigraphe getEpigraphe(int id) throws Exception {
        Epigraphe epigraphe = findById(id);

        if(epigraphe !=null) {
            LocalDate date = epigraphe.getFetchDate();

            if (date.isBefore(LocalDate.now().plusDays(2)))
                return epigraphe;
            remove(epigraphe);
        }
        epigraphe = SI.CreateEpigraphie(id);
        persist(epigraphe);
        return epigraphe;
    }


    @Override
    public void close () throws Exception {
        entityManager.close();
    }
}