package fr.univtln.m1infodid.projet_s2.backend.server;

import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Class lanceur du serveur REST en contact avec la BD
 */
@Slf4j

public class Lanceur {
    /**
     * Methode pour tester Autentification, devrait etre supprimer apres l'issue
     * pour ajouter un user
     * Persiste un Utilisateur avec comme email 'test@test.fr' et mdp 'leNomDuChien'
     * Contrairement au test unitaire cette utilisateur n'est pas nettoyer apres le test
     */
    public static void addTestUser() {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU")) {
            EntityManager em = emf.createEntityManager();
            try (UtilisateurDAO dao = UtilisateurDAO.create(em)) {
                Utilisateur testUser = Utilisateur.of("test@test.fr", "1234");
                Utilisateur testUser2 = Utilisateur.of("test2@test.fr", "1234");
                dao.persist(testUser2);

                log.error("User at init =" + testUser);
                if (dao.findByEmail(testUser.getEmail()).isEmpty()) {
                    dao.persist(testUser);

                }
                Utilisateur testAdmin = Utilisateur.of("admin@admin.fr", "1234");
                testAdmin.setRole(Utilisateur.Role.GESTIONNAIRE);
                if (dao.findByEmail(testAdmin.getEmail()).isEmpty()) {
                    dao.persist(testAdmin);
                }
            } catch (Exception e) {
                log.info("Erreur avec la BD");
            }
        }
    }
    public static String  getAnnotations () {

        return Api.mapAnnotations();
    }

    public static final String BASE_URI = "http://0.0.0.0:8080/api/";

    /**
     * Lance le serveur HTTP pour communiquer avec l'API
     *
     * @param args
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
        EntityManager em = emf.createEntityManager();
        final ResourceConfig rc = new ResourceConfig().packages("fr.univtln.m1infodid.projet_s2.backend.server");
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        addTestUser();
        log.info("l'API rest est active <C-c> pour la fermer");
        em.close();
        emf.close();
    }
}

