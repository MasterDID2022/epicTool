package fr.univtln.m1infodid.projet_s2.backend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.backend.DAO.AnnotationDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.FormulaireDAO;
import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import fr.univtln.m1infodid.projet_s2.backend.Facade;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;
import fr.univtln.m1infodid.projet_s2.backend.model.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.*;

/**
 * Api REST cote backend
 */
@Slf4j
@Path("epicTools")
public class Api {
    private static final long EXPIRATION_TIME = 60000;
    private static final String PERSISTENCE_UNIT = "EpiPU";
    private Key key = Facade.generateKey();

    static String mapAnnotations () {
        String jsonStr = "";
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
             EntityManager em = emf.createEntityManager();
             AnnotationDAO annotationDAO = AnnotationDAO.create(em)
        ) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode responseJson = objectMapper.createObjectNode();
            responseJson.putPOJO("annotations", annotationDAO.findAll());
            jsonStr = objectMapper.writeValueAsString(responseJson);
        } catch (Exception e) {
            log.error("Erreur lors du formatage du JSON des annotations");
        }
        return jsonStr;
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
            log.error("back"+annotationJson);
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
     * Prend un json et extrait le token de celui-ci, le verifie et retourne le
     *
     * @param jsonMsg le json à tester
     * @return le json reçu sans le token, si l'authentification est confirmée.
     */
    public Optional<String> cleanAndVerifyTokenOf ( String jsonMsg ) {
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

    @GET
    @Path("epigraphe/annotation/{id : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendAnnotationsOfEpigraph ( @PathParam("id") String id ) {
        List<Annotation> annotations = Facade.getAnnotationOfEpigraphe(Integer.parseInt(id));
        log.error("Annot" + annotations.toString());

        if (annotations.isEmpty()) {
            return Response.noContent().build();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Response.ok(mapper.writeValueAsString(annotations)).build();
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }

        }
    }

    @GET
    @Path("epigraphe/{id:\\d+}")
    public Response sendEpigraphe(@PathParam("id") String id) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseJson = objectMapper.createObjectNode();
        Epigraphe epigraphe = SI.createEpigraphie(Integer.parseInt(id));
        assert epigraphe != null;
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
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .compact();
        log.info(token);
        return token;
    }

    private Optional<Formulaire> createFormulaire(String formulaireJson) {
        Optional<Formulaire> formulaire = Optional.empty();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
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
     * Methode pour ajouter un utilisateur
     *
     * @param userJson le formulaire au format Json
     */
    @Path("userP")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receiveUser(@HeaderParam("Authorization") String token, String userJson ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Optional<Utilisateur> utilisateur = createUser(userJson);
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT); EntityManager entityManager = entityManagerFactory.createEntityManager(); UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)) {
            utilisateurDAO.persist(utilisateur.orElseThrow(IllegalStateException::new));
            Facade.sendMail(true, utilisateur.orElseThrow(IllegalArgumentException::new).getEmail()); // Envoie du mail de validation
            return Response.ok().build();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return Response.notModified().build();
    }

    /**
     * Récupère la liste des utilisateurs et renvoie une réponse HTTP contenant les utilisateurs au format JSON.
     * <p>
     * return: Une réponse HTTP contenant la liste des utilisateurs au format JSON.
     */
    @GET
    @Path("utilisateurs")
    public Response sendUtilisateur ( @HeaderParam("Authorization") String token ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String utilisateursJson = Facade.recupereUtilisateurs();
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
     * @return Une réponse HTTP contenant la liste des formulaires au format JSON.
     */
    @GET
    @Path("formulaires")
    public Response sendFormulaire(@HeaderParam("Authorization") String token ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String formJson = Facade.recupererFormulaires();
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
     * Methode pour envoyer un mail de non-validation du compte et supression du formulaire
     *
     * @param id du demandeur
     */
    @DELETE
    @Path("formD/{id:\\d+}")
    public Response deleteFormulaire(@HeaderParam("Authorization") String token, @PathParam("id") int id ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Formulaire formulaire = FormulaireDAO.findByIdFormulaire(id);
        if(formulaire != null){
            FormulaireDAO.deleteFormulaire(id);
            String email = formulaire.getEmail();
            Facade.sendMail(false, email);
            return Response.ok().build();
        }
        return Response.notModified().build();
    }

    @GET
    @Path("userInfos/{email}")
    public Response sendInfosUser ( @HeaderParam("Authorization") String token, @PathParam("email") String email ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
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

        String jsonStr;
        try {
            jsonStr = objectMapper.writeValueAsString(responseArray);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return Response.ok(jsonStr, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Récupère la liste des annotations et renvoie une réponse HTTP contenant les annotations au format JSON.
     *
     * @return Une réponse HTTP contenant la liste des annotations au format JSON.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("annotations")
    public Response sendAnnotations ( @HeaderParam("Authorization") String token ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String jsonStr = mapAnnotations();
        return Response.ok(jsonStr).build();
    }

    /**
     * Methode pour supprimer un utilisateur à partir de son id
     *
     * @param id de l'user
     */
    @DELETE
	@Path("userD/{id:\\d+}")
	public Response deleteUser(@HeaderParam("Authorization") String token, @PathParam("id") int id ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            try (UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)) {
                utilisateurDAO.remove(id);
                return Response.ok().build();
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Response.serverError().build();
		}
	}

    /**
     * Méthode pour supprimer une annotation à partir de son ID.
     *
     * @param infosJson les infos de l'annotation à supprimer (id Epigraphe, mail)
     * @return une réponse indiquant si la suppression a réussi ou non
     */
    @POST
    @Path("annotationD")
    public Response deleteAnnotation(@HeaderParam("Authorization") String token, String infosJson ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(infosJson);
            String idEpigraphe = rootNode.path("idEpigraphe").asText();
            String email = rootNode.path("email").asText();

            try (AnnotationDAO annotationDAO = AnnotationDAO.create(entityManager)) {
                annotationDAO.remove(annotationDAO.findByIdEpiAndMail(Integer.parseInt(idEpigraphe), email));
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
            String email = usernamePassword[0];
            String password = usernamePassword[1];
            Optional<String> optionalTokenAndRole = verifyUserOf(email, password);
            if (optionalTokenAndRole.isPresent()) {
                return Response.ok().entity(optionalTokenAndRole.get()).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).entity(email + ":" + password + " est un combo incorrect").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
    }


    public void getNewKey () {
        this.key = Facade.generateKey();
    }

    /**
     * Prend un combo password mot de passe et verifie s'il est valide,
     * renvoi une reponse pour l'API
     *
     * @param email    de l'utilisateur
     * @param password de l'utilisateur
     * @return réponse json
     */
    private Optional<String> verifyUserOf ( String email, String password ) {
        log.error("Username : " + email + ", password : " + password);
        log.error("Authentification: " + Utilisateur.checkPassword(password, email));
        Boolean isPasswordValide = Utilisateur.checkPassword(password, email);
        if (Boolean.TRUE.equals(isPasswordValide)) {
            Optional<String> role = Utilisateur.getRoleOf(email);
            if (role.isPresent()) {
                String token = generateTokenOfRole(role.get());
                return Optional.of(role.get() + ":" + token);
            }
        }
        return Optional.empty();
    }

    /**
     * Methode pour mettre à jour un utilisateur à partir de son id
     *
     * @param infosJson infos du user sous le format JSON
     */
    @PUT
    @Path("userU/{id:\\d+}")
    public Response updateUser ( @HeaderParam("Authorization") String token, @PathParam("id") int id, String infosJson ) {
        if (Boolean.FALSE.equals(this.verifyToken(token, Utilisateur.Role.GESTIONNAIRE))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            try (UtilisateurDAO utilisateurDAO = UtilisateurDAO.create(entityManager)) {

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
