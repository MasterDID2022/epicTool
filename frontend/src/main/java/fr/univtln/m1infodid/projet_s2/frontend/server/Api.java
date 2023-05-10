package fr.univtln.m1infodid.projet_s2.frontend.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.PageVisualisationController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;

/**
 * Api REST pour les epigraphe
 */
@Slf4j
@Path("epigraphe")
public class Api {
    static final String URI_API_BACKEND = "http://127.0.0.1:8080/api/epigraphe/";

    /**
     * Methode static pour recupere le fichier XML de l'épigraphe d'ID id
     * <p>
     * Retourne la chaine vide en cas d'échec
     *
     * @param id
     * @return
     */
    public static String sendRequestOf(Integer id) {
        String epigraphJson = "";
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(URI_API_BACKEND + id.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() != 200) {
                throw new RuntimeException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
            }
            epigraphJson = response.readEntity(String.class);
        } catch (Exception e) {
            log.error("ERR: probleme avec la conection au backend:");
            log.debug(e.toString());
        }
        log.info("-->" + epigraphJson);
        return epigraphJson;
    }
    /**
     * Méthode qui gère les requêtes REST POST pour les épigraphes.
     * Prend en entrée un objet JSON représentant un épigraphe, extrait les informations nécessaires, et les envoie à une méthode de la classe PageVisualisationController pour affichage.
     * En cas de succès, renvoie une réponse HTTP.
     * En cas d'échec (attributs manquants, erreur lors de la lecture de la chaîne JSON), renvoie une réponse HTTP avec un code d'erreur et un message explicatif.
     *
     * @param epigraphJson un objet JSON représentant un épigraphe, contenant les attributs suivants : "url", "date", "texte", "traduction", et "nom".
     * @return une réponse HTTP avec un code de statut et un message explicatif.
     */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt(String epigraphJson) {
        log.info(epigraphJson);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(epigraphJson);
            JsonNode url = jsonNode.get("url");
            JsonNode date = jsonNode.get("date");
            JsonNode texte = jsonNode.get("texte");
            JsonNode traduction = jsonNode.get("traduction");
            JsonNode nom = jsonNode.get("nom");
            if (url != null && date != null && texte != null && traduction != null && nom != null) {
                PageVisualisationController visualisation = new PageVisualisationController();
                visualisation.setupVisualEpigraphe(nom.asText(), url.asText(), Date.valueOf(date.asText()), texte.asText(), traduction.asText());
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Err: des attributs manquants")
                        .build();
            }

            return Response.ok().build();
        } catch (Exception e) {
            log.debug(e.toString());
            return Response.serverError().entity("ERR:").build();
        }
    }
    /**
     * Methode qui gere les requete REST post pour les annotations
     * Prend les annotations et les renvoie vers le back
     *
     * @return String de message de retour
     */
    public static String postAnnotations(String annotationsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        String annotationJson = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(annotationsJson);
            String idAnnotation = jsonNode.get("idAnnotation").asText();
            try (Client client = ClientBuilder.newClient()) {
                Response response = client.target(URI_API_BACKEND + "/annotation/" + idAnnotation)
                        .request(MediaType.APPLICATION_JSON)
                        .get();
                if (response.getStatus() != 200) {
                    throw new RuntimeException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
                }
                annotationJson = response.readEntity(String.class);
            } catch (Exception e) {
                log.warn("Erreur lors de l'envoi des données");
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la lecture de la chaîne JSON");
        }
        return annotationJson;
    }
}