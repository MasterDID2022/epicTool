package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.gestionAnnotations;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AffAnnotationController {

    @FXML
    private ListView<String> annotationsListView;

    @FXML
    private Label title;

    private List<List<String>> listeAnnotations;
    private List<String> actuel;

    private String actualIdEpigraphie;

    public void reset() {
        setTitleText();
        annotationsListView.getItems().clear();
        setupCellFactory();
        annotationsListView.getStyleClass().add("annotations-list-view");
    }

    private void setTitleText() { title.setText("Les annotations de l'épigraphie " + GestionAnnotationsController.getEpigraphieSelectionnee()) ;   }

    public void initialize(Optional<List<List<String>>> listeAnnotation) {
        if (listeAnnotation.orElse(List.of()).isEmpty()) return;
        actualIdEpigraphie = listeAnnotation.orElseThrow().get(0).get(0).split(":", 2)[0];
        setListeAnnotations(listeAnnotation.orElseThrow());
        setTitleText();
        actuel = new ArrayList<>();

        for ( List<String> entry : listeAnnotation.orElseThrow() )
            actuel.add( entry.get(0).split(":", 2)[1] );
        annotationsListView.getItems().addAll(actuel);
        setupCellFactory();
        annotationsListView.getStyleClass().add("annotations-list-view");
    }

    private void setListeAnnotations(List<List<String>> listeAnnotation) {
        listeAnnotations = new ArrayList<>();
        listeAnnotations.addAll(listeAnnotation);
    }

    private void setupCellFactory() {
        annotationsListView.setCellFactory( e -> new FormulaireListCell() );
    }

    public class FormulaireListCell extends ListCell<String> { //NOSONAR
        private Button supprimerButton;

        public FormulaireListCell() {
            super();
            createCellComponents();
            setupButtonAction();

        }

        private void createCellComponents() {
            HBox hbox = new HBox();
            Label texteLabel = new Label();
            supprimerButton = new Button("Supprimer");

            HBox btnBox = new HBox(10, supprimerButton);
            btnBox.getStyleClass().add("supprimer-button");

            btnBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(btnBox, new Insets(0, 40, 0, 0));

            hbox.getChildren().addAll(texteLabel, btnBox);
            hbox.setSpacing(10);
            HBox.setHgrow(btnBox, Priority.ALWAYS);
        }

        private void setupButtonAction() {
            supprimerButton.setOnAction(event -> supprimerAnnotation());
        }

        private void supprimerAnnotation() {
            String itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                Api.deleteAnnotationOf(actualIdEpigraphie, itemData);
                for (int i = 0; i < annotationsListView.getItems().size(); i++) {
                    if (annotationsListView.getItems().get(i).equals(itemData)) {
                        annotationsListView.getItems().remove(i);
                        break;
                    }
                }
                actuel.remove(itemData);
                for (List<String> entry : listeAnnotations) {
                    if (entry.get(0).equals(actualIdEpigraphie + ":" + itemData)) {
                        listeAnnotations.remove(entry);
                        return;
                    }
                }
            }
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                HBox mainContainer = new HBox();
                mainContainer.setAlignment(Pos.CENTER_LEFT);

                HBox elementsContainer = new HBox();
                elementsContainer.setSpacing(10);
                elementsContainer.setAlignment(Pos.CENTER_LEFT);
                Label label = new Label(item);
                elementsContainer.getChildren().add(label);
                Button supprimerBtn = new Button("Supprimer");
                supprimerBtn.getStyleClass().add("supprimer-button");
                supprimerBtn.setOnAction(event -> supprimerAnnotation());

                HBox.setHgrow(elementsContainer, Priority.ALWAYS);

                mainContainer.getChildren().addAll(elementsContainer, supprimerBtn);
                setGraphic(mainContainer);
            }
        }
    }

    @FXML
    private void handleRetourButton() {
        Facade.showScene(SceneType.ANNOTATIONS);
    }

}
