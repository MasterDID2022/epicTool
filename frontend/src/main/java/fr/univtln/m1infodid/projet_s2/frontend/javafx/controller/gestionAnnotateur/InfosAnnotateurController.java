package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotateur;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Les informations d'un annotateur
 */
@Slf4j
public class InfosAnnotateurController {
    @FXML
    private TextField nomTxt;
    @FXML
    private TextField prenomTxt;
    @FXML
    private TextField mailTxt;
    @FXML
    private TextField affTxt;
    @FXML
    private Label title;
    private String id;
    private static String infos;


    public static void setInfos(String infos) {
        InfosAnnotateurController.infos = infos;
    }



    /**
     * Refresh des champs
     */
    public void reset() {
        nomTxt.setText("");
        prenomTxt.setText("");
        mailTxt.setText("");
        affTxt.setText("");
    }

    /**
     *  initialize est une méthode pour initialiser les étiquettes avec des valeurs par défaut.
     */
    public void initialize() {
        reset();
        title.setText("Page informations de l'annotateur n° "+GestionAnnotateurController.getId());
        showInfos(infos);
    }

    private void showInfos(String user){
        // Convertir la chaîne JSON en objet Java
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(user);
            id = jsonNode.get(0).asText();
            nomTxt.setText(jsonNode.get(1).asText());
            prenomTxt.setText(jsonNode.get(2).asText());
            mailTxt.setText(jsonNode.get(3).asText());
            affTxt.setText(jsonNode.get(4).asText());
        } catch (IOException e) {
            log.info("Une erreur s'est produite lors de la conversion de la chaîne JSON : " + e.getMessage());
        }
    }

    /**
     * Retourne à la page de gestion des annotateurs du gestionnaire
     */
    @FXML
    private void backToPage() {
        Facade.showScene(SceneType.GESTION_ANNOTATEUR);
    }

    /**
     * Met à jour les données de l'annotateur
     */
    @FXML
    private void updateInfos() {
        log.info("Les informations du user de formulaire n°"+id+ " ont bien été modifiées!");
        Facade.sendInfos(id, nomTxt.getText(), prenomTxt.getText(), mailTxt.getText(), affTxt.getText());
        Facade.showScene(SceneType.GESTION_ANNOTATEUR);
    }
}
