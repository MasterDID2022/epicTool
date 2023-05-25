package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.Facade.ROLE;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.ListeAnnotationController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.TradController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.TranscriptionController;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.manager.AnnotationsManager;
import fr.univtln.m1infodid.projet_s2.frontend.server.Verification;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Slf4j
public class PageVisualisationController implements Initializable {
    @FXML
    private Button buttonProfileIcon;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView plaqueImg;
    @FXML
    private AnchorPane plaqueAnchor;
    @FXML
    private TextField inputBar;
    @FXML
    private Pane paneCanvas;
    @FXML private Button saveBtn;

    @FXML
    private Parent traduction;
    @FXML
    private TradController traductionController;

    @FXML
    private Parent transcription;
    @FXML
    private TranscriptionController transcriptionController;

    @FXML
    private Parent alert;
    @FXML
    private AlertController alertController;

    @FXML private Parent listeAnnotation;
    @FXML private ListeAnnotationController listeAnnotationController;

    @FXML private Button newAnnotationBtn;

    private String idEpigraphie;

    private AnnotationsManager annotationsManager;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alertController.hideAlertPane();
        plaqueImg.fitWidthProperty().bind(plaqueAnchor.widthProperty());
        plaqueImg.fitHeightProperty().bind(plaqueAnchor.heightProperty());
        annotationsManager = AnnotationsManager.getInstance();
        annotationsManager.initialize(paneCanvas, plaqueImg, transcriptionController);
        inputBar.setText("");
        inputBar.setOnAction(e -> numFicheBtnOnClick());
        Platform.runLater(() -> anchorPane.requestFocus());
        anchorPane.widthProperty().addListener((obs, oldValue, newValue) -> alertController.setScreenWidth(newValue.doubleValue()));
        setupProfileButton();

    }

    private void setupProfileButton() {
        if (Objects.requireNonNull(Facade.getRole()) == ROLE.VISITEUR) {
            buttonProfileIcon.setId("buttonBack");
            buttonProfileIcon.setText("<");
        } else {
            buttonProfileIcon.setId("profileButton");
            buttonProfileIcon.setText(Facade.getEmail().substring(0, 1).toUpperCase());
        }
    }

    public void setupVisualEpigraphe(String id, String imgUrl, String tradTxt, List<String> plaqueTxt) {
        //afficher l'id et d'autres infos de l'épigraphie sur la page
        //obtenir l'image et l'afficher
        //construire le clavier à partir du texte de la plaque
        //modifier le label pour afficher la traduction
        idEpigraphie = id;
        traductionController.displayTranslation(tradTxt);
        transcriptionController.setup(id, plaqueTxt);
        setupProfileButton();
        setupSaveBtn();
        listeAnnotationController.initialize( Facade.renvoiAnnotation(Integer.parseInt(id)) ); //problème : à bouger dans la méthode setupVisualEpigraphe (pour prévoir l'actualiser d'une autre épigraphie) et Facade.renvoiAnnotation doit prendre idEpigraphie en paramètre

        //obtiens l'image et l'affiche
        if (imgUrl.length() == 0) return; //prévoir une image par défaut ou erreur si jamais l'image est introuvable ?
        Image img = new Image(imgUrl);
        plaqueImg.setImage(img);
    }

    public void setupSaveBtn() {
        boolean roleCheck = Facade.getRole() != ROLE.VISITEUR;

        String annMail = annotationsManager.getImportedEmail();
        if (annMail.isEmpty() || annMail.isBlank()) {
            saveBtn.setVisible( roleCheck );
            newAnnotationBtn.setVisible( roleCheck );
            return;
        }

        if (!roleCheck) {
            saveBtn.setVisible(false);
            newAnnotationBtn.setVisible(false);
            return;
        }

        boolean mailCheck = Facade.getEmail().equals(annMail);
        saveBtn.setVisible( mailCheck );
        newAnnotationBtn.setVisible( !mailCheck);
    }

    /**
     * Méthode appelée au clic sur  le bouton de numéro de fiche.
     * Affiche une erreur si l'entrée de l'utilisateur n'est pas valide
     * Sinon le numéro de fiche est récupéré et l'appli actualise les infos d'épigraphie
     */
    @FXML
    private void numFicheBtnOnClick() {

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
        if (Facade.getRole() == ROLE.VISITEUR) return;
        boolean annMailCheck = !annotationsManager.getImportedEmail().isBlank() || !annotationsManager.getImportedEmail().isEmpty();
        boolean notEqualMail = !Facade.getEmail().equals(annotationsManager.getImportedEmail());
        if (annMailCheck && notEqualMail) return;
        int codeHttp = Facade.postAnnotations(idEpigraphie, annotationsManager.getAnnotationsRectMap());
        switch (codeHttp) {
            case 401 -> {
                Facade.showScene(SceneType.HOME);
                Facade.showSessionExpired();
            }
            case 200 -> alertController.showAnnotationSuccess();
            default -> alertController.showAlert("Échec de l'envoi:" + codeHttp);
        }
    }

    @FXML
    private void newAnnotationBtnOnClick() {
        annotationsManager.reset();
        setupSaveBtn();
        transcriptionController.updateTranscriptionBtnsColor();
    }

    public AlertController getAlertController() {
        return alertController;
    }

    public void onClickProfileButton() {
        Facade.disconnectUser();
    }

    public void profileButtonExited() {
        if (Facade.getRole().equals(Facade.ROLE.ANNOTATEUR)) {
            buttonProfileIcon.setText(Facade.getEmail().substring(0, 1).toUpperCase());
        }
    }

    public void profileButtonEntered() {
        if (Facade.getRole().equals(Facade.ROLE.ANNOTATEUR)) {
            buttonProfileIcon.setText("X");
        }
    }
}


