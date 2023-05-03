package fr.univtln.m1infodid.projets2.javafx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * Classe logique du message d'erreur
 */
public class AlertController implements Initializable{

    @FXML private AnchorPane alertPane;
    @FXML private ImageView alertImg;
    @FXML private Label alertMsg;

    private double screenWidth;

    private SequentialTransition fadeOutSequence;
    private TranslateTransition translate;
    private static final float PIXEL_X_TRANSLATION = 100;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alertImg.setImage( new Image("/images/warning.png") );

        setupAnimations();
    }

    /**
     * S'occupe d'initialiser les animations de l'alerte (typiquement l'opacité qui va graduellement diminuer après une pause et la translation)
     */
    private void setupAnimations() {

        translate = new TranslateTransition(Duration.millis(250), alertPane);
        translate.setByX(-PIXEL_X_TRANSLATION);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(1300), alertPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOutSequence = new SequentialTransition( new PauseTransition(Duration.millis(4200)), fadeOut );

        fadeOutSequence.setOnFinished( e -> hideAlertPane() );
    }

    /**
     * Repositionne l'alerte dynamiquement dans le coin supérieur droit de l'écran
     */
    private void placeAlert() {
        alertPane.setLayoutX( screenWidth - alertPane.getLayoutBounds().getMaxX() + PIXEL_X_TRANSLATION );
        alertPane.setLayoutY( 25 );

        alertPane.setTranslateX(0);
        alertPane.setTranslateY(0);
    }

    /**
     * Défini la largeur de l'écran
     * @param value la nouvelle largeur de l'écran
     */
    public void setScreenWidth(double value) { 
        screenWidth = value; 
        placeAlert();
    }

    /**
     * Cacher l'alerte sur l'application
     */
    public void hideAlertPane() {
        alertPane.setOpacity(1);
        alertPane.setVisible(false);
    }

    /**
     * Montrer l'alerte sur l'application en changeant le message présent sur l'alerte
     * @param msg le Message de l'alerte
     */
    public void showAlert(String msg) {
        if ( !alertPane.getScene().getStylesheets().contains("/styles/alert.css") )
            alertPane.getScene().getStylesheets().add("/styles/alert.css");

        placeAlert();

        alertMsg.setText(msg);

        alertPane.setVisible(true);
        alertPane.setOpacity(1);
        alertPane.setMinHeight(30);
        alertPane.setMinWidth(100);
        alertPane.autosize();

        //animation pour faire disparaitre l'alerte après 5sec et la faire bouger à son apparition
        fadeOutSequence.playFromStart();
        translate.playFromStart();
    }

    /** Montre une alerte pour un numéro de fiche non valide */
    public void showNotValidId() { showAlert("Le n° de Fiche ÉpiCherchell n'est pas valide."); }
    
    /** Montre une alerte pour un mail ou un mot de passe non valide */
    public void showNotValidAuth() { showAlert("Le mail ou le mot de passe n'est pas valide."); }
    
    /** Montre une alerte pour la perte de connexion à internet */
    public void showNoInternet() { showAlert("Pas de connexion internet, réessayez ultérieurement."); }
    
    /** Montre une alerte pour un compte utilisateur non trouvé ou connexion échoué */
    public void showUserDontExist(String mail) { showAlert("Aucun compte enregistré pour l'adresse mail " + mail); }
}
