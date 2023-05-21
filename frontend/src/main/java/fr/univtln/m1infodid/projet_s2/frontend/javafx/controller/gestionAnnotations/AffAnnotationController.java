package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotations;

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
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class AffAnnotationController {
    @FXML
    private ListView<List<String>> annotationsListView;
    public static List<List<String>> listeAnnotations ;

    public void reset() {
        annotationsListView.getItems().clear();
    }
    private static void setListeAnnotations(List<List<String>> listeAnnotation) {
        listeAnnotations = new ArrayList<>();
        for (List<String> ann : listeAnnotation) {
            listeAnnotations.add(new ArrayList<>(ann));
        }
    }
    public void initialize(List<List<String>> listeAnnotation) {
        setListeAnnotations(listeAnnotation);
        System.out.println(listeAnnotations);

        annotationsListView.getItems().addAll(listeAnnotations);

        annotationsListView.setCellFactory(new Callback<ListView<List<String>>, ListCell<List<String>>>() {
            @Override
            public ListCell<List<String>> call(ListView<List<String>> listView) {
                return new FormulaireListCell();
            }
        });
        annotationsListView.getStyleClass().add("annotations-list-view");
    }
    /**
     * La classe FormulaireListCell pour les cellules de la liste des formulaires.
     */
    public class FormulaireListCell extends ListCell<List<String>> {
        private HBox hbox;
        private Label texteLabel;
        private Button supprimerButton;

        public FormulaireListCell() {
            super();

            hbox = new HBox();
            texteLabel = new Label();
            supprimerButton = new Button("Supprimer");

            HBox btnBox = new HBox(10, supprimerButton);
            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 40, 0, 0));

            hbox.getChildren().addAll(texteLabel, btnBox);
            hbox.setSpacing(10);
            HBox.setHgrow(btnBox, Priority.ALWAYS);

            supprimerButton.setOnAction(event -> supprimerAnnotation());

            supprimerButton.getStyleClass().add("supprimer-button");

        }

        /**
         * supprime la ligne de la listView
         */
        private void supprimerAnnotation() {
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
        protected void updateItem(List<String> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                HBox mainContainer = new HBox();
                mainContainer.setAlignment(Pos.CENTER_LEFT);

                HBox elementsContainer = new HBox();
                elementsContainer.setSpacing(10);
                elementsContainer.setAlignment(Pos.CENTER_LEFT);

                for (String element : item) {
                    Label label = new Label(element);
                    elementsContainer.getChildren().add(label);
                }

                Button supprimerButton = new Button("Supprimer");
                supprimerButton.setOnAction(event -> supprimerAnnotation());

                HBox.setHgrow(elementsContainer, Priority.ALWAYS); // Permet à la HBox des éléments de prendre toute la largeur disponible

                mainContainer.getChildren().addAll(elementsContainer, supprimerButton);
                setGraphic(mainContainer);
            }
        }
    }
    @FXML
    private void handleRetourButton() {
        Facade.showScene(SceneType.ANNOTATIONS);
    }
}
