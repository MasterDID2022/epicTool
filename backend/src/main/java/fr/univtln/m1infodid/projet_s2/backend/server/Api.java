package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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
	public static String getEpigrapheOf(Integer id) {
		Client client = ClientBuilder.newClient();
		String epigraph = client.target(URL_EPICHERCHELL + id.toString())
				.request(MediaType.APPLICATION_XML)
				.get(String.class);
		client.close();
		return SI.getFirstImgUrl(epigraph);
	}

	private Optional<Annotation> createAnnotation(String annotationJson) {
		Optional<Annotation> annotation = Optional.empty();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(annotationJson);
			String idAnnotation = rootNode.path("idAnnotation").asText();
			String idEpigraphe = rootNode.path("idEpigraphe").asText();
			annotation = Optional.of(
					Annotation.of(Integer.parseInt(idAnnotation), Integer.parseInt(idEpigraphe)));
			JsonNode annotations = rootNode.get("annotations");
			for (JsonNode jsonPoints : annotations) {
				for (JsonNode coordonner : jsonPoints) {
					Integer y = (Integer.parseInt(String.valueOf(coordonner.toString().charAt(1))));
					Integer x = (Integer.parseInt(String.valueOf(coordonner.toString().charAt(3))));
					annotation.get().addPoints(x, y);
				}
			}
			return annotation;
		} catch (JsonProcessingException e) {
			log.error("Parsing error, le json ne semble pas valide");
		}
		return annotation;
	}

	/**
	 * Methode pour ajouter une anotation
	 *
	 * @param annotationJson l'anotation au format Json
	 */
	@Path("annotation")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveAnnotation(String annotationJson) {
		Optional<Annotation> annotation = createAnnotation(annotationJson);
		if (annotation.isPresent()){
			log.info(annotation.toString());// Pour DAO persister ici
			return Response.ok().build();
		}
		return Response.notModified().build();
	}

	/**
	 * Methode de reponse pour une demande du front sur un epigraphe d'ID
	 * 
	 * @param id d'un epigraphe
	 */
	@GET
	@Path("/{id}")
	public Response sendEpigraphe(@PathParam("id") String id) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode responseJson = objectMapper.createObjectNode();
		Epigraphe epigraphe = SI.CreateEpigraphie(
				SI.extractTextAndImageFromXml(id, URL_EPICHERCHELL + Integer.valueOf(id).toString()));
		responseJson.put("id", epigraphe.getId());
		responseJson.put("date", String.valueOf(epigraphe.getDate()));
		responseJson.put("texte", epigraphe.getText());
		responseJson.put("traduction", epigraphe.getTranslation());
		responseJson.put("nom", epigraphe.getName());
		responseJson.put("ImgURL", epigraphe.getImgUrl());
		String jsonStr = objectMapper.writeValueAsString(responseJson);
		return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
	}
}
