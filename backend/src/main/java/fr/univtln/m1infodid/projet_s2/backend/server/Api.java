package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.univtln.m1infodid.projet_s2.backend.DAO.FormulaireDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
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
@Path("epicTools")
public class Api {
	private Optional<Annotation> createAnnotation(String annotationJson) {
		Optional<Annotation> annotation = Optional.empty();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(annotationJson);
			String idAnnotation = rootNode.path("idAnnotation").asText();
			String idEpigraphe = rootNode.path("idEpigraphe").asText();
			annotation = Optional.of(Annotation.of(Integer.parseInt(idAnnotation), Integer.parseInt(idEpigraphe)));

			JsonNode annotations = rootNode.get("annotations");
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = annotations.fields();
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> entry = fieldsIterator.next();
				String pointIndex = entry.getKey();
				JsonNode pointData = entry.getValue();
				List<List<Double>> points = new ArrayList<>();

				if (pointData.isArray()) {
					if (pointData.size() == 4) {
						// Convert rectangle coordinates to points
						double x = pointData.get(0).asDouble();
						double y = pointData.get(1).asDouble();
						double width = pointData.get(2).asDouble();
						double height = pointData.get(3).asDouble();

						points.add(Arrays.asList(x, y));
						points.add(Arrays.asList(x + width, y));
						points.add(Arrays.asList(x + width, y + height));
						points.add(Arrays.asList(x, y + height));
					} else {
						// Process points as they are
						for (JsonNode jsonPoint : pointData) {
							double x = jsonPoint.get(0).asDouble();
							double y = jsonPoint.get(1).asDouble();
							points.add(Arrays.asList(x, y));
						}
					}
				}

				annotation.get().addPoints(points);
			}

			return annotation;
		} catch (JsonProcessingException e) {
			log.error("Erreur de traitement JSON : " + e.getMessage());
		}

		return annotation;
	}



	/**
	 * Methode pour ajouter une anotation
	 *
	 * @param annotationJson l'anotation au format Json
	 */
	@Path("epigraphe/annotation")
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
	@Path("epigraphe/{id}")
	public Response sendEpigraphe(@PathParam("id") String id) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode responseJson = objectMapper.createObjectNode();
		Epigraphe epigraphe = SI.CreateEpigraphie(Integer.parseInt(id));
		responseJson.put("id", epigraphe.getId());
		responseJson.put("date", String.valueOf(epigraphe.getDate()));
		responseJson.put("traduction", epigraphe.getTranslation());
		responseJson.put("nom", epigraphe.getName());
		responseJson.put("ImgURL", epigraphe.getImgUrl());

		ArrayNode texteArray = responseJson.putArray("texte");
		for (String line : epigraphe.getText())
			texteArray.add(line);

		String jsonStr = objectMapper.writeValueAsString(responseJson);
		return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
	}



	private Optional<Formulaire> createFormulaire(String formulaireJson) {
		Optional<Formulaire> formulaire = Optional.empty();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(formulaireJson);
			String idFormulaire = rootNode.path("idFormulaire").asText();
			String nomFormulaire = rootNode.path("nomFormulaire").asText();
			String prenomFormulaire = rootNode.path("prenomFormulaire").asText();
			String emailFormulaire = rootNode.path("emailFormulaire").asText();
			String affiliationFormulaire = rootNode.path("affiliationFormulaire").asText();
			String commentaireFormulaire = rootNode.path("commentaireFormulaire").asText();

			formulaire = Optional.of(
					Formulaire.of(Integer.parseInt(idFormulaire),nomFormulaire,prenomFormulaire,emailFormulaire,affiliationFormulaire,commentaireFormulaire));
			return formulaire;
		} catch (JsonProcessingException e) {
			log.error("Parsing error, le json ne semble pas valide");
		}
		return formulaire;
	}

	/**
	 * Methode pour ajouter un formulaire
	 *
	 * @param formulaireJson le formulaire au format Json
	 */
	@Path("formulaire")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveFormulaire(String formulaireJson) {
		Optional<Formulaire> formulaire = createFormulaire(formulaireJson);
		if (formulaire.isPresent()){
			FormulaireDAO.createFormulaire(formulaire.get());
			return Response.ok().build();
		}
		return Response.notModified().build();
	}

}
