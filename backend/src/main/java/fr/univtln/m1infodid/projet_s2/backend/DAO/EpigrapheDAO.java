package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

public class EpigrapheDAO extends GenericDAO<Epigraphe, Integer> {

    private EpigrapheDAO ( EntityManager entityManager ) {
        super(Epigraphe.class, entityManager);
    }

    public static EpigrapheDAO create ( EntityManager entityManager ) {
        return new EpigrapheDAO(entityManager);
    }

    /**
     * Récupère une instance de l'entité Epigraphe à partir de son identifiant.
     *
     * @param id L'identifiant de l'épigraphe à rechercher.
     * @return L'épigraphe correspondant à l'identifiant spécifié, ou null s'il n'existe pas.
     *
     * @apiNote Cette méthode effectue une recherche de l'épigraphe en utilisant l'identifiant spécifié.
     * Si l'épigraphe est trouvée, elle est mise à jour au-delà d'un délai de deux jours.
     * Si la mise à jour est impossible, l'entité en base est retournée en l'état.
     */
    @Override
    public Epigraphe findById ( Integer id ) {
        Epigraphe epigraphe = getEntityManager().find(Epigraphe.class, id);
        if (epigraphe == null) return null;

        if (epigraphe.getFetchDate().isBefore(LocalDate.now().minusDays(2))) { // expiration du cache
            getEntityManager().getTransaction().begin();
            getEntityManager().detach(epigraphe); // Comme on va reconstruire l'objet, on le détache temporairement de JPA, qui pourrait les considérer égaux comme ils ont le même identifiant

            try {
                epigraphe = SI.CreateEpigraphie(id);
            } catch (Exception e) {
                getEntityManager().merge(epigraphe);
                getEntityManager().getTransaction().rollback();
                return epigraphe;
            }

            getEntityManager().merge(epigraphe); // Rattachement et mise à jour
            getEntityManager().getTransaction().commit();
        }

        return epigraphe;
    }

}