package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.*;

import java.security.Key;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.backend.DAO.FormulaireDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import io.jsonwebtoken.Jwts;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;


/**
 * Api REST cote backend
 */
@Slf4j
@Path("epicTools")
public class Api {
    private Key key = SI.generateKey();


    /**
     * Prend un json et extrait le token de celui-ci, le verifie et retourne le
     * json sans le token si valide, sinon Optional empty
     * @param jsonMsg le json a tester
     * @return le json sans token ou rien
     */
    public Optional<String> cleanAndVerifyTokenOf(String jsonMsg){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node;
        try {
            node = objectMapper.readTree(jsonMsg);
            String token = node.get("token").asText();
            if (Boolean.FALSE.equals(verifyToken(token, Utilisateur.Role.ANNOTATEUR))){
                log.error("Token refuser");
                return Optional.empty();
            }
            ((ObjectNode) node).remove("token");
            return Optional.of(objectMapper.writeValueAsString(node));
        } catch (JsonProcessingException e) {
            log.error("Err: format json incorrect");
            return Optional.empty();
        }
    }

    private Boolean verifyToken(String token, Utilisateur.Role role){
        try {
            Jws<Claims> tokenClaim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            Claims claims = tokenClaim.getBody();
            String roleOfToken = claims.get("role", String.class);
            if (roleOfToken.equals(role.toString()) || roleOfToken.equals("gestionnaire")){
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

	private Optional<Annotation> createAnnotation(String annotationJson) throws NotAuthorizedException {
		try{
		ObjectMapper objectMapper = new ObjectMapper();
			return Optional.of(objectMapper.readValue(annotationJson,Annotation.class));
		} catch (JsonProcessingException e ) {
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
        Optional<String> cleanAnnotation = cleanAndVerifyTokenOf(annotationJson);
        if (cleanAnnotation.isEmpty()){
            throw new NotAuthorizedException("Token absent ou invalide");
        }
        try {
            Optional<Annotation> annotation = createAnnotation(cleanAnnotation.get());
            if (annotation.isPresent()){
                return Response.ok().build();
            }
        }catch (NotAuthorizedException e){
            return Response.notModified().entity("Err: authorisation invalide").build();
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
    public Response sendEpigraphe(@PathParam("id") String id) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseJson = objectMapper.createObjectNode();
        Epigraphe epigraphe = SI.createEpigraphie(Integer.parseInt(id));
        responseJson.put("id", epigraphe.getId());
        responseJson.put("date", String.valueOf(epigraphe.getDate()));
        responseJson.put("traduction", epigraphe.getTranslation());
        responseJson.put("nom", epigraphe.getName());
        responseJson.put("ImgURL", epigraphe.getImgUrl());
        ArrayNode texteArray = responseJson.putArray("texte");
        for (String line : epigraphe.getText())
            texteArray.add(line);
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(responseJson);
        } catch (JsonProcessingException e) {
            log.error("Err: impossible de parser le json de l'Epigraphe id:"+id);
        }
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
                    Formulaire.of(Integer.parseInt(idFormulaire), nomFormulaire, prenomFormulaire, emailFormulaire, affiliationFormulaire, commentaireFormulaire));
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
        if (formulaire.isPresent()) {
            FormulaireDAO.createFormulaire(formulaire.get());
            return Response.ok().build();
        }
        return Response.notModified().build();
    }

    public String generateTokenOfRole(String role) {
        Claims claims = Jwts.claims();
        claims.put("role", role);
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(key,SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + 20000))
                .compact();
        log.info(token);
        return token;
    }


	/**
	 * Récupère la liste des utilisateurs et renvoie une réponse HTTP contenant les utilisateurs au format JSON.

	 * return: Une réponse HTTP contenant la liste des utilisateurs au format JSON.
	 */
	@GET
	@Path("utilisateurs")
	public Response sendUtilisateur(@HeaderParam("Authorization")String token) {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
		String utilisateursJson = SI.recupereUtilisateurs();
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode responseJson = objectMapper.createObjectNode();
		responseJson.put("utilisateurs", utilisateursJson);
		String jsonStr = null;
		try {
			jsonStr = objectMapper.writeValueAsString(responseJson);
		} catch (JsonProcessingException e) {
            log.error("Err: format json incorrect");
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
			try( UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)){
                utilisateurDAO.remove(id);
                return Response.ok().build();
            }
		} catch (Exception e) {
			log.error(e.toString());
			return Response.serverError().build();
		}
	}
    @DELETE
    @Path("formulaireD/{id}")
    public Response deleteFor(@PathParam("id") int id) {
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("FoPU");
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            try {
                Formulaire formulaire = entityManager.find(Formulaire.class, id);
                if (formulaire != null) {
                    SI.sendMail(true,formulaire);
                    FormulaireDAO.createFormulaire(formulaire);
                    FormulaireDAO.deleteFormulaire(id);
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (Exception e) {
                log.error(e.toString());
                return Response.serverError().build();
            }
        }
    }


    @Path("user/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receiveLogin(@Context HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length());
            String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] usernamePassword = decodedCredentials.split(":", 2);
            if (true) {
                String username = usernamePassword[0];
                String password = usernamePassword[1];
                log.info("Username : " + username + ", password : " + password);
                log.info("Authentification: " + Utilisateur.checkPassword(password, username).toString());
                if (Utilisateur.checkPassword(password, username)) {
                    String role = Utilisateur.getRoleOf(username).get();
                    String token = generateTokenOfRole(role);
                    return Response.ok().entity(role+":"+token).build();
                }
                return Response.status(Response.Status.UNAUTHORIZED).entity(username + ":" + password + " est un combo incorrect").build();
            }
        } // No Authorization header or invalid format
        return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
    }

    public void getNewKey(){
        this.key = SI.generateKey();
    }

    /**
     * Prend un combo password mot de passe et verifie si il est valide,
     * renvoi une reponse pour l'API
     * @param email de l'utilisateur
     * @param password de l'utlisateur
     * @return reponse json
     */
    private Response verifyUserOf(String email, String password){
        log.error("Username : " + email + ", password : " + password);
        log.error("Authentification: " + Utilisateur.checkPassword(password, email).toString());
        Boolean isPasswordValide = Utilisateur.checkPassword(password, email);
        if (Boolean.TRUE.equals(isPasswordValide)) {
            Optional<String> role = Utilisateur.getRoleOf(email);
            if (role.isPresent()){
                String token = generateTokenOfRole(role.get());
                return Response.ok().entity(role+":"+token).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(email + ":" + password + " est un combo incorrect").build();
    }

}