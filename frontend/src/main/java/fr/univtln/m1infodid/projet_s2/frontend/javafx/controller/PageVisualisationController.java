package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/** Classe logique de la vue visualisation d'épigraphe */
public class PageVisualisationController implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private ImageView plaqueImg;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
        //test pour l'affichage de l'image, à supprimer
        setupVisualEpigraphe("13", 
                            "http://ccj-epicherchel.huma-num.fr/interface/phototheque/13/40341.jpg",
                            "IACENTVS•CORPOR\nRE•QVSTOS•H•S•E•\nNATOS•FECERVNT", 
                            "Iacentus, garde du corps. Il repose ici. Ses enfants ont fait faire (sa tombe).");
    }

    public void setupVisualEpigraphe(String id, String imgUrl, String plaqueTxt, String tradTxt) {
        //afficher l'id et d'autres infos de l'épigraphie sur la page
        //obtenir l'image et l'afficher
        //construire le clavier à partir du texte de la plaque
        //modifier le label pour afficher la traduction

        //obtiens l'image et l'affiche (TEST !!!!)
        plaqueImg.setImage( new Image(imgUrl) );
    }
}
