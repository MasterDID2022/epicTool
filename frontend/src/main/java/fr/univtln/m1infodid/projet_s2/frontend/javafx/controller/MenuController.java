package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import
        fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verification;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
    private PasswordField inputPassword;
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
        inputBar.setOnAction(e -> numFicheBtnOnClick());

        anchorPane.widthProperty().addListener(
                (obs, oldValue, newValue) -> alertController.setScreenWidth(newValue.doubleValue()));
    }

    public void showSessionAlert(){
        alertController.showSessionExpired();
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
        inputBar.setText("");
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
        String password = inputPassword.getText();
        if (testMailCorrectness(mail)) {
            Facade.ROLE role = Facade.sendLoginAndPasseword(mail, password);
            Facade.setRole(role);
            Facade.setEmail(mail);
            switch (role) {
                case GESTIONNAIRE -> Facade.showScene(SceneType.HUB_GESTIONNAIRE);
                case ANNOTATEUR -> Facade.visualiseEpigraphie(42);
                default -> alertController.showNotValidAuth();
            }
        }
        inputMail.setText("");
        inputPassword.setText("");
    }


    /**
     * Test la validiter d'un mail avec la Classe verification, et indique sur l'UI l'échec
     *
     * @param email
     */
    public boolean testMailCorrectness(String email) {
        if (!Verification.isInputAvalideEmail(email)) {
            alert.setDisable(false);
            alertController.showNotValidEmail();
            inputMail.getStyleClass().add("wrongMail");
            return false;
        }

        if (inputMail.getStyleClass().contains("wrongMail")) {
            inputMail.getStyleClass().clear();
            inputMail.getStyleClass().addAll("text-field", "text-input");
        }
        return true;
    }

    public AlertController getAlertController() {
        return alertController;
    }

    @FXML
    private void consulterDemandesBtnOnClick() {
        Facade.showScene(SceneType.GESTION_ADHESION);
    }

    @FXML
    private void consulterAnnotationsBtnOnClick() {
        Facade.showScene(SceneType.ANNOTATIONS);
    }

}
