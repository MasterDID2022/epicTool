package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;


@Slf4j

public class UtilisateurDAO implements AutoCloseable{
    private final EntityManager entityManager;

    /**
     * Constructeur prive de la DAO
     * @param entityManager
     */
    private UtilisateurDAO ( EntityManager entityManager ) {
        this.entityManager = entityManager;
    }

    /**
     * Static factory de la classe UtilisateurDAO, prend un entityManager et
     * renvoie une dao pour manipuler la BD
     *
     * @param entityManager
     * @return dao
     */
    public static UtilisateurDAO create ( EntityManager entityManager ) {
        return new UtilisateurDAO(entityManager);
    }

    /**
     * Retourne une liste de tous les utilisateurs en BD
     *
     * @return list des utilisateurs
     */
    public List<Utilisateur> findAll()
    {
        TypedQuery<Utilisateur> query = entityManager.createQuery("SELECT T FROM Utilisateur as T",Utilisateur.class);
        return query.getResultList();
    }


    /**
     * Prend un id d'utilisateur et retourne  l'entité en BD
     *
     * @return L'utilisateur d'id id
     */
    public Utilisateur findById ( int id ) {
        return entityManager.find(Utilisateur.class, id);
    }


    /**
     * Prend un email d'utilisateur et retourne sont instanciation en BD sous la forme
     * d'un Optional
     *
     * @return L'utilisateur avec comme email, email
     */
    public Optional<Utilisateur> findByEmail (String email ) {
        TypedQuery<Utilisateur> query = entityManager.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
        query.setParameter("email", email);
        if (query.getResultList().isEmpty()){
            return  Optional.empty();
        }
        return Optional.of(query.getResultList().get(0));
    }

    /**
     * Prend un utilisateur et le persiste en BD
     * @param utilisateur
     */
    public void persist(Utilisateur utilisateur){
        entityManager.getTransaction().begin();
        entityManager.persist(utilisateur);
        entityManager.getTransaction().commit();
    }

    /**
     * Supprime un utilisateur en utilisant l'ID
     *
     * @param id
     */
    public void remove (int  id) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(Utilisateur.class, id));
        entityManager.getTransaction().commit();
    }

    /**
     * Supprime un utilisateur en utilisant l'email
     *
     * @param email
     */
    public void remove(String email) {
        entityManager.getTransaction().begin();
        Optional<Utilisateur> utilisateur = findByEmail(email);
        if (utilisateur.isEmpty()){
            return;
        }
        entityManager.remove(entityManager.find(Utilisateur.class, utilisateur.get().getId()));
        entityManager.getTransaction().commit();
    }

    /**
     * Supprime un utilisateur de la BD
     *
     * @param utilisateur l'utilisateur a supprimer
     */
    public void remove(Utilisateur utilisateur) {
        entityManager.getTransaction().begin();
        entityManager.remove(utilisateur);
        entityManager.getTransaction().commit();
    }

    /**
     * Met a jour un utilisateur de la BD
     *
     * @param utilisateur l'utilisateur a mettre à jour
     */
    public void update(Utilisateur utilisateur) {
        entityManager.getTransaction().begin();
        entityManager.merge(utilisateur);
        entityManager.getTransaction().commit();

    }



    @Override
    public void close () throws Exception {
        entityManager.close();
    }
}
