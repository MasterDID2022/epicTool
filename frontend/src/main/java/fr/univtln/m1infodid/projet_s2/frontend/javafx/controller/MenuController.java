package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verifcation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
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

    @FXML
    private TextField inputMail;
    @FXML
    private Label connecter;
    @FXML
    private TextField inputPassword;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField inputBar;
    @FXML
    private Parent alert;
    @FXML
    private AlertController alertController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        alert.setDisable(true);
        alertController.hideAlertPane();
        Platform.runLater(() -> anchorPane.requestFocus());
        anchorPane.widthProperty().addListener(
                (obs, oldValue, newValue) -> alertController.setScreenWidth(newValue.doubleValue()));
    }

    /**
     * Méthode appelé lorsque le bouton de numéro de fiche est cliqué.
     * Affiche une erreur si l'entrée de l'utilisateur n'est pas valide
     * Sinon le numéro de fiche est récupérer
     *
     * @throws IOException renvoi une erreur si il y a un soucis pour le chargement
     *                     de la nouvelle scène
     */
    @FXML
    private void numFicheBtnOnClick() throws IOException {
        if (!Verifcation.isInputOnlyInteger(inputBar.getText())) {
            alert.setDisable(false);
            alertController.showNotValidId();
            return;
        }

        int validId = Integer.parseInt(inputBar.getText());
        log.info("VALID ID : " + validId);
        // appeler la méthode pour récupérer le fichier XML à partir de cet id
        // pour le test uniquement, changement de scène auto, à supprimer plus tard
        Stage primaryStage = (Stage) anchorPane.getScene()
                .getWindow();
        SceneController.switchToPageVisualisation(primaryStage);
        Api.sendRequestOf(validId);
    }


    /**
     * Methode pour changer la page vers le formulaire,
     * lors du click du bouton rejoindre
     */
    public void joinButtonPressed() throws IOException {
        Stage primaryStage = (Stage) anchorPane.getScene()
                .getWindow();
        SceneController.switchToPageFormulaire(primaryStage);
    }

    public void connexionButtonPressed() {
        String mail = inputMail.getText();
        // String password = inputPassword.getText();Pour l'authentification
        testMailCorrectness(mail);
        inputMail.setText("");
        inputPassword.setText("");
    }


    /**
     * Test la validiter d'un mail avec la Classe verification, et indique sur l'UI l'échec
     *
     * @param email
     */
    public void testMailCorrectness(String email) {
        if (!Verifcation.isInputAvalideEmail(email)) {
            alert.setDisable(false);
            alertController.showNotValidEmail();
            inputMail.setStyle("-fx-control-inner-background: #2F3855; -fx-text-inner-color: #f4c4c4; -fx-prompt-text-fill: grey; -fx-text-box-border: #803C3C; -fx-background-radius: 10 10 0 0;");
        } else {
            inputMail.setStyle("-fx-control-inner-background: #2F3855; -fx-text-inner-color: #f4c4c4; -fx-prompt-text-fill: grey; -fx-text-box-border: #2F3855; -fx-background-radius: 10 10 0 0;");

        }
    }
}
