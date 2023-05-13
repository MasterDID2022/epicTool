package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import jakarta.persistence.EntityManager;

public class FormulaireDAO extends GenericDAO<Formulaire,Integer> {
    private FormulaireDAO ( Class<Formulaire> entityClass, EntityManager entityManager ) {
        super(entityClass, entityManager);
    }

    public static FormulaireDAO create ( EntityManager entityManager ) {
        return new FormulaireDAO(Formulaire.class, entityManager);
    }
}
