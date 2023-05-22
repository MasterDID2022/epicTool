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

import java.util.*;


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
            return Response.notModified().entity("Error: authorisation invalide").build();
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
            String nomFormulaire = rootNode.path("nomFormulaire").asText();
            String prenomFormulaire = rootNode.path("prenomFormulaire").asText();
            String emailFormulaire = rootNode.path("emailFormulaire").asText();
            String mdpFormulaire = rootNode.path("mdpFormulaire").asText();
            String affiliationFormulaire = rootNode.path("affiliationFormulaire").asText();
            String commentaireFormulaire = rootNode.path("commentaireFormulaire").asText();
            formulaire = Optional.of(
                    Formulaire.of(nomFormulaire,prenomFormulaire,emailFormulaire,mdpFormulaire,affiliationFormulaire,commentaireFormulaire));
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
        try {
            Optional<Formulaire> formulaire = createFormulaire(formulaireJson);
            if (formulaire.isPresent()){
                FormulaireDAO.createFormulaire(formulaire.get());
                return Response.ok().build();
            }
        }catch (NotAuthorizedException e){
            return Response.notModified().entity("Erreur: authorisation invalide").build();
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
     * Methode pour ajouter un utilisateur
     *
     * @param userJson le formulaire au format Json
     */
    @Path("userP")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receiveUser(@HeaderParam("Authorization") String token, String userJson) {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            Optional<Utilisateur> utilisateur = createUser(userJson);
            if (utilisateur.isPresent()) {
                try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU")) {
                    try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
                        try (UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)) {
                            utilisateurDAO.persist(utilisateur.get());
                            SI.sendMail(true, utilisateur.get().getEmail()); // Envoie du mail de validation
                            return Response.ok().build();
                        }
                    }
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        } catch (Exception e) {
            return Response.notModified().entity("Err: authorisation invalide").build();
        }
        return Response.notModified().build();
    }




    private Optional<Utilisateur> createUser(String formulaireJson) {
        Optional<Utilisateur> utilisateur = Optional.empty();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(formulaireJson);
            String emailUser = rootNode.path("emailFormulaire").asText();
            String mdpUser = rootNode.path("mdpFormulaire").asText();

            utilisateur = Optional.of(Utilisateur.of(emailUser,mdpUser));
            return utilisateur;
        } catch (JsonProcessingException e) {
            log.error("Parsing erreur, le json ne semble pas valide");
        }
        return utilisateur;
    }


	/**
	 * Récupère la liste des utilisateurs et renvoie une réponse HTTP contenant les utilisateurs au format JSON.

	 * return: Une réponse HTTP contenant la liste des utilisateurs au format JSON.
	 */
	@GET
	@Path("utilisateurs")
	public Response sendUtilisateur(@HeaderParam("Authorization") String token) {
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
            log.error("Error: format json incorrect");
		}
		return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
	}


    /**
     * Récupère la liste des formulaires et renvoie une réponse HTTP contenant les formulaires au format json

     * return: Une réponse HTTP contenant la liste des formulaires au format JSON.
     */
    @GET
    @Path("formulaires")
    public Response sendFormulaire(@HeaderParam("Authorization") String token) {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String formJson = SI.recupererFormulaires();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseJson = objectMapper.createObjectNode();
        responseJson.put("formulaires", formJson);
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(responseJson);
        } catch (JsonProcessingException e) {
            log.error("Error: format json incorrect");
        }
        return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
    }


    /**
     * Methode pour envoyer un mail de non validation du compte et supression du formulaire
     *
     * @param id du demandeur
     */
    @DELETE
    @Path("formD/{id}")
    public Response deleteFormulaire(@HeaderParam("Authorization") String token, @PathParam("id") int id) {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Formulaire formulaire = FormulaireDAO.findByIdFormulaire(id);
        if(formulaire != null){
            FormulaireDAO.deleteFormulaire(id);
            String email = formulaire.getEmail();
            SI.sendMail(false, email);
            return Response.ok().build();
        }
        return Response.notModified().build();
    }



    @GET
    @Path("userInfos/{email}")
    public Response sendInfosUser(@HeaderParam("Authorization") String token, @PathParam("email") String email) throws Exception {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode responseArray = objectMapper.createArrayNode();
        List<String> champs = new ArrayList<>();

        Formulaire formulaire = FormulaireDAO.findByEmailFormulaire(email);
        champs.add(String.valueOf(formulaire.getId()));
        champs.add(formulaire.getNom());
        champs.add(formulaire.getPrenom());
        champs.add(formulaire.getEmail());
        champs.add(formulaire.getAffiliation());
        champs.add(formulaire.getMdp());
        champs.add(formulaire.getCommentaire());

        for (String champ : champs) {
            responseArray.add(champ);
        }

        String jsonStr = objectMapper.writeValueAsString(responseArray);
        return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
    }



    /**
	 * Methode pour supprimer un utilisateur à partir de son id
	 *
	 * @param id du user
	 */
	@DELETE
	@Path("userD/{id}")
	public Response deleteUser(@HeaderParam("Authorization") String token, @PathParam("id") int id) {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU");
			 EntityManager entityManager = entityManagerFactory.createEntityManager()) {
			try( UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)){
                Utilisateur user = utilisateurDAO.findById(id);
                utilisateurDAO.remove(id);

                Formulaire formulaire = FormulaireDAO.findByEmailFormulaire(user.getEmail());
                FormulaireDAO.deleteFormulaire(formulaire.getId());
                return Response.ok().build();
            }
		} catch (Exception e) {
			log.error(e.toString());
			return Response.serverError().build();
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
                    Optional<String> roleOptional = Utilisateur.getRoleOf(username);
                    if (roleOptional.isPresent()) {
                        String role = roleOptional.get();
                        String token = generateTokenOfRole(role);
                        return Response.ok().entity(role + ":" + token).build();
                    } else {
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Impossible d'obtenir le rôle de l'utilisateur").build();
                    }
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



    /**
     * Methode pour mettre à jour un utilisateur à partir de son id
     *
     * @param infosJson infos du user sous le format JSON
     */
    @PUT
    @Path("userU/{id}")
    public Response updateUser(@HeaderParam("Authorization") String token, @PathParam("id") int id, String infosJson) throws Exception {
        if (! this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EpiPU");
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            try(UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)){

            Optional<Formulaire> beforeUpdate = Optional.of(FormulaireDAO.findByIdFormulaire(id));
            Optional<Utilisateur> utilisateurOptional = utilisateurDAO.findByEmail(beforeUpdate.get().getEmail());

            if (utilisateurOptional.isPresent()) {
                Utilisateur utilisateur = utilisateurOptional.get();

                Optional<Formulaire> toUpdateOptional = infosUser(infosJson);

                if (toUpdateOptional.isPresent()) {
                    Formulaire beforeUpdateEntity = beforeUpdate.get();
                    Formulaire toUpdateEntity = toUpdateOptional.get();

                    beforeUpdateEntity.setNom(toUpdateEntity.getNom());
                    beforeUpdateEntity.setPrenom(toUpdateEntity.getPrenom());
                    beforeUpdateEntity.setEmail(toUpdateEntity.getEmail());
                    beforeUpdateEntity.setAffiliation(toUpdateEntity.getAffiliation());
                    FormulaireDAO.updateFormulaire(beforeUpdateEntity);

                    utilisateur.setEmail(toUpdateEntity.getEmail());
                    utilisateurDAO.update(utilisateur);

                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data provided.").build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } }catch (Exception e) {
            log.error(e.toString());
            return Response.serverError().build();
        }
    }


    private Optional<Formulaire> infosUser(String infosJson) {
        Optional<Formulaire> formulaire = Optional.empty();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(infosJson);
            String nom = rootNode.path("nom").asText();
            String prenom = rootNode.path("prenom").asText();
            String email = rootNode.path("email").asText();
            String affiliation = rootNode.path("affiliation").asText();
            formulaire = Optional.of(Formulaire.of(nom,prenom,email,"",affiliation,""));
            return formulaire;
        } catch (JsonProcessingException e) {
            log.error("Parsing err, le json ne semble pas valide");
        }
        return formulaire;
    }

}