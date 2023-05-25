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
import javafx.util.Callback;

import java.util.*;

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
        if (listeAnnotation == null || listeAnnotation.get().isEmpty()) return;
        actualIdEpigraphie = listeAnnotation.get().get(0).get(0).split(":", 2)[0];
        if (listeAnnotation.isPresent()) {
            setListeAnnotations(listeAnnotation.get());
            setTitleText();
            actuel = new ArrayList<>();

            for ( List<String> entry : listeAnnotation.get() )
                actuel.add( entry.get(0).split(":", 2)[1] );
            annotationsListView.getItems().addAll(actuel);
            setupCellFactory();
            annotationsListView.getStyleClass().add("annotations-list-view");
        } else {
            // Gérer le cas où la liste d'annotations est absente ou vide
            // Par exemple, afficher un message d'erreur ou une indication à l'utilisateur
        }
    }

    private void setListeAnnotations(List<List<String>> listeAnnotation) {
        listeAnnotations = new ArrayList<>();
        for (List<String> ann : listeAnnotation) {
            listeAnnotations.add(ann);
        }
    }

    private void setupCellFactory() {
        annotationsListView.setCellFactory( e -> new FormulaireListCell() );
    }

    public class FormulaireListCell extends ListCell<String> {
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
                String annotationMail = itemData;
                Api.deleteAnnotationOf(actualIdEpigraphie, annotationMail);
                for (int i = 0; i < annotationsListView.getItems().size(); i++) {
                    if (annotationsListView.getItems().get(i).equals(annotationMail)) {
                        annotationsListView.getItems().remove(i);
                        break;
                    }
                }
                actuel.remove(annotationMail);
                for (List<String> entry : listeAnnotations) {
                    if (entry.get(0).equals(actualIdEpigraphie + ":" + annotationMail)) {
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

    public void setAnnotationsListView(ListView<String> annotationsListView) {
        this.annotationsListView = annotationsListView;
    }

    public void setTitleLabel(Label title) {
        this.title = title;
    }
}
