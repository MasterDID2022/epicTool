package fr.univtln.m1infodid.projet_s2.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.FormulaireController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.MenuController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.PageVisualisationController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.SceneController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.SceneController.SceneData;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion.AffichageDemandeController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion.GestionFormulaireController;
import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Facade principale du Frontend pour faciliter la communication entre Api et UI
 */
public class Facade {

    private Facade () {
        throw new IllegalStateException("Ne doit pas être instancié");
    }

    private static Stage primaryStage;

    private static SceneData<MenuController> menuData; //scène et controller du menu
    private static SceneData<PageVisualisationController> visuEpiData; //scène et controller de la page visualisation
    private static SceneData<FormulaireController> formData;
    private static SceneData<GestionFormulaireController> formGest;
    private static SceneData<AffichageDemandeController> afficherDemande;

    public static void initStage(Stage primaryStage) {
        if (Facade.primaryStage != null) return;
        Facade.primaryStage = primaryStage;
    }

    public static boolean isMenuStageShown() { return primaryStage.getScene() == menuData.scene(); }
    public static boolean isPageVisualisationShown() { return primaryStage.getScene() == visuEpiData.scene(); }
    public static boolean isFormulaireShown() { return primaryStage.getScene() == formData.scene(); }

    public static List<String> requestEpigraphieInfo(int epigraphieId) {
       return Api.sendRequestOf(epigraphieId);
    }

    public static void postAnnotations(String idEpigraphie, Map<String, Rectangle> rectAnnotationsMap) {
        if (rectAnnotationsMap == null) return;
        if (rectAnnotationsMap.isEmpty()) return;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonForm = mapper.createObjectNode();

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

        Api.postAnnotations(jsonForm.toString());
        System.out.println(jsonForm);
    }


    public static void sendFormulaire(String nom, String prenom, String email, String affiliation, String commentaire) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonForm = mapper.createObjectNode();

        jsonForm.put("idFormulaire",-1);
        jsonForm.put("nomFormulaire",nom);
        jsonForm.put("prenomFormulaire",prenom);
        jsonForm.put("emailFormulaire",email);
        jsonForm.put("affiliationFormulaire",affiliation);
        jsonForm.put("commentaireFormulaire",commentaire);

        Api.postFormulaire(jsonForm.toString());
    }

    public static void visualiseGestionFormulaire (){
        GestionFormulaireController gestionFormulaireController = formGest.controller();
        gestionFormulaireController.reset();
        List<List<String>> listeDeFormulaire = Api.tmpMethodeInit();
        gestionFormulaireController.initialize(listeDeFormulaire);
    }

    public static void sendLoginAndPasseword ( String email, String passeword ) {

        Api.postLogin(Base64.getEncoder().encodeToString((email + ":" + passeword).getBytes()));
    }

    public static void showScene(SceneType type) {
        try {
            switch(type) {
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
            }
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
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
            transcriptionList.add( epiInfo.get(i) );

        //afficher les infos d'épigraphie
        visuEpiData.controller().setupVisualEpigraphe( String.valueOf(epigraphieId), epiInfo.get(3), epiInfo.get(2), transcriptionList );
    }

    public static void showNoInternetAlert() {
        if (isMenuStageShown()) menuData.controller().getAlertController().showNoInternet();
        else if (isPageVisualisationShown()) visuEpiData.controller().getAlertController().showNoInternet();
    }
}
