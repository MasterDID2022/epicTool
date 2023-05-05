package fr.univtln.m1infodid.projet_s2.backend.server;

import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Api REST pour les epigraphe
 */
@Slf4j
@Path("epigraphe")
public class Api {

    /**
     * Methode qui gere les requete REST post pour les epigraphie
     * WIP Methode jouer pour infra
     * Prend l'ID et l'envoie a la DAO pour la persister
     * @return String de message de retour
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt(String epigraheJson) {
        log.info(epigraheJson);
        EpigrapheDAO dao = new EpigrapheDAO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(epigraheJson);
            JsonNode id = jsonNode.get("ID");
            if (id != null) {
                dao.persistID(id.asInt());
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Il ne s'agit pas d'un epigrahe," +
                        "l'ID").build();
            }

            return Response.ok().build();
        } catch (Exception e) {
            log.debug(e.toString());
            return Response.serverError().entity("ERR:").build();
        }



    }
}
