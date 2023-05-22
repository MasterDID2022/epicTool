package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FormulaireDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");

    /**
     * Contructeur prive de la DAO ne devrait jamais etre instancier
     */
    private FormulaireDAO () {
        throw new IllegalStateException("ne devrait pas etre instancier");
    }

    /**
     * créer un objet Formulaire dans BD en utilisant l'EntityManager
     *
     * @param formulaire l'objet Formulaire à persister
     * @throws RuntimeException si la transaction ne marche pas
     */


    public static void createFormulaire ( Formulaire formulaire ) {
        EntityManager em = emf.createEntityManager();
        try (em) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(formulaire);
            tx.commit();
        } finally {
            log.info("Persistance formulaire");
        }
    }

    /**
     * update objet Formulaire existant dans la BD en utilisant l'EntityManager
     *
     * @param formulaire l'objet Formulaire à mettre à jour
     */
    public static void updateFormulaire ( Formulaire formulaire ) {
        EntityManager em = emf.createEntityManager();
        try (em) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.merge(formulaire);
            tx.commit();
        } catch (Exception e) {
            //ignore
        }
    }

    /**
     * Supprimer un objet Formulaire de la BD en utilisant l'EntityManager.
     *
     * @param id l'identifiant de l'objet Formulaire à supprimer
     */
    public static void deleteFormulaire ( int id ) {
        EntityManager em = emf.createEntityManager();
        try (em) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            Formulaire formulaire = em.find(Formulaire.class, id);
            em.remove(formulaire);
            tx.commit();
        } catch (Exception e) {
            // ignored
        }
    }


    /**
    * chercher et retourner un objet Formulaire à partir de son identifiant dans la BD en utilisant l'EntityManager.
     * @param id l'identifiant de l'objet Formulaire à rechercher
    */
    public static Formulaire findByIdFormulaire(int id ) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Formulaire.class, id);
        }
    }

    public static Formulaire findByEmailFormulaire(String mail ) {
        try (EntityManager em = emf.createEntityManager()) {
            Query query = em.createQuery("SELECT f FROM Formulaire f where f.email= :email");
            query.setParameter("email", mail);
            return (Formulaire) query.getSingleResult();
        }
    }


    /**
     * chercher et retourner une liste des objets Formulaire dans la BD en utilisant l'EntityManager.
     *
     * @return une liste d'objets Formulaire, ou une liste vide si la base de données est vide
     */
    public static List<Formulaire> findAllFormulaire () {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Formulaire> query = em.createQuery("SELECT f FROM Formulaire f", Formulaire.class);
            return query.getResultList();
        }
    }

    public static List<Formulaire> findFormulaireNotValidated () {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Formulaire> query = em.createQuery("SELECT f FROM Formulaire f WHERE NOT EXISTS (SELECT 1 FROM Utilisateur u where u.email = f.email)", Formulaire.class);
            return query.getResultList();
        }
    }

}