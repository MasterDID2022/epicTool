package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.*;

public class ListeAnnotationController {

    @FXML
    public AnchorPane backgroundAnnotation;
    @FXML
    private ListView<ListeAnnotationData> listeAnnotateurView;

    public List<ListeAnnotationData> listAnnotation(List<String> annotationList) {

        List<ListeAnnotationData> listAnnotation = new ArrayList<>();

        for ( String jsonString: annotationList){

            try {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonString);


                int epigraphe = jsonNode.get("idEpigraphe").asInt();
                String utilisateur = jsonNode.get("email").asText();
                JsonNode listCoordonesPolyNode = jsonNode.get("listCoordonesPoly");
                Map<Integer, List<Double>> listCoordonesPoly = new HashMap<>();
                ListeAnnotationData listeAnnotationData = null;
                for (Iterator<Map.Entry<String, JsonNode>> it = listCoordonesPolyNode.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    int key = Integer.parseInt(entry.getKey());
                    List<Double> values = new ArrayList<>();
                    entry.getValue().forEach(valueNode -> values.add(valueNode.asDouble()));
                    listCoordonesPoly.put(key, values);
                    listeAnnotationData = new ListeAnnotationData(epigraphe,utilisateur,listCoordonesPoly);
                }
                listAnnotation.add(listeAnnotationData);

            }catch (Exception E){
                E.getMessage();
            }


        }
        return listAnnotation;
    }

    public class consulterAnnotation extends ListCell<ListeAnnotationData> {

            private Label mailUserLabel;
            private Button userConsulterButton;

            private HBox hbox;

            public consulterAnnotation() {
                super();

                mailUserLabel = new Label();
                userConsulterButton = new Button("▲");
                userConsulterButton.setRotate(90);
                hbox = new HBox();

                HBox btnBox = new HBox(0, userConsulterButton);
                btnBox.setAlignment(Pos.CENTER_RIGHT);
                HBox.setMargin(btnBox, new Insets(0, 5, 0, 0));

                hbox.getChildren().addAll(mailUserLabel, btnBox);
                hbox.setSpacing(15);
                HBox.setHgrow(btnBox, Priority.ALWAYS);

                userConsulterButton.setOnAction(event -> consulterLesAnnotation());
                userConsulterButton.setId("Button-Consulter");
            }


            /**
             * Renvoie vers les infos correspondant au mail selectionné
             */
        private void consulterLesAnnotation() {
            ListeAnnotationData itemData = getItem(); //annotation selectionne
            //actualiser l'affichage de l'annotation actuel (AnnotationManager)
            Facade.updateVisualAnnotationEpigraphie(itemData);
        }



        /**
         * Affiche la cellule en fonction des données fournies
         *
         * @param item  Les données de la cellule
         * @param empty Indique si la cellule est vide ou non
         */
        @Override
        protected void updateItem(ListeAnnotationData item, boolean empty){
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                mailUserLabel.setText(item.getEmail());
                setGraphic(hbox);
            }
        }
    }


    public void initialize(List<String> annotations){
        listeAnnotateurView.getItems().clear();
        List<ListeAnnotationData> listannot = listAnnotation(annotations);

        if (listannot.isEmpty()) {
            listeAnnotateurView.getParent().getParent().setVisible(false);
            return;
        }
        listeAnnotateurView.getParent().getParent().setVisible(true);
        listeAnnotateurView.getItems().addAll(listannot);


        listeAnnotateurView.setCellFactory(listView -> new consulterAnnotation());
    }

}
