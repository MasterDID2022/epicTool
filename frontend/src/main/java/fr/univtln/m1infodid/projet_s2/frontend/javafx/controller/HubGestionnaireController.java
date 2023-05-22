package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller pour la page Hub du gestionnaire
 */
@Slf4j
public class HubGestionnaireController implements Initializable {

    @FXML private HBox controlHBox;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //:first-child & :last-child non supporté par javafx en css...
        controlHBox.getChildren().get(0).getStyleClass().add("btnFirstChild");
        controlHBox.getChildren().get( controlHBox.getChildren().size()-1 ).getStyleClass().add("btnLastChild");
    }

    /**
     * Méthode du clic pour le bouton deconnexion
     */
    @FXML private void disconnectBtnOnClick() {
        Facade.disconnectUser();
    }

    /**
     * Méthode pour le bouton formulaire du hub
     * renvoi vers la page de gestion de formulaire
     */
    @FXML private void formBtnOnClick() {
        Facade.showScene(SceneType.GESTION_ADHESION);
    }

    /**
     * Méthode pour le bouton annotation du hub
     * renvoi vers la page de gestion d'annotation
     */
    @FXML private void annotationBtnOnClick() {
        //à changer pour rediriger vers la page de gestion d'annotation
        Facade.showScene(SceneType.ANNOTATIONS);
    }

    /**
     * Méthode pour le bouton annotateur du hub
     * renvoi vers la page de gestion d'annotateur
     */
    @FXML private void annotateurBtnOnClick() {
        Facade.showScene(SceneType.GESTION_ANNOTATEUR);
    }
}
