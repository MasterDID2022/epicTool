package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.TranscriptionController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.manager.AnnotationsManager;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verification;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.TradController;

@Slf4j
/**
 * Classe logique de la vue visualisation d'épigraphe
 */
public class PageVisualisationController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView plaqueImg;
    @FXML private AnchorPane plaqueAnchor;
    @FXML private TextField inputBar;
    @FXML private Pane paneCanvas;

    @FXML
    private Parent traduction;
    @FXML
    private TradController traductionController;

    @FXML private Parent transcription;
    @FXML private TranscriptionController transcriptionController;

    @FXML private Parent alert;
    @FXML private AlertController alertController;

    private String idEpigraphie;

    private AnnotationsManager annotationsManager;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alertController.hideAlertPane();

        plaqueImg.fitWidthProperty().bind(plaqueAnchor.widthProperty());
        plaqueImg.fitHeightProperty().bind(plaqueAnchor.heightProperty());

        annotationsManager = AnnotationsManager.getInstance();
        annotationsManager.initialize(paneCanvas, plaqueImg);

        inputBar.setText("");
        inputBar.setOnAction( e -> numFicheBtnOnClick() );
        
        Platform.runLater(() -> anchorPane.requestFocus());

        anchorPane.widthProperty().addListener(( obs, oldValue, newValue ) -> alertController.setScreenWidth(newValue.doubleValue()));
    }

    public void setupVisualEpigraphe ( String id, String imgUrl, String tradTxt, List<String> plaqueTxt ) {
        //afficher l'id et d'autres infos de l'épigraphie sur la page
        //obtenir l'image et l'afficher
        //construire le clavier à partir du texte de la plaque
        //modifier le label pour afficher la traduction

        idEpigraphie = id;
        traductionController.displayTranslation(tradTxt);
        transcriptionController.setup(id, plaqueTxt);

        //obtiens l'image et l'affiche
        if (imgUrl.length() == 0) return; //prévoir une image par défaut ou erreur si jamais l'image est introuvable ?
        Image img = new Image(imgUrl);
        plaqueImg.setImage(img);
    }

    /**
     * Méthode appelé lorsque le bouton de numéro de fiche est cliqué.
     * Affiche une erreur si l'entrée de l'utilisateur n'est pas valide
     * Sinon le numéro de fiche est récupérer et l'appli actualise les infos d'épigraphie
     */
    @FXML
    private void numFicheBtnOnClick () {

        if (!Verification.isInputOnlyInteger(inputBar.getText())) {
            alertController.showNotValidId();
            return;
        }

        int validId = Integer.parseInt(inputBar.getText());
        log.info("VALID ID : " + validId);
        
        Facade.visualiseEpigraphie(validId); //visualisation d'epigraphie pour visiteur
        inputBar.setText("");
        anchorPane.requestFocus();
        alertController.hideAlertPane();
    }

    @FXML
    private void saveAnnotationsBtnOnClick() {
        //peut être une popup de êtes-vous sûr de vouloir sauvegarder blabla

        Facade.postAnnotations( idEpigraphie, annotationsManager.getAnnotationsRectMap() );
    }

    public AlertController getAlertController() { return alertController; }
}
