package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Api REST pour les epigraphe
 */
@Slf4j
@Path("epigraphe")
public class Api {
    public static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("epigraphs");

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
        EntityManager em = entityManagerFactory.createEntityManager();
        try( EpigrapheDAO dao = EpigrapheDAO.create(em);) {
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
