package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.DAO.AnnotationDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class Facade {


    /**
     * Contructeur prive de facade ne devrait jamais etre instancier
     */
    private Facade(){
        throw new IllegalStateException("ne devrait pas etre instancié");
    }
public static List<Annotation> getAnnotationofEpigraphe(int id ) {
    try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
         EntityManager em = emf.createEntityManager();
         AnnotationDAO annotationDAO = AnnotationDAO.create(em);) {
        log.info(annotationDAO.findOfEpigraphe(id).toString());
        return annotationDAO.findOfEpigraphe(id);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
    public static void updateEpigraphe ( Annotation that, Integer idEpigraphe, String emailUtilisateur ) {
        log.info("Epigraphe"+idEpigraphe);
    try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
         EntityManager em = emf.createEntityManager();
         EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(em);
         UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(em)) {
        that.setEpigraphe(epigrapheDAO.getEpigraphe(idEpigraphe));
        em.getTransaction().begin();
        Utilisateur u = utilisateurDAO.findByEmail(emailUtilisateur).orElseThrow(Exception::new);
        that.setUtilisateur(u);
        if (that.getEpigraphe().getAnnotations().contains(that)){
            that.getEpigraphe().getAnnotations().remove(that);
            em.flush();
            that.getEpigraphe().getAnnotations().add(that);
        }
        else that.getEpigraphe().addAnnotation(that);
        em.merge(that.getEpigraphe());
        em.getTransaction().commit();
    } catch (Exception e) {
        log.error(e.toString());
        //throw new IllegalArgumentException("L'épigraphe" + idEpigraphe + "n'existe pas");
    }
}

}
