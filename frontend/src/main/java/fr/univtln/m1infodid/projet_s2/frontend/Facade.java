package fr.univtln.m1infodid.projet_s2.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.FormulaireController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.HubGestionnaireController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.MenuController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.PageVisualisationController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.SceneController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.SceneController.SceneData;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion.AffichageDemandeController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion.GestionFormulaireController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotateur.InfosAnnotateurController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotateur.GestionAnnotateurController;
import static fr.univtln.m1infodid.projet_s2.frontend.server.Api.convertJsonToList;
import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.*;



@Slf4j
/**
 * Facade principale du Frontend pour faciliter la communication entre Api et UI
 */
public class Facade {
    public enum ROLE {
        VISITEUR,
        ANNOTATEUR,
        GESTIONNAIRE
    }

    private Facade() {
        throw new IllegalStateException("Ne doit pas être instancié");
    }

    private static Stage primaryStage;

    private static SceneData<MenuController> menuData; //scène et controller du menu
    private static SceneData<PageVisualisationController> visuEpiData; //scène et controller de la page visualisation
    private static SceneData<FormulaireController> formData;
    private static String token;
    private static String email;
    private static ROLE role = ROLE.VISITEUR;
    private static SceneData<GestionFormulaireController> formGest;
    private static SceneData<AffichageDemandeController> afficherDemande;
    private static SceneData<HubGestionnaireController> hubData;
    private static SceneData<GestionAnnotateurController> gestAnnotateur;
    private static SceneData<InfosAnnotateurController> infosAnnotateur;

    public static void initStage(Stage primaryStage) {
        if (Facade.primaryStage != null) return;
        Facade.primaryStage = primaryStage;
    }

    public static void setToken(String token) {
        Facade.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static boolean isMenuStageShown() {
        return primaryStage.getScene() == menuData.scene();
    }

    public static boolean isPageVisualisationShown() {
        return primaryStage.getScene() == visuEpiData.scene();
    }

    public static boolean isFormulaireShown() {
        return primaryStage.getScene() == formData.scene();
    }

    public static List<String> requestEpigraphieInfo(int epigraphieId) {
        return Api.sendRequestOf(epigraphieId);
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Facade.email = email;
    }

    public static int postAnnotations(String idEpigraphie, Map<String, Rectangle> rectAnnotationsMap) {
        if (rectAnnotationsMap == null) return 0;
        if (rectAnnotationsMap.isEmpty()) return 0;
        if (token.isEmpty()) {
            log.error("ERR: Token vide");
            return 401;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonForm = mapper.createObjectNode();
        jsonForm.put("token", token);
        jsonForm.put("idEpigraphe", idEpigraphie);
        ArrayNode annotations = jsonForm.putArray("listePoly");
        for (var entry : rectAnnotationsMap.entrySet()) {
            Rectangle r = entry.getValue();
            ArrayNode rectangle = mapper.createArrayNode();
            rectangle.add(r.getX());
            rectangle.add(r.getY());
            rectangle.add(r.getWidth());
            rectangle.add(r.getHeight());
            annotations.add(rectangle);
        }
        return Api.postAnnotations(jsonForm.toString());
    }


    public static void sendFormulaire(String nom, String prenom, String email, String affiliation, String commentaire) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonForm = mapper.createObjectNode();

        jsonForm.put("idFormulaire", -1);
        jsonForm.put("nomFormulaire", nom);
        jsonForm.put("prenomFormulaire", prenom);
        jsonForm.put("emailFormulaire", email);
        jsonForm.put("affiliationFormulaire", affiliation);
        jsonForm.put("commentaireFormulaire", commentaire);

        Api.postFormulaire(jsonForm.toString());
    }

    public static void visualiseGestionFormulaire() {
        GestionFormulaireController gestionFormulaireController = formGest.controller();
        gestionFormulaireController.reset();
        List<List<String>> listeDeFormulaire = Api.tmpMethodeInit();
        gestionFormulaireController.initialize(listeDeFormulaire);
    }

    /**
     * Change la scene et met une alert expliquant la raison
     */
    private static void sessionEnded(){
        Facade.showScene(SceneType.HOME);
        Facade.showSessionExpired();
    }

    public static void visualiseGestionAnnotateur (){
        GestionAnnotateurController gestionAnnotateurController = gestAnnotateur.controller();
        gestionAnnotateurController.reset();
        Optional<List<String>> listAnnotateurs = getListOfUser();
        if (listAnnotateurs.isEmpty()){
            sessionEnded();
            return;
        }
        gestionAnnotateurController.initialize(listAnnotateurs.get());
    }

    public static void resetInfosAnnotateur() {
        if (infosAnnotateur != null) {
            InfosAnnotateurController infosAnnotateurController = infosAnnotateur.controller();
            infosAnnotateurController.initialize();
        }
    }

    public static void sendIdUserToDelete (int idUser){
        Api.deleteUserOf(idUser);
    }
    public static void sendIdFormulaireToDelete (int idFormulaire){
        Api.deleteFormOf(idFormulaire);
    }



    public static ROLE sendLoginAndPasseword ( String email, String passeword ) {
        Optional<String> response = Api.postLogin(Base64.getEncoder().encodeToString((email + ":" + passeword).getBytes()));
        if (response.isPresent()) {
            String[] reponsJson = response.get().split(":", 2);
            String role = reponsJson[0];
            String receiveToken = reponsJson[1];
            Facade.setToken(receiveToken);
            switch (role) {
                case "ANNOTATEUR":
                    return ROLE.ANNOTATEUR;
                case "GESTIONNAIRE":
                    return ROLE.GESTIONNAIRE;
                default:
                    return ROLE.VISITEUR;
            }
        }
        return ROLE.VISITEUR;
    }

    public static ROLE getRole() {
        return role;
    }

    public static void setRole(ROLE role) {
        Facade.role = role;
    }

    public static void showScene(SceneType type) {
        try {
            switch (type) {
                case HOME:
                    switchToSceneInFunctionOf(getRole());
                    break;
                case MENU:
                    if (menuData == null) menuData = SceneController.switchToMenu(primaryStage);
                    else SceneController.switchToScene(primaryStage, menuData);
                    break;
                case VISU_EPIGRAPHE:
                    if (visuEpiData == null) visuEpiData = SceneController.switchToPageVisualisation(primaryStage);
                    else SceneController.switchToScene(primaryStage, visuEpiData);
                    break;
                case FORMULAIRE:
                    if (formData == null) formData = SceneController.switchToPageFormulaire(primaryStage);
                    else SceneController.switchToScene(primaryStage, formData);
                    break;
                case GESTION_ADHESION:
                    if (formGest == null) {
                        formGest = SceneController.switchToPageGestionFormulaire(primaryStage);
                        visualiseGestionFormulaire();
                    } else {
                        SceneController.switchToScene(primaryStage, formGest);
                    }
                    break;

                case AFFICHAGE_DEMANDE:
                    if (afficherDemande == null) {
                        afficherDemande = SceneController.switchToPageGestionFormulairUI2(primaryStage);
                    } else {
                        resetAffichageDemande();
                        SceneController.switchToScene(primaryStage, afficherDemande);
                    }
                    break;

                case HUB_GESTIONNAIRE:
                    if (hubData == null) hubData = SceneController.switchToHubGestionnaire(primaryStage);
                    else SceneController.switchToScene(primaryStage, hubData);
                    break;

                case GESTION_ANNOTATEUR:
                    if (gestAnnotateur == null) {
                        gestAnnotateur = SceneController.switchToPageGestionAnnotateur(primaryStage);
                        visualiseGestionAnnotateur();
                    } else {
                        resetAffichageDemande();
                        SceneController.switchToScene(primaryStage, gestAnnotateur);
                    }
                    break;
                case INFOS_ANNOTATEUR:
                    if (infosAnnotateur == null) {
                        infosAnnotateur = SceneController.switchToPageGestionAnnotateurUI2(primaryStage);
                    } else {
                        resetInfosAnnotateur();
                        SceneController.switchToScene(primaryStage, infosAnnotateur);
                    }
                    break;
            }
        } catch (IOException e) {
            log.error("Impossible de change scene");
        }
    }

    private static void switchToSceneInFunctionOf(ROLE role) {
        log.info(role.name());
        try {
            switch (role) {
                case VISITEUR:
                    if (menuData == null) menuData = SceneController.switchToMenu(primaryStage);
                    else SceneController.switchToScene(primaryStage, menuData);
                    break;
                case ANNOTATEUR:
                    if (visuEpiData == null) visuEpiData = SceneController.switchToPageVisualisation(primaryStage);
                    else SceneController.switchToScene(primaryStage, visuEpiData);
                    break;
                case GESTIONNAIRE:
                    if (hubData == null) hubData = SceneController.switchToHubGestionnaire(primaryStage);
                    else SceneController.switchToScene(primaryStage, hubData);
                    break;
            }
        } catch (IOException e) {
            log.error("Err: impossible de changer de page");
        }
    }


    public static void resetAffichageDemande() {
        if (afficherDemande != null) {
            AffichageDemandeController affichageDemandeController = afficherDemande.controller();
            affichageDemandeController.initialize();
        }
    }

    public static void visualiseEpigraphie(int epigraphieId) {
        List<String> epiInfo = requestEpigraphieInfo(epigraphieId);
        //rajouter try catch ? pour renvoyer des exceptions si jamais y a des soucis
        //peut être throw exception, pour l'affichage dans MenuController d'alerte

        if (epiInfo.isEmpty()) {
            showNoInternetAlert();
            return;
        }

        showScene(SceneType.VISU_EPIGRAPHE); //si aucun soucis de chargement, changement de scène vers la page de visualisation d'epigraphie

        List<String> transcriptionList = new ArrayList<>();
        for (int i = 4; i < epiInfo.size(); i++)
            transcriptionList.add(epiInfo.get(i));

        //afficher les infos d'épigraphie
        visuEpiData.controller().setupVisualEpigraphe(String.valueOf(epigraphieId), epiInfo.get(3), epiInfo.get(2), transcriptionList);
    }

    public static void showNoInternetAlert() {
        if (isMenuStageShown()) menuData.controller().getAlertController().showNoInternet();
        else if (isPageVisualisationShown()) visuEpiData.controller().getAlertController().showNoInternet();
    }
    /**
    methode qui affiche recupere les utilisateurs du back sou forme json et les convertit en liste

     */
    public static Optional<List<String>> getListOfUser(){
        String utilisateursString = Api.getAllAnnotateur();
        if (utilisateursString.equals("401")){
            return Optional.empty();
        }
        List<String> utilisateursList = convertJsonToList(utilisateursString);
        return Optional.of(utilisateursList);
    }


    public static void showSessionExpired() {
        if (isMenuStageShown()) menuData.controller().getAlertController().showSessionExpired();
        else if (isPageVisualisationShown()) visuEpiData.controller().getAlertController().showSessionExpired();
    }

    /**
     * Méthode pour afficher le hub du gestionnaire
     * à appeler après avoir décidé quelle interface
     * affiché selon le rôle de l'utilisateur connecté
     */
    public static void showHubGestionnaire() {
        showScene(SceneType.HUB_GESTIONNAIRE);
    }

    /**
     * Methode de reset des attribut de l'utilisateur courant
     */
    public static void disconnectUser() {
        token = "";
        role = ROLE.VISITEUR;
        showScene(SceneType.MENU);
    }

}
