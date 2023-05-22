package fr.univtln.m1infodid.projet_s2.frontend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Api REST cote frontend
 */
@Slf4j
public class Api {
	static final String URI_API_BACKEND = "http://127.0.0.1:8080/api/epicTools/";
    static final String HTTP_ERROR_MSG = "La requête a échoué : code d'erreur HTTP ";

    private Api(){
        throw new IllegalStateException("ne devrait pas etre instancier");
    }

    /**
     * Methode static pour recupere le fichier XML de l'épigraphe d'ID id
     * <p>
     * Retourne la chaine vide en cas d'échec
     *
     * @param id
     * @return
     */
    public static List<String> sendRequestOf ( Integer id ) {
        String epigraphJson = "";
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(URI_API_BACKEND + "epigraphe/" + id.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() != 200) {
                log.error(HTTP_ERROR_MSG + response.getStatus());
            }
            epigraphJson = response.readEntity(String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(epigraphJson);
            JsonNode url = jsonNode.get("ImgURL");
            JsonNode date = jsonNode.get("date");
            JsonNode texte = jsonNode.get("texte");
            JsonNode traduction = jsonNode.get("traduction");
            JsonNode nom = jsonNode.get("nom");

            List<String> contentList = new ArrayList<>();
            contentList.add(nom.asText());
            contentList.add(date.asText());
            contentList.add(traduction.asText());
            contentList.add(url.asText());

            String[] transcriptionArray = objectMapper.readValue(texte.toString(), String[].class);
            List<String> transcriptionList = Arrays.asList(transcriptionArray);
            contentList.addAll( transcriptionList );

            return contentList;
        } catch (Exception e) {
            log.error("ERR: probleme avec la conection au backend:");
            log.error(e.toString());
        }

        return List.of();
    }

    /**
     * Methode qui gere les requetes REST post pour les annotations
     * Prend les annotations et les renvoie vers le back
     *
     * @return True si erreur
     */
    public static int postAnnotations(String annotationsJson) {
        try (Client client = ClientBuilder.newClient()) {
            Entity<String> entity = Entity.entity(annotationsJson, MediaType.APPLICATION_JSON);
            Response response = client.target(URI_API_BACKEND +"epigraphe/" + "annotation")
                    .request(MediaType.APPLICATION_JSON)
                    .post(entity);
            if (response.getStatus() != 200) {
                if (response.getStatus() == 401){
                    sessionExpired();
                    return 401;
                }
                else {
                    log.error(HTTP_ERROR_MSG+ response.getStatus());
                    return response.getStatus();
                }
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de l'annotation");
            log.warn(e.toString());
        }
        return 200;
    }
    public static void sessionExpired(){
        Facade.setToken("");
        Facade.setRole(Facade.ROLE.VISITEUR);
        Facade.setEmail("");
    }

    /**
     * Methode qui gere les requetes REST post pour les formulaires
     * Prend les formulaires et les renvoie vers le back
     *
     * @return String de message de retour
     */
    public static String postFormulaire(String formulaireJson) {
        String formJson = "";
        try {
            try (Client client = ClientBuilder.newClient()) {
                Entity<String> entity = Entity.entity(formulaireJson, MediaType.APPLICATION_JSON);
                Response response = client.target(URI_API_BACKEND + "formulaire" )
                        .request(MediaType.APPLICATION_JSON)
                        .post(entity);
                if (response.getStatus() != 200) {
                    log.error(HTTP_ERROR_MSG+ response.getStatus());
                }
                formJson = response.readEntity(String.class);
            } catch (Exception e) {
                log.error(e.toString());
                log.warn("Erreur lors de l'envoi du formulaire");
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la lecture de la chaîne JSON");
        }
        return formJson;
    }


    public static Optional<String> postLogin (String encodedCredentials ) {
        try (Client client = ClientBuilder.newClient();
             Response response = client.target(URI_API_BACKEND + "user/login")
                     .request(MediaType.APPLICATION_JSON).header("Authorization", "Basic " + encodedCredentials)
                     .post(Entity.json(""))) {
            if (response.getStatus() != 200) {
                if (response.getStatus()== 401){
                    log.info("Combo mot de passe email invalide");
                    return Optional.empty();
                }
                throw new IllegalStateException(HTTP_ERROR_MSG+ response.getStatus());
            }
            return Optional.of(response.readEntity(String.class));
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi des données");
            log.error(e.toString());
            log.warn("Erreur lors de la lecture de la chaîne JSON");
        }
        return Optional.empty();
    }

    /**
     * Récupère ls liste des utilisateurs à partir de l'API du backend et la renvoie sous forme d'une unique chaîne de caractères.
     *
     * @return le contenu des utilisateurs en tant que chaîne de caractères
     */
    public static String getAllAnnotateur() {
        String contenu = "";
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(URI_API_BACKEND + "utilisateurs")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", Facade.getToken() )
                    .get();
            if (response.getStatus() != 200) {
                if (response.getStatus() == 401){
                    sessionExpired();
                    return "401";
                }
                log.error(HTTP_ERROR_MSG+ response.getStatus());
            }
            contenu = response.readEntity(String.class);
            response.close();
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération des utilisateurs", e);
        }
        return contenu;
    }

    /**
     * Convertit une chaîne de caractères JSON en une liste d'objets.
     *
     * @param jsonString la chaîne de caractères JSON à convertir
     * @return une liste d'objets obtenue à partir de la conversion du JSON
     */
    public static List<String> convertJsonToList(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> resultList = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            String utilisateursString = jsonNode.get("utilisateurs").asText();
            resultList = objectMapper.readValue(utilisateursString, ArrayList.class);
        } catch (JsonProcessingException e) {
            log.warn("Erreur lors de la désérialisation du JSON");
        }
        return resultList;
    }


    /**
     * Fonction qui permet l'envoie d'une requête HTTP DELETE au backend pour supprimer l'utilisateur
     */
    public static void deleteUserOf(int userId){
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(URI_API_BACKEND + "userD/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            if (response.getStatus() != 200) {
                throw new IllegalStateException(HTTP_ERROR_MSG+ response.getStatus());
            }
            log.info("Utilisateur n° "+userId+" supprimé avec succès !");

        } catch (Exception e) {
            log.error(e.toString());
            log.warn("Erreur lors de l'envoi des données");
        }
    }


    /**
     * methode tmp pour faciliter tache, initalise une liste de formulaire pour tester ..
     */
    public static  List<List<String>> tmpMethodeInit(){
        List<List<String>> listeDeFormulaire = new ArrayList<>();

        List<String> listeFormulaire1 = new ArrayList<>();
        listeFormulaire1.add(" rawiarayan@gmail.com");
        listeFormulaire1.add(" ra");
        listeFormulaire1.add(" ben");
        listeFormulaire1.add(" comm");
        listeFormulaire1.add(" aff");

        List<String> listeFormulaire2 = new ArrayList<>();
        listeFormulaire2.add(" meryam@gmail.com");
        listeFormulaire2.add(" myr");
        listeFormulaire2.add(" ben");
        listeFormulaire2.add(" okk");
        listeFormulaire2.add(" hihihi");

        listeDeFormulaire.add(listeFormulaire1);
        listeDeFormulaire.add(listeFormulaire2);
        return listeDeFormulaire;

    }
    /**
     *
     * methode tmp initialise les annotations d une epigraphie
     * a modifier plus tard lors de la recuperation du backend
     */
    public static  List<List<String>> annotationsMethodeInit(){
        List<List<String>> listeAnnotation = new ArrayList<>();
        String ann = "les annotations de l'epygraphie ";

        List<String> listeAnnotation1 = new ArrayList<>();
        listeAnnotation1.add(ann);
        listeAnnotation1.add("1");

        List<String> listeAnnotation2 = new ArrayList<>();
        listeAnnotation2.add(ann);
        listeAnnotation2.add("2");

        List<String> listeAnnotation3 = new ArrayList<>();
        listeAnnotation3.add(ann);
        listeAnnotation3.add("3");

        List<String> listeAnnotation4 = new ArrayList<>();
        listeAnnotation4.add(ann);
        listeAnnotation4.add("4");

        listeAnnotation.add(listeAnnotation1);
        listeAnnotation.add(listeAnnotation2);
        listeAnnotation.add(listeAnnotation3);
        listeAnnotation.add(listeAnnotation4);
        return listeAnnotation;
    }

    /**
     *
     * similaire a la methode precedente, juste elle initialise les annotations d une epigraphie particuliere
     */
    public static  List<List<List<String>>> annotationMethodeInit(){
        List<List<List<String>>> listeAnnotation = new ArrayList<>();

        List<List<String>> epigraphie1 = new ArrayList<>();
        String ann = "annotation numero  ";

        List<String> listeAnnotation1 = new ArrayList<>();
        listeAnnotation1.add(ann);
        listeAnnotation1.add("1");
        listeAnnotation1.add("ici l'annotation 1 de l'epigraphie 1");

        List<String> listeAnnotation2 = new ArrayList<>();
        listeAnnotation2.add(ann);
        listeAnnotation2.add("2");
        listeAnnotation2.add("ici l'annotation 2 de l'epigraphie 1");

        epigraphie1.add(listeAnnotation1);
        epigraphie1.add(listeAnnotation2);
        listeAnnotation.add(epigraphie1);

        List<List<String>> epigraphie2 = new ArrayList<>();

        List<String> listeAnnotation21 = new ArrayList<>();
        listeAnnotation21.add(ann);
        listeAnnotation21.add("1");
        listeAnnotation21.add("ici l'annotation 1 de l'epigraphie 2 ");

        List<String> listeAnnotation22 = new ArrayList<>();
        listeAnnotation22.add(ann);
        listeAnnotation22.add("2");
        listeAnnotation22.add("ici l'annotation 2 de l'epigraphie 2");

        epigraphie2.add(listeAnnotation21);
        epigraphie2.add(listeAnnotation22);
        listeAnnotation.add(epigraphie2);

        List<List<String>> epigraphie3 = new ArrayList<>();

        List<String> listeAnnotation31 = new ArrayList<>();
        listeAnnotation31.add(ann);
        listeAnnotation31.add("1");
        listeAnnotation31.add("ici l'annotation 1 de l'epigraphie 3 ");

        List<String> listeAnnotation32 = new ArrayList<>();
        listeAnnotation32.add(ann);
        listeAnnotation32.add("2");
        listeAnnotation32.add("ici l'annotation 2 de l'epigraphie 3");

        epigraphie3.add(listeAnnotation31);
        epigraphie3.add(listeAnnotation32);
        listeAnnotation.add(epigraphie3);

        List<List<String>> epigraphie4 = new ArrayList<>();

        List<String> listeAnnotation41 = new ArrayList<>();
        listeAnnotation41.add(ann);
        listeAnnotation41.add("1");
        listeAnnotation41.add("ici l'annotation 1 de l'epigraphie 4 ");

        List<String> listeAnnotation42 = new ArrayList<>();
        listeAnnotation42.add(ann);
        listeAnnotation42.add("2");
        listeAnnotation42.add("ici l'annotation 2 de l'epigraphie 4");

        epigraphie4.add(listeAnnotation41);
        epigraphie4.add(listeAnnotation42);
        listeAnnotation.add(epigraphie4);
        return listeAnnotation;
    }
}