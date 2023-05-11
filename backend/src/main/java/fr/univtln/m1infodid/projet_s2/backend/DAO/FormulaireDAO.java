package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class
FormulaireDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");


    /**
     * créer un objet Formulaire dans BD en utilisant l'EntityManager
     * @param formulaire l'objet Formulaire à persister
     * @throws RuntimeException si la transaction ne marche pas
     */
    public static void createFormulaire(Formulaire formulaire) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(formulaire);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            log.info("Persistance formulaire");
            em.close();
        }
    }

    /**
    * update objet Formulaire existant dans la BD en utilisant l'EntityManager
    * @param formulaire l'objet Formulaire à mettre à jour
    */
    public static void updateFormulaire(Formulaire formulaire) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(formulaire);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Supprimer un objet Formulaire de la BD en utilisant l'EntityManager.
     * @param id l'identifiant de l'objet Formulaire à supprimer
     * */
    public static void deleteFormulaire(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Formulaire formulaire = em.find(Formulaire.class, id);
            em.remove(formulaire);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    /**
    * chercher et retourner un objet Formulaire à partir de son identifiant dans la BD en utilisant l'EntityManager.
     * @param id l'identifiant de l'objet Formulaire à rechercher
    */
    public static Formulaire findByIdFormulaire(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Formulaire.class, id);
        } finally {
            em.close();
        }
    }



    /**
     * chercher et retourner une liste des objets Formulaire dans la BD en utilisant l'EntityManager.
     *
     * @return une liste d'objets Formulaire, ou une liste vide si la base de données est vide
     */
    public static List<Formulaire> findAllFormulaire() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT f FROM Formulaire f");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}