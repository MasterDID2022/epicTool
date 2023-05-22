package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.backend.DAO.AnnotationDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Polygone;
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

                //dao.persist(testUser);
                dao.persist(testUser2);

                log.error("User at init ="+testUser.toString());
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
    /*
    public static void addTestAnnotations() {
     try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
    EntityManager em = emf.createEntityManager();
        AnnotationDAO dao = AnnotationDAO.create(em);)
         {
            Polygone polygone1 = Polygone.create(10.0, 10.0, 20.0, 30.0);
            Polygone polygone2 = Polygone.create(20.0, 20.0, 30.0, 40.0);
            Polygone polygone3 = Polygone.create(30.0, 30.0, 40.0, 50.0);

            List<Polygone> polygones1 = new ArrayList<>();
            polygones1.add(polygone1);
            polygones1.add(polygone2);

            List<Polygone> polygones2 = new ArrayList<>();
            polygones2.add(polygone3);

            Annotation annotation1 = Annotation.of(32);
            annotation1.setListCoordonesPoly(polygones1);
            Annotation annotation2 = Annotation.of(33);
            annotation2.setListCoordonesPoly(polygones2);
            dao.persist(annotation1);
            dao.persist(annotation2);
        } catch (Exception e) {
         throw new IllegalStateException(e);
     }
    }*/

    public static String  getAnnotations () {
        String jsonStr = "";
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
            EntityManager em = emf.createEntityManager();
            AnnotationDAO annotationDAO = AnnotationDAO.create(em)
        ){
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode responseJson = objectMapper.createObjectNode();
            responseJson.putPOJO("annotations", annotationDAO.findAll());
            jsonStr = objectMapper.writeValueAsString(responseJson);
        } catch (Exception e) {
            log.error("Erreur lors du formatage du JSON des annotations");
        }
        return jsonStr;
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

