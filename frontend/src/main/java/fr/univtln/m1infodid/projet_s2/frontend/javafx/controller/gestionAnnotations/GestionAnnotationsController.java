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
    private List<List<String>> listeAnnotations;
    private static String epigraphieSelectionnee;
    public static String getEpigraphieSelectionnee() {
        return epigraphieSelectionnee;
    }


    private void setListeAnnotations(List<List<String>> listeAnnotation) {
        listeAnnotations = new ArrayList<>();
        for (List<String> annotation : listeAnnotation) {
            listeAnnotations.add(new ArrayList<>(annotation));
        }
    }

    public void initialize(List<List<String>> listeAnnotation) {
        setListeAnnotations(listeAnnotation);

        annotationListView.getItems().addAll(listeAnnotations);

        annotationListView.setCellFactory(new Callback<ListView<List<String>>, ListCell<List<String>>>() {
            @Override
            public ListCell<List<String>> call(ListView<List<String>> listView) {
                return new AnnotationListCell();
            }
        });
        annotationListView.getStyleClass().add("annotation-list-view");
    }

    public void reset() {
        annotationListView.getItems().clear();
        epigraphieSelectionnee = null;
    }

    @FXML
    public void backToMenu() {
        Facade.showScene(SceneType.HUB_GESTIONNAIRE);
    }

    public class AnnotationListCell extends ListCell<List<String>> {
        private HBox hbox;
        private Label texteLabel;
        private Button consulterButton;

        public AnnotationListCell() {
            super();
            createCellComponents();
            setupButtonAction();

        }

        private void createCellComponents() {
            hbox = new HBox();
            texteLabel = new Label();
            consulterButton = new Button("Consulter");
            consulterButton.getStyleClass().add("consulter-button");


            HBox btnBox = new HBox(10, consulterButton);
            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 40, 0, 0));

            hbox.getChildren().addAll(texteLabel, btnBox);
            hbox.setSpacing(10);
            HBox.setHgrow(btnBox, Priority.ALWAYS);
        }

        private void setupButtonAction() {
            consulterButton.setOnAction(event -> consulterAnnotation());
        }

        private void consulterAnnotation() {
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                String numero = itemData.get(1);
                epigraphieSelectionnee = numero;
            }
            Facade.showScene(SceneType.ANNOTATION);
        }

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
                consulterButton.getStyleClass().add("consulter-button");

                consulterButton.setOnAction(event -> consulterAnnotation());

                HBox.setHgrow(elementsContainer, Priority.ALWAYS);

                mainContainer.getChildren().addAll(elementsContainer, consulterButton);
                setGraphic(mainContainer);
            }
        }
    }
}