package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.backend.DAO.FormulaireDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.Facade;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Optional;


/**
 * Api REST cote backend
 */
@Slf4j
@Path("epicTools")
public class Api {
	private Optional<Annotation> createAnnotation(String annotationJson)   {
		try{
		ObjectMapper objectMapper = new ObjectMapper();
			return Optional.of(objectMapper.readValue(annotationJson,Annotation.class));
		} catch (JsonProcessingException e ) {
			log.info(e.toString());
			return Optional.empty();
		}
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
		Epigraphe epigraphe = Facade.createEpigraphie(Integer.parseInt(id));
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
			String mdpFormulaire = rootNode.path("mdpFormulaire").asText();
			String affiliationFormulaire = rootNode.path("affiliationFormulaire").asText();
			String commentaireFormulaire = rootNode.path("commentaireFormulaire").asText();
			formulaire = Optional.of(
					Formulaire.of(Integer.parseInt(idFormulaire),nomFormulaire,prenomFormulaire,emailFormulaire,mdpFormulaire,affiliationFormulaire,commentaireFormulaire));
			return formulaire;
		} catch (JsonProcessingException e) {
			log.error("Parsing error, le json ne semble pas valide");
		}
		return formulaire;
	}

	private Optional<Utilisateur> createUser(String userJson) {
		Optional<Utilisateur> utilisateur = Optional.empty();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(userJson);
			String emailUser = rootNode.path("emailUser").asText();
			String mdpUser = rootNode.path("mdpUser").asText();

			utilisateur = Optional.of(
					Utilisateur.of(emailUser,mdpUser));
			return utilisateur;
		} catch (JsonProcessingException e) {
			log.error("Parsing error, le json ne semble pas valide");
		}
		return utilisateur;
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

	@Path("user/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveLogin ( @Context HttpHeaders headers ) {
		String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
			String base64Credentials = authorizationHeader.substring("Basic ".length());
			String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials));
			String[] usernamePassword = decodedCredentials.split(":", 2);

			if (usernamePassword.length == 2 && !usernamePassword[1].isEmpty() && Verification.isInputAvalideEmail(usernamePassword[0])) {
				String username = usernamePassword[0];
				String password = usernamePassword[1];
				log.info("Username : " + username + ", password : " + password);
				log.info("Authentification: "+Utilisateur.checkPassword(password,username).toString());
				return Response.ok().entity("Bienvenue " + username + " !").build();
			}
		} // No Authorization header or invalid format
		return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
	}


	/**
	 * Récupère la liste des utilisateurs et renvoie une réponse HTTP contenant les utilisateurs au format JSON.

	 * return: Une réponse HTTP contenant la liste des utilisateurs au format JSON.
	 */
	@GET
	@Path("utilisateurs")
	public static Response sendUtilisateur() {
		String utilisateursJson = SI.recupereUtilisateurs();
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode responseJson = objectMapper.createObjectNode();
		responseJson.put("utilisateurs", utilisateursJson);

		String jsonStr = null;
		try {
			jsonStr = objectMapper.writeValueAsString(responseJson);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
	}


	/**
	 * Methode pour supprimer un utilisateur à partir de son id
	 *
	 * @param id du user
	 */
	@DELETE
	@Path("userD/{id}")
	public Response deleteUser(@PathParam("id") int id) {
		try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU");
			 EntityManager entityManager = entityManagerFactory.createEntityManager()) {

			UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager);
			utilisateurDAO.remove(id);

			return Response.ok().build();
		} catch (Exception e) {
			log.error(e.toString());
			return Response.serverError().build();
		}
	}
}