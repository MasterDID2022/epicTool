package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Facade {


    /**
     * Contructeur prive de facade ne devrait jamais etre instancier
     */
    private Facade(){
        throw new IllegalStateException("ne devrait pas etre instancier");
    }

    public static void updateEpigraphe ( Annotation that, Integer id ) {
    try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
         EntityManager em = emf.createEntityManager();
         EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(em)) {
        that.setEpigraphe(epigrapheDAO.getEpigrapheNoCommit(id));
        that.getEpigraphe().addAnnotation(that);
        em.getTransaction().begin();
        em.merge(that.getEpigraphe());
        em.getTransaction().commit();
    } catch (Exception e) {
        throw new IllegalArgumentException("L'épigraphe" + id + "n'existe pas");
    }
}

}
