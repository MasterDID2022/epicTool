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

public class AffAnnotationController {

    @FXML
    private ListView<List<String>> annotationsListView;

    @FXML
    private Label title;

    private List<List<List<String>>> listeAnnotations;
    private List<List<String>> actuel;

    public void reset() {
        setTitleText();
        annotationsListView.getItems().clear();
        actuel = listeAnnotations.get(getSelectedEpigraphieIndex());
        annotationsListView.getItems().addAll(actuel);
        setupCellFactory();
        annotationsListView.getStyleClass().add("annotations-list-view");
    }

    private void setTitleText() {
        title.setText("Les annotations de l'épigraphie " + GestionAnnotationsController.getEpigraphieSelectionnée()) ;   }

    /**
     * Récupère l'index de l'épigraphie sélectionnée.
     *
     * @return L'index de l'épigraphie sélectionnée.
     */
    private int getSelectedEpigraphieIndex() {
        return Integer.parseInt(GestionAnnotationsController.getEpigraphieSelectionnée()) - 1;
    }


    public void initialize(List<List<List<String>>> listeAnnotation) {
        setListeAnnotations(listeAnnotation);
        setTitleText();
        actuel = listeAnnotation.get(getSelectedEpigraphieIndex());
        annotationsListView.getItems().addAll(actuel);
        setupCellFactory();
        annotationsListView.getStyleClass().add("annotations-list-view");
    }

    private void setListeAnnotations(List<List<List<String>>> listeAnnotation) {
        listeAnnotations = new ArrayList<>();
        for (List<List<String>> ann : listeAnnotation) {
            listeAnnotations.add(new ArrayList<>(ann));
        }
    }

    private void setupCellFactory() {
        annotationsListView.setCellFactory(new Callback<ListView<List<String>>, ListCell<List<String>>>() {
            @Override
            public ListCell<List<String>> call(ListView<List<String>> listView) {
                return new FormulaireListCell();
            }
        });
    }

    public class FormulaireListCell extends ListCell<List<String>> {
        private HBox hbox;
        private Label texteLabel;
        private Button supprimerButton;

        public FormulaireListCell() {
            super();
            createCellComponents();
            setupButtonAction();
            supprimerButton.getStyleClass().add("supprimer-button");
        }

        private void createCellComponents() {
            hbox = new HBox();
            texteLabel = new Label();
            supprimerButton = new Button("Supprimer");

            HBox btnBox = new HBox(10, supprimerButton);
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
            List<String> itemData = getItem();
            if (itemData != null && !itemData.isEmpty()) {
                annotationsListView.getItems().remove(itemData);
                actuel.remove(itemData);
                listeAnnotations.get(getSelectedEpigraphieIndex()).remove(itemData);
            }
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

                Button supprimerButton = new Button("Supprimer");
                supprimerButton.setOnAction(event -> supprimerAnnotation());

                HBox.setHgrow(elementsContainer, Priority.ALWAYS);

                mainContainer.getChildren().addAll(elementsContainer, supprimerButton);
                setGraphic(mainContainer);
            }
        }
    }

    @FXML
    private void handleRetourButton() {
        Facade.showScene(SceneType.ANNOTATIONS);
    }

    public void setAnnotationsListView(ListView<List<String>> annotationsListView) {
        this.annotationsListView = annotationsListView;
    }

    public void setTitleLabel(Label title) {
        this.title = title;
    }
}
