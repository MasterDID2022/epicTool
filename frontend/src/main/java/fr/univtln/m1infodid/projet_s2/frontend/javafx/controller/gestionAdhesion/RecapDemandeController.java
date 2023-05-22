package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * AffichageDemandeController est Le contrôleur responsable de l'affichage des demandes d'adhésion dans l'interface
 * utilisateur JavaFX.
 */
@Slf4j
public class RecapDemandeController {
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label mailLabel;
    @FXML
    private Label affLabel;
    @FXML
    private Label commeLabel;
    @FXML
    private Label title;
    private static String recap;


    public static void setInfos(String recap) {
        RecapDemandeController.recap = recap;
    }
    /**
    * refresh
     */
    public void reset() {
        nomLabel.setText("");
        prenomLabel.setText("");
        mailLabel.setText("");
        affLabel.setText("");
        commeLabel.setText("");
    }

    /**
     *  initialize est une méthode pour initialiser les étiquettes avec des valeurs par défaut.
     */
    public void initialize() {
        reset();
        title.setText("Demande de création de compte n°"+ GestionFormulaireController.getId());
        showRecap(recap);
    }

    private void showRecap(String user){
        // Convertir la chaîne JSON en objet Java
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(user);
            nomLabel.setText(jsonNode.get(1).asText());
            prenomLabel.setText(jsonNode.get(2).asText());
            mailLabel.setText(jsonNode.get(3).asText());
            affLabel.setText(jsonNode.get(4).asText());
            commeLabel.setText(jsonNode.get(6).asText());
        } catch (IOException e) {
            log.info("Error occurred while parsing JSON: " + e.getMessage());
        }
    }
    /**
     * retour vers la page de gestion des formulaires du gestionnaire
     */
    @FXML
    private void backToPage() {
        Facade.showScene(SceneType.GESTION_ADHESION);
        }
}
