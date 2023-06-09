package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotateur;

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
 * Contrôleur pour la gestion des annotateurs.
 */
@Slf4j
public class GestionAnnotateurController {
    @FXML
    private ListView<List<String>> listListView;
    private static String emailSelectionne;
    private static String id;

    public static void setEmailSelectionne(String emailSelectionne) {
        GestionAnnotateurController.emailSelectionne = emailSelectionne;
    }

    public static String getEmailSelectionne() {
        return emailSelectionne;
    }

    public static void setId (String id) {
        GestionAnnotateurController.id = id;
    }

    public static String getId() {
        return id;
    }


    /**
     *
     * @param listePerAnnotateurs
     * Liste tous les annotateurs
     */
    private static List<List<String>> setListAnnotateurs(List<String> listePerAnnotateurs) {
        List<List<String>> listAnnotateurs = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String annotateur : listePerAnnotateurs) {
            try {
                JsonNode jsonNode = objectMapper.readTree(annotateur);
                String id = jsonNode.get("id").asText();
                String email = jsonNode.get("email").asText();
                listAnnotateurs.add(Arrays.asList(id, email));
            } catch (JsonProcessingException e) {
                log.warn("Erreur lors de la désérialisation du JSON");
            }
        }
        return listAnnotateurs;
    }


    public void initialize(List<String> listePerAnnotateurs) {
        List<List<String>> listAnnotateurs = setListAnnotateurs(listePerAnnotateurs);
        listListView.getItems().addAll(listAnnotateurs);

        listListView.setCellFactory(listView -> new AnnotateurCellList());
        listListView.getStyleClass().add("annotateur-list-view");
    }


    public void reset() {
        listListView.getItems().clear();
        GestionAnnotateurController.setEmailSelectionne("");
    }


    @FXML
    public void backToHub() {
        Facade.showScene(SceneType.HUB_GESTIONNAIRE);
    }


    /**
     * La classe AnnotateurCellList pour les cellules de la liste des annotateurs
     */
    public class AnnotateurCellList extends ListCell<List<String>> {
        private HBox hbox;
        private Label idLabel;
        private Label mailLabel;
        private Button consulterButton;
        private Button supprimerButton;

        public AnnotateurCellList() {
            super();

            hbox = new HBox();
            idLabel = new Label();
            mailLabel = new Label();
            consulterButton = new Button("Consulter");
            supprimerButton = new Button("Supprimer");

            HBox btnBox = new HBox(10, consulterButton, supprimerButton);
            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 30, 0, 0));

            hbox.getChildren().addAll(idLabel, mailLabel, btnBox);
            hbox.setSpacing(25);
            HBox.setHgrow(btnBox, Priority.ALWAYS);

            consulterButton.setOnAction(event -> consulterAnnotateur());
            supprimerButton.setOnAction(event -> supprimerAnnotateur());

            consulterButton.getStyleClass().add("consulter-button");
            supprimerButton.getStyleClass().add("supprimer-button");
        }


        /**
         * Renvoie vers les infos correspondant au mail selectionné
         */
        private void consulterAnnotateur() {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                GestionAnnotateurController.setId(itemData.get(0));
                GestionAnnotateurController.setEmailSelectionne(itemData.get(1));
            }
            String jsonInfos = Facade.getUserInfos(GestionAnnotateurController.getEmailSelectionne());
            if(!jsonInfos.isEmpty()) {
                if(!jsonInfos.equals("FIN")){
                    InfosAnnotateurController.setInfos(jsonInfos);
                    Facade.showScene(SceneType.INFOS_ANNOTATEUR);
                }
            }
            else{
                log.error("Erreur lors de la récuperation des données.");
            }
        }


        /**
         * Supprime user sélectionné
         */
        private void supprimerAnnotateur() {
            List<String> itemData = getItem();
            if (! itemData.isEmpty()){
                    getListView().getItems().remove(itemData);
                Facade.sendIdUserToDelete(Integer.parseInt(itemData.get(0)));
            }
        }



        /**
         * Affiche la cellule en fonction des données fournies
         *
         * @param item  Les données de la cellule
         * @param empty Indique si la cellule est vide ou non
         */
        @Override
        protected void updateItem(List<String> item, boolean empty){
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                idLabel.setText(item.get(0));
                mailLabel.setText(item.get(1));
                setGraphic(hbox);
            }
        }
    }
}
