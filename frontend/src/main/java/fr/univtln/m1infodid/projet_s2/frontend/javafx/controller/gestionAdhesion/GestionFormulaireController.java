package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAdhesion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import fr.univtln.m1infodid.projet_s2.backend.model.Formulaire;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur pour la gestion des formulaires.
 */
public class GestionFormulaireController {

    @FXML
    private ListView<List<String>> formulaireListView;
    public static List<List<String>> listeDeFormulaires ;
    public static String emailSelectionné ;

    /**
     *
     * @param listeDeFormulaire
     * remplie le champ liste de formulaire avec une liste de liste en parametre
     */
    private static void setListeDeFormulaires(List<List<String>> listeDeFormulaire) {
        listeDeFormulaires = new ArrayList<>();
        for (List<String> formulaire : listeDeFormulaire) {
            listeDeFormulaires.add(new ArrayList<>(formulaire));
        }
    }

    /**
     * initialize est une méthode d'initialisation du contrôleur.
     * Cette méthode est de l'initialisation du fichier FXML.
     */
    public void initialize(List<List<String>> listeDeFormulaire) {
        setListeDeFormulaires(listeDeFormulaire);

        formulaireListView.getItems().addAll(listeDeFormulaires);

        formulaireListView.setCellFactory(new Callback<ListView<List<String>>, ListCell<List<String>>>() {
            @Override
            public ListCell<List<String>> call(ListView<List<String>> listView) {
                return new FormulaireListCell();
            }
        });
        formulaireListView.getStyleClass().add("formulaire-list-view");
    }
    public void reset() {
        formulaireListView.getItems().clear();
        emailSelectionné = null;
    }




    @FXML
    public void backToMenu() {
        Facade.showScene(SceneType.MENU);
    }

    /**
     * La classe FormulaireListCell pour les cellules de la liste des formulaires.
     */
    public class FormulaireListCell extends ListCell<List<String>> {
        private HBox hbox;
        private Label texteLabel;
        private Button consulterButton;
        private Button validerButton;
        private Button supprimerButton;

        public FormulaireListCell() {
            super();

            hbox = new HBox();
            texteLabel = new Label();
            consulterButton = new Button("Consulter");
            validerButton = new Button("Valider");
            supprimerButton = new Button("Supprimer");

            HBox btnBox = new HBox(10, consulterButton, validerButton, supprimerButton);
            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 40, 0, 0));

            hbox.getChildren().addAll(texteLabel, btnBox);
            hbox.setSpacing(10);
            HBox.setHgrow(btnBox, Priority.ALWAYS);

            consulterButton.setOnAction(event -> consulterFormulaire());
            validerButton.setOnAction(event -> validerFormulaire());
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
                String email = itemData.get(0);
                emailSelectionné = email;
            }
            Facade.showScene(SceneType.AFFICHAGE_DEMANDE);
        }

        private void validerFormulaire() {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                String email = itemData.get(0);
                emailSelectionné = email;

                // Envoie de l'e-mail
                try {
                    Formulaire formulaire = new Formulaire();
                    formulaire.setEmail(email);

                    SI.sendMail(true, formulaire);
                    // Conversion de l'objet Formulaire en JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode formulaireNode = objectMapper.createObjectNode();
                    formulaireNode.put("emailFormulaire", formulaire.getEmail());
                    String formulaireJson = objectMapper.writeValueAsString(formulaireNode);

                    Api.postFormulaire(formulaireJson);

                    System.out.println("c bon");
                } catch (Exception e) {
                    System.out.println("c nonn");

                }
            }
            Facade.showScene(SceneType.AFFICHAGE_DEMANDE);
        }
        /**
         * supprime la ligne de la listView
         */
        private void supprimerFormulaire() {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                getListView().getItems().remove(itemData);
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
                texteLabel.setText(item.get(0));
                setGraphic(hbox);
            }
        }
    }
}
