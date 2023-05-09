package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
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
    static final String URL_EPICHERCHELL = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=";

    /**
     * Methode static pour recupere le fichier XML de l'épigraphe d'ID id
     * Retourne la chaine vide en cas d'échec
     *
     * @param id
     * @return
     */
    public static String getEpigrapheOf ( Integer id ) {
        Client client = ClientBuilder.newClient();
        String epigraph = client.target(URL_EPICHERCHELL + id.toString())
                .request(MediaType.APPLICATION_XML)
                .get(String.class);
        client.close();
        return SI.getFirstImgUrl(epigraph);
    }

    /**
     * Methode qui gere les requete REST post pour les epigraphie
     * WIP Methode jou buer pour infra
     * Prend l'ID et l'envoie a la DAO pour la persister
     *
     * @return String de message de retour
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt ( String epigraheJson ) {
        log.info(epigraheJson);
        try (EntityManager emf = Persistence.createEntityManagerFactory("epigraphs").createEntityManager();
             EpigrapheDAO dao = EpigrapheDAO.create(emf)) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(epigraheJson);
            JsonNode id = jsonNode.get("ID");
            if (id != null) {
                dao.persistID(id.asInt());
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Il ne s'agit pas d'un epigrahe," +
                                "l'ID")
                        .build();
            }

            return Response.ok().build();
        } catch (Exception e) {
            log.debug(e.toString());
            return Response.serverError().entity("ERR:").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getId ( @PathParam("id") String id ) {
        return Response.status(200).entity(getEpigrapheOf(Integer.valueOf(id))).build();
    }
}
