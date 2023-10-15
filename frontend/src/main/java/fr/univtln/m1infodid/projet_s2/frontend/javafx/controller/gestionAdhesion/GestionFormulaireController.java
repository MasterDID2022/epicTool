package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contrôleur pour la gestion des formulaires.
 */
@Slf4j
public class GestionFormulaireController {

    @FXML
    private ListView<List<String>> formulaireListView;
    private static String emailSelectionne;
    private static String id;


    public static void setEmailSelectionne(String emailSelectionne) {
        GestionFormulaireController.emailSelectionne = emailSelectionne;
    }

    public static String getEmailSelectionne() {
        return emailSelectionne;
    }

    public static void setId(String id) {
        GestionFormulaireController.id = id;
    }

    public static String getId() {
        return id;
    }

    /**
     * Liste tous les formulaires
     *
     * @param listPerFormulaire
     */
    private static List<List<String>> setListFormulaires(List<String> listPerFormulaire) {
        List<List<String>> listFormulaires = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String formulaire : listPerFormulaire) {
            try {
                JsonNode jsonNode = objectMapper.readTree(formulaire);
                String id = jsonNode.get("id").asText();
                String email = jsonNode.get("email").asText();
                listFormulaires.add(Arrays.asList(id, email));
            } catch (JsonProcessingException e) {
                log.warn("Erreur lors de la désérialisation du JSON");
            }
        }
        return listFormulaires;
    }


    /**
     * initialize est une méthode d'initialisation du contrôleur.
     * Cette méthode est de l'initialisation du fichier FXML.
     */
    public void initialize(List<String> listPerFormulaire) {
        List<List<String>> listFormulaires = setListFormulaires(listPerFormulaire);
        formulaireListView.getItems().addAll(listFormulaires);

        formulaireListView.setCellFactory(listView -> new FormulaireListCell());
        formulaireListView.getStyleClass().add("formulaire-list-view");
    }


    public void reset() {
        formulaireListView.getItems().clear();
        GestionFormulaireController.setEmailSelectionne("");
    }


    @FXML
    public void backToMenu() {
        Facade.showScene(SceneType.HUB_GESTIONNAIRE);
    }


    /**
     * La classe FormulaireListCell pour les cellules de la liste des formulaires.
     */
    public class FormulaireListCell extends ListCell<List<String>> {
        private HBox hbox;
        private Label mailForm;
        private Button consulterButton;
        private Button validerButton;
        private Button supprimerButton;

        public FormulaireListCell() {
            super();

            hbox = new HBox();
            mailForm = new Label();
            consulterButton = new Button("Consulter");
            validerButton = new Button("Valider");
            supprimerButton = new Button("Supprimer");

            HBox btnBox = new HBox(10, consulterButton, validerButton, supprimerButton);
            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 30, 0, 0));

            hbox.getChildren().addAll(mailForm, btnBox);
            hbox.setSpacing(10);
            HBox.setHgrow(btnBox, Priority.ALWAYS);

            consulterButton.setOnAction(event -> consulterFormulaire());
            validerButton.setOnAction(event -> {
                try {
                    validerFormulaire();
                } catch (JsonProcessingException e) {
                    log.error("Err: parsing formulaire");
                }
            });
            supprimerButton.setOnAction(event -> supprimerFormulaire());

            consulterButton.getStyleClass().add("consulter-button");
            validerButton.getStyleClass().add("valider-button");
            supprimerButton.getStyleClass().add("supprimer-button");

        }

        /**
         * renvoie vers le formulaire correspondant a l adresse selectionnée
         */
        private void consulterFormulaire() {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                GestionFormulaireController.setId(itemData.get(0));
                GestionFormulaireController.setEmailSelectionne(itemData.get(1));
            }
            String jsonInfos = Facade.getUserInfos(GestionFormulaireController.getEmailSelectionne());
            if(!jsonInfos.isEmpty()) {
                if(!jsonInfos.equals("FIN")){
                    RecapDemandeController.setInfos(jsonInfos);
                    Facade.showScene(SceneType.AFFICHAGE_DEMANDE);
                }
            }
            else{
                log.error("Erreur lors de la récuperation des données.");
            }
            Facade.showScene(SceneType.AFFICHAGE_DEMANDE);
        }

        private void validerFormulaire() throws JsonProcessingException {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                getListView().getItems().remove(itemData);
                GestionFormulaireController.setEmailSelectionne(itemData.get(1));
            }
            String mail = Facade.getUserInfos(GestionFormulaireController.getEmailSelectionne());
            if(!mail.equals("FIN")){
                ObjectMapper objectMapper = new ObjectMapper();
                String mtp = objectMapper.readTree(mail).get(5).asText();
                Facade.sendUser(GestionFormulaireController.getEmailSelectionne(), mtp);
            }
        }

        /**
         * supprime la ligne de la listView
         */
        private void supprimerFormulaire() throws NullPointerException {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                getListView().getItems().remove(itemData);
                Integer idToDelete = Integer.parseInt(itemData.get(0));
                Facade.sendFormulaireToDelete(idToDelete);
            }
        }




        /**
         * La méthode updateItem affiche la cellule en fonction des données fournies.
         *
         * @param item  Les données de la cellule.
         * @param empty Indique si la cellule est vide ou non.
         */
        @Override
        protected void updateItem(List<String> item, boolean empty){
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                mailForm.setText(item.get(1));
                setGraphic(hbox);
            }
        }
    }
}
