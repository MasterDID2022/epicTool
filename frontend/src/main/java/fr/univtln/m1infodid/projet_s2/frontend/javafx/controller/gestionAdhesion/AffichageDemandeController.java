package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotateur.GestionAnnotateurController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;


/**
 * AffichageDemandeController est Le contrôleur responsable de l'affichage des demandes d'adhésion dans l'interface
 * utilisateur JavaFX.
 */
public class AffichageDemandeController {
    @FXML
    private Button retour;
    @FXML
    private Label nom;
    @FXML
    private Label prenom;
    @FXML
    private Label email;
    @FXML
    private Label commentaire;
    @FXML
    private Label affiliation;

    @FXML
    private Label nomTextField;
    @FXML
    private Label prenomTextField;
    @FXML
    private Label emailTextField;
    @FXML
    private Label commentaireTextField;
    @FXML
    private Label affiliationTextField;

    /**
    *remet les elements du formulaire a vide au momoent du chargement
     */
    public void reset() {
        nomTextField.setText("");
        prenomTextField.setText("");
        emailTextField.setText("");
        commentaireTextField.setText("");
        affiliationTextField.setText("");
    }

    /**
     *  initialize est une méthode pour initialiser les étiquettes avec des valeurs par défaut.
     */
    public void initialize() {
        nom.setText("Nom");
        prenom.setText("Prénom");
        email.setText("Email");
        commentaire.setText("Commentaire");
        affiliation.setText("Affiliation");
        reset();
        //reste plus qu'a récuperer le reste des infos de l'annotateur
        emailTextField.setText(GestionAnnotateurController.emailSelectionné);
    }
    /**
     * handleRetourButton est une méthode pour afficher la scène de gestion des adhésions en utilisant la classe Facade.
     */
    @FXML
    private void handleRetourButton() {
        Facade.showScene(SceneType.GESTION_ADHESION);
        }
}
