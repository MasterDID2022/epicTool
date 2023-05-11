package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verifcation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class FormulaireController implements Initializable{
    public AnchorPane anchorid;
    public AnchorPane alert;
    @FXML
    private TextField nomid ;
    @FXML
    private TextField prenomid ;
    @FXML
    private TextField emailid ;
    @FXML
    private TextField afiliationid ;
    @FXML
    private TextArea commentaireid ;
    @FXML
    private AlertController alertController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        alert.setDisable(true);
        alertController.hideAlertPane();
        Platform.runLater(() -> anchorid.requestFocus());
        anchorid.widthProperty().addListener(
                (obs, oldValue, newValue) -> alertController.setScreenWidth(newValue.doubleValue()));
        commentaireid.setStyle(
                "    -fx-font-size: 13px;" +
                        "    -fx-border-radius: 0 0 0 0;" +
                        "    -fx-background-radius: 10 0 0 10;" +
                        "  -fx-prompt-text-fill: #C0C0C0;" +
                        "-fx-border-color: -fx-blue; ;" +
                        "    -fx-control-inner-background: -fx-blue;" +
                        "    -fx-text-fill: -fx-white;");

    }

    @FXML
    private void EnvFormBtnOnClick()  {
        String nom = nomid.getText();
        String prenom = prenomid.getText();
        String email = emailid.getText();
        String afiliation = afiliationid.getText();
        String commentaire = commentaireid.getText();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonForm = mapper.createObjectNode();
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || afiliation.isEmpty() ||commentaire.isEmpty()){
                alert.setDisable(false);
                alertController.showFillField();
        } else if (!Verifcation.isInputAvalideEmail(email)) {
            log.error("Err:email invalide "+email);
            alert.setDisable(false);
            alertController.showNotValidEmail();
        } else {
            jsonForm.put("idFormulaire",-1);
            jsonForm.put("nomFormulaire",nom);
            jsonForm.put("prenomFormulaire",prenom);
            jsonForm.put("emailFormulaire",email);
            jsonForm.put("affiliationFormulaire",afiliation);
            jsonForm.put("commentaireFormulaire",commentaire);
            Api.postFormulaire(jsonForm.toString());
            clearAllFieldAndSwitch();
        }

    }

    private void clearAllFieldAndSwitch(){
        nomid.setText("");
        prenomid.setText("");
        emailid.setText("");
        afiliationid.setText("");
        commentaireid.setText("");
        try {
            backToMenu();
        }
        catch (IOException e) {
            log.error("Err: changement de page");
            throw new RuntimeException(e);
        }
    }


    public void backToMenu() throws IOException {
        Stage primaryStage = (Stage) anchorid.getScene()
                .getWindow();
        SceneController.switchToMenu(primaryStage);
    }
}
