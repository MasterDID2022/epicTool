package fr.univtln.m1infodid.projet_s2.frontend.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Api REST cote frontend
 */
@Slf4j
public class Api {
	static final String URI_API_BACKEND = "http://127.0.0.1:8080/api/epicTools/";

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
                throw new RuntimeException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
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
     * @return String de message de retour
     */
    public static String postAnnotations(String annotationsJson) {
        String annotationJson = "";
        try {
            try (Client client = ClientBuilder.newClient()) {
                Entity<String> entity = Entity.entity(annotationsJson, MediaType.APPLICATION_JSON);
                Response response = client.target(URI_API_BACKEND +"epigraphe/" + "annotation")
                        .request(MediaType.APPLICATION_JSON)
                        .post(entity);
                if (response.getStatus() != 200) {
                    throw new RuntimeException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
                }
                annotationJson = response.readEntity(String.class);
            } catch (Exception e) {
                log.warn("Erreur lors de l'envoi des données");
                log.warn(e.toString());
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la lecture de la chaîne JSON");
        }
        return annotationJson;
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
                    throw new RuntimeException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
                }
                formJson = response.readEntity(String.class);
            } catch (Exception e) {
                log.error(e.toString());
                log.warn("Erreur lors de l'envoi des données");
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la lecture de la chaîne JSON");
        }
        return formJson;
    }


    public static String postLogin ( String encodedCredentials ) {
        String loginJson="";
        try (Client client = ClientBuilder.newClient();
             Response response = client.target(URI_API_BACKEND + "user/login")
                     .request(MediaType.APPLICATION_JSON).header("Authorization", "Basic " + encodedCredentials)
                     .post(Entity.json(""))) {

            if (response.getStatus() != 200) {
                throw new IllegalStateException("La requête a échoué : code d'erreur HTTP " + response.getStatus());
            }
            loginJson = response.readEntity(String.class);
            log.info(loginJson);
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi des données");
            log.error(e.toString());
            log.warn("Erreur lors de la lecture de la chaîne JSON");
        }
        return loginJson;
    }
    /**
     * Récupère le contenu des utilisateurs à partir de l'API backend et le renvoie sous forme de chaîne de caractères.
     *
     * @return le contenu des utilisateurs en tant que chaîne de caractères
     */
    public static String recupereContenuUtilisateurs() {
        String contenu = "";
        try (Client client = ClientBuilder.newClient()) {
            Response response = client.target(URI_API_BACKEND + "utilisateurs")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() != 200) {
                log.error("Erreur lors de la récupération des utilisateurs : code d'erreur HTTP " + response.getStatus());
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
            resultList = objectMapper.readValue(utilisateursString, new ArrayList<>().getClass());
        } catch (JsonProcessingException e) {
            log.warn("Erreur lors de la désérialisation du JSON");
        }
        return resultList;
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
    public static  List<List<String>> AnnotationsMethodeInit(){
        List<List<String>> listeAnnotation = new ArrayList<>();
        String ann = "les annotations de l'epygraphie ";

        List<String> listeAnnotation1 = new ArrayList<>();
        listeAnnotation1.add(ann);
        listeAnnotation1.add("42");

        List<String> listeAnnotation2 = new ArrayList<>();
        listeAnnotation2.add(ann);
        listeAnnotation2.add("50");

        listeAnnotation.add(listeAnnotation1);
        listeAnnotation.add(listeAnnotation2);
        return listeAnnotation;
    }

    /**
     *
     * similaire a la methode precedente, juste elle initialise les annotation d une epigraphie particuliere
     */
    public static  List<List<String>> AnnotationMethodeInit(){
        List<List<String>> listeAnnotation = new ArrayList<>();
        String ann = "annotation numero  ";

        List<String> listeAnnotation1 = new ArrayList<>();
        listeAnnotation1.add(ann);
        listeAnnotation1.add("1");
        listeAnnotation1.add("je vous decrit ici ...... ");

        List<String> listeAnnotation2 = new ArrayList<>();
        listeAnnotation2.add(ann);
        listeAnnotation2.add("2");
        listeAnnotation2.add("je vous decrit cette annotation ....");

        listeAnnotation.add(listeAnnotation1);
        listeAnnotation.add(listeAnnotation2);
        return listeAnnotation;
    }

}






