package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verification;
import javafx.fxml.FXML;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class FormulaireController implements Initializable{
    @FXML private AnchorPane anchorid;

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

    @FXML private ImageView formImg;

    @FXML private Parent alert;
    @FXML
    private AlertController alertController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formImg.setImage(new Image("/images/icon.png"));

        alertController.hideAlertPane();
        Platform.runLater(() -> anchorid.requestFocus());
        anchorid.widthProperty().addListener(
                (obs, oldValue, newValue) -> alertController.setScreenWidth(newValue.doubleValue()));

    }

    @FXML
    private void EnvFormBtnOnClick()  {
        String nom = nomid.getText();
        String prenom = prenomid.getText();
        String email = emailid.getText();
        String afiliation = afiliationid.getText();
        String commentaire = commentaireid.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || afiliation.isEmpty() || commentaire.isEmpty()){
            alertController.showFillField();
            return;
        }
        
        if (!Verification.isInputAvalideEmail(email)) {
            log.error("Err:email invalide "+email);
            alertController.showNotValidEmail();
            return;
        }

        Facade.sendFormulaire(nom, prenom, email, afiliation, commentaire);
        clearAllFieldAndSwitch();

    }

    private void clearAllFieldAndSwitch(){
        nomid.setText("");
        prenomid.setText("");
        emailid.setText("");
        afiliationid.setText("");
        commentaireid.setText("");

        backToMenu();
    }


    public void backToMenu() {
        Facade.showScene(SceneType.HOME);
    }
}
