package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe logique de la vue visualisation d'épigraphe
 */
public class PageVisualisationController implements Initializable {
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView plaqueImg;
    @FXML
    private Parent traduction;
    @FXML
    private TradController traductionController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    public void setupVisualEpigraphe(String id, String imgUrl, String plaqueTxt, String tradTxt) {
        plaqueImg.setImage(new Image(imgUrl));
        traductionController.displayTranslation(tradTxt);

    }
}
