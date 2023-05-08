package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe logique de la vue du menu
 */
@Slf4j
public class MenuController implements Initializable {

    private static final String NUMBER_ONLY_REGEX = "\\d+"; //[0-9]+

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField inputBar;

    @FXML
    private Parent alert;
    @FXML
    private AlertController alertController;

    @Override
    public void initialize ( URL location, ResourceBundle resources ) {
        alert.setDisable(true);
        alertController.hideAlertPane();

        Platform.runLater(() -> anchorPane.requestFocus());
        inputBar.setText("");
        anchorPane.widthProperty().addListener(( obs, oldValue, newValue ) -> alertController.setScreenWidth(newValue.doubleValue()));
    }

    /**
     * Méthode appelé lorsque le bouton de numéro de fiche est cliqué.
     * Affiche une erreur si l'entrée de l'utilisateur n'est pas valide
     * Sinon le numéro de fiche est récupérer
     *
     * @throws IOException renvoi une erreur si il y a un soucis pour le chargement de la nouvelle scène
     */
    @FXML
    private void numFicheBtnOnClick () throws IOException {

        if (!isInputOnlyInteger(inputBar.getText())) {
            alert.setDisable(false);
            alertController.showNotValidId();
            return;
        }

        int validId = Integer.parseInt(inputBar.getText());
        log.info("VALID ID : " + validId);
        //appeler la méthode pour récupérer le fichier XML à partir de cet id

        //pour le test uniquement, changement de scène auto, à supprimer plus tard
        Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
        SceneController.switchToPageVisualisation(primaryStage);
    }

    /**
     * Vérifie si l'entrée est valide selon le regex, celle ci vérifie que l'entrée est bien un nombre
     *
     * @param input la chaîne de caractère à tester
     * @return le résultat du regex, boolean
     */
    private boolean isInputOnlyInteger ( String input ) {
        return input.matches(NUMBER_ONLY_REGEX);
    }
}
