package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TradController {
    @FXML
    private Label tradLabel;

    public void displayTranslation(String tradTxt){
        tradLabel.setText(tradTxt);
    }

}
