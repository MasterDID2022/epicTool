package fr.univtln.m1infodid.projet_s2.backend.DAO;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

/**
 * Classe abstraite pour un DAO générique offrant des fonctionnalités de base pour les opérations CRUD (Create, Read, Update, Delete)
 * sur une entité spécifiée.
 *
 * @param <T> Le type de l'entité associée au DAO.
 * @param <PK> Le type de la clé primaire de l'entité associée.
 *
 * @apiNote Cette classe fournit des méthodes pour persister, rechercher, mettre à jour et supprimer des entités
 * en utilisant le gestionnaire d'entités (EntityManager). La classe générique doit être étendue en spécifiant
 * le type de l'entité associée et le type de sa clé primaire.
 */
public abstract class GenericDAO<T, PK extends Serializable> {

    private final Class<T> entityClass;
    private final EntityManager entityManager;

    /**
     * Constructeur de la classe GenericDAO.
     *
     * @param entityClass   Le type de l'entité associée au DAO.
     * @param entityManager Le gestionnaire d'entités utilisé pour les opérations de persistance.
     */
    protected GenericDAO ( Class<T> entityClass, EntityManager entityManager ) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    /**
     * Récupère le gestionnaire d'entités associé au DAO.
     *
     * @return Le gestionnaire d'entités utilisé pour les opérations de persistance.
     */
    public EntityManager getEntityManager () {
        return entityManager;
    }

    /**
     * Persiste une entité dans la base de données.
     *
     * @param entity L'entité a persister.
     * @return L'entité persistée.
     * @throws IllegalStateException Si une exception survient lors de la gestion de la transaction.
     */
    public T persist ( T entity ) throws IllegalStateException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(entity);
        transaction.commit();
        return entity;
    }

    /**
     * Recherche une entité dans la base de données en utilisant son identifiant.
     *
     * @param id L'identifiant de l'entité à rechercher.
     * @return L'entité correspondant à l'identifiant spécifié, ou null si elle n'existe pas.
     */
    public T findById ( PK id ) {
        return entityManager.find(entityClass, id);
    }

    /**
     * Récupère toutes les entités de ce type dans la base de données.
     *
     * @return Une liste contenant toutes les entités de ce type.
     */
    public List<T> findAll () {
        String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<T> typedQuery = entityManager.createQuery(query, entityClass);
        return typedQuery.getResultList();
    }

    /**
     * Met à jour une entité dans la base de données.
     *
     * @param entity L'entité à mettre à jour.
     * @return L'entité mise à jour.
     * @throws IllegalStateException Si une exception survient lors de la gestion de la transaction.
     */
    public T update ( T entity ) throws IllegalStateException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        T updatedEntity  = entityManager.merge(entity);
        transaction.commit();
        return updatedEntity;
    }

    /**
     * Supprime une entité de la base de données.
     *
     * @param entity L'entité à supprimer.
     *
     * @throws IllegalStateException Si une exception survient lors de la gestion de la transaction.
     */
    public void remove(T entity) throws IllegalStateException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        transaction.commit();
    }

    /**
     * Supprime une entité de la base de données en utilisant son identifiant.
     *
     * @param id L'identifiant de l'entité à supprimer.
     *
     * @throws IllegalStateException Si une exception survient lors de la gestion de la transaction.
     */
    public void removeById(PK id) throws IllegalStateException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        T entity = entityManager.find(entityClass, id);
        entityManager.remove(entity);
        transaction.commit();
    }
}

