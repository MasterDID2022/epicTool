package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verification;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import lombok.extern.slf4j.Slf4j;

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
        alertController.hideAlertPane();
        Platform.runLater(() -> anchorPane.requestFocus());

        inputBar.setText("");
        inputBar.setOnAction( e -> numFicheBtnOnClick() );

        anchorPane.widthProperty().addListener(
                (obs, oldValue, newValue) -> alertController.setScreenWidth(newValue.doubleValue()));
    }

    /**
     * Méthode appelé lorsque le bouton de numéro de fiche est cliqué.
     * Affiche une erreur si l'entrée de l'utilisateur n'est pas valide
     * Sinon le numéro de fiche est récupérer et l'appli change vers l'écran d'épigraphie
     */
    @FXML
    private void numFicheBtnOnClick() {
        if (!Verification.isInputOnlyInteger(inputBar.getText())) {
            alert.setDisable(false);
            alertController.showNotValidId();
            return;
        }

        int validId = Integer.parseInt(inputBar.getText());
        log.info("VALID ID : " + validId);
        Facade.visualiseEpigraphie(validId);
    }


    /**
     * Methode pour changer la page vers le formulaire,
     * lors du click du bouton rejoindre
     */
    public void joinButtonPressed() {
        Facade.showScene(SceneType.FORMULAIRE);
    }

    public void connexionButtonPressed() {
        String mail = inputMail.getText();
        // String password = inputPassword.getText();Pour l'authentification
        testMailCorrectness(mail);
        inputMail.setText("");
        inputPassword.setText("");

        //authentification
    }


    /**
     * Test la validiter d'un mail avec la Classe verification, et indique sur l'UI l'échec
     *
     * @param email
     */
    public void testMailCorrectness(String email) {
        if (!Verification.isInputAvalideEmail(email)) {
            alert.setDisable(false);
            alertController.showNotValidEmail();
            inputMail.getStyleClass().add("wrongMail");
            return;
            // inputMail.setStyle("-fx-control-inner-background: #2F3855; -fx-text-inner-color: #f4c4c4; -fx-prompt-text-fill: grey; -fx-text-box-border: #803C3C; -fx-background-radius: 10 10 0 0;");
        }

        if (inputMail.getStyleClass().contains("wrongMail")) {
            inputMail.getStyleClass().clear();
            inputMail.getStyleClass().addAll("text-field", "text-input");
        }
        // inputMail.setStyle("-fx-control-inner-background: #2F3855; -fx-text-inner-color: #f4c4c4; -fx-prompt-text-fill: grey; -fx-text-box-border: #2F3855; -fx-background-radius: 10 10 0 0;");
    }

    public AlertController getAlertController() { return alertController; }

    @FXML
    private void consulterDemandesBtnOnClick() {
        Facade.showScene(SceneType.GESTION_ADHESION);

    }

}
