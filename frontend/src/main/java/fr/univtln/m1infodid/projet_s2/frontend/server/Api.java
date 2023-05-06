package fr.univtln.m1infodid.projet_s2.frontend.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.PageVisualisationController;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

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
    public static String sendRequestOf ( Integer id ) {
        String urlEpigraph = "";
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(URI_API_BACKEND + id.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() != 200) {
                throw new RuntimeException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
            }
            urlEpigraph = response.readEntity(String.class);

        } catch (Exception e) {
            log.error("ERR: probleme avec la conection au backend:");
            log.debug(e.toString());
        }
        log.info("-->" + urlEpigraph);
        return urlEpigraph;

    }

    /**
     * Methode qui gere les requete REST post pour les epigraphie
     * WIP Methode jouer pour infra
     * Prend l'ID et l'envoie a la DAO pour la persister
     *
     * @return String de message de retour
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt ( String epigraheJson ) {
        log.info(epigraheJson);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(epigraheJson);
            JsonNode url = jsonNode.get("URL");
            if (url != null) {
                PageVisualisationController visualisation = new PageVisualisationController();
                visualisation.setupVisualEpigraphe("-1", url.asText(), "", "");
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Err:pas d'url")
                        .build();
            }

            return Response.ok().build();
        } catch (Exception e) {
            log.debug(e.toString());
            return Response.serverError().entity("ERR:").build();
        }
    }
}
