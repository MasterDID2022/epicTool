package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotateur;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

/**
 * Les informations d'un annotateur
 */
@Slf4j
public class InfosAnnotateurController {
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


    /**
     * Refresh des champs
     */
    public void reset() {
        nomLabel.setText("");
        prenomLabel.setText("");
        mailLabel.setText("");
        commeLabel.setText("");
        affLabel.setText("");
    }

    /**
     *  initialize est une méthode pour initialiser les étiquettes avec des valeurs par défaut.
     */
    public void initialize() {
       reset();
       //reste plus qu'a récuperer le reste des infos de l'annotateur
       title.setText("Page informations de l'annotateur n° "+GestionAnnotateurController.id);
       mailLabel.setText(GestionAnnotateurController.emailSelectionné);
    }

    /**
     * Retourne à la page de gestion des annotateurs du gestionnaire
     */
    @FXML
    private void backToPage() {
        Facade.showScene(SceneType.GESTION_ANNOTATEUR);
    }

    /**
     * Met à jour les données de l'annotateur
     */
    @FXML
    private void updateInfos() {
        //Facade.showScene(SceneType.GESTION_ANNOTATEUR);
        log.info("A faire quand user et formulaire seront liés");
    }
}
