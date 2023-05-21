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
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class GestionAnnotationsController {

    @FXML
    private ListView<List<String>> annotationListView;
    public static List<List<String>> listeAnnotations ;
    public static String epigraphieSelectionnée ;

    /**
     *
     * @param listeAnnotation
     * remplie le champ liste de annotation avec une liste de liste en parametre
     */
    private static void setlisteAnnotations(List<List<String>> listeAnnotation) {
        listeAnnotations = new ArrayList<>();
        for (List<String> annotation : listeAnnotation) {
            listeAnnotations.add(new ArrayList<>(annotation));
        }
    }

    /**
     * initialize est une méthode d'initialisation du contrôleur.
     * Cette méthode est de l'initialisation du fichier FXML.
     */
    public void initialize(List<List<String>> listeAnnotation) {
        setlisteAnnotations(listeAnnotation);

        annotationListView.getItems().addAll(listeAnnotations);

        annotationListView.setCellFactory(new Callback<ListView<List<String>>, ListCell<List<String>>>() {
            @Override
            public ListCell<List<String>> call(ListView<List<String>> listView) {
                return new GestionAnnotationsController.AnnotationListCell();
            }
        });
        annotationListView.getStyleClass().add("annotation-list-view");
    }
    public void reset() {
        annotationListView.getItems().clear();
        epigraphieSelectionnée = null;
    }


    @FXML
    public void backToMenu() {
        Facade.showScene(SceneType.MENU);
    }

    /**
     * La classe AnnotationListCell pour les cellules de la liste des annotations.
     */
    public class AnnotationListCell extends ListCell<List<String>> {

        private HBox hbox;
        private Label texteLabel;
        private Button consulterButton;

        public AnnotationListCell() {
            super();

            hbox = new HBox();
            texteLabel = new Label();
            consulterButton = new Button("Consulter");

            HBox btnBox = new HBox(10, consulterButton);
            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 40, 0, 0));

            hbox.getChildren().addAll(texteLabel, btnBox);
            hbox.setSpacing(10);
            HBox.setHgrow(btnBox, Priority.ALWAYS);

            consulterButton.setOnAction(event -> consulterAnnotation());
            consulterButton.getStyleClass().add("consulter-button");

        }
        /**
         * renvoie vers le annotation correspondant a l adresse selectionnée
         */
        private void consulterAnnotation() {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                String numero = itemData.get(1);
                epigraphieSelectionnée = numero;
            }
            Facade.showScene(SceneType.ANNOTATION);
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
                Button consulterButton = new Button("consulter");
                consulterButton.setOnAction(event -> consulterAnnotation());

                HBox.setHgrow(elementsContainer, Priority.ALWAYS);

                mainContainer.getChildren().addAll(elementsContainer, consulterButton);
                setGraphic(mainContainer);
            }
        }

    }

}
