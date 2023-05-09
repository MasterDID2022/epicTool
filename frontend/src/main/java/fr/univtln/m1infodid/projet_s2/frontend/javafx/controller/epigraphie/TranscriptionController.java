package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TranscriptionController{

    private final int MAX_TRANSCRIPTION_WORD_COUNT = 18;
    
    @FXML private AnchorPane transcriptionPane;
    @FXML private Label idLabel;
    @FXML private VBox transcriptionVBox;

    private List<String> symbolList; //contient les symboles de la transcriptions dans l'ordre retrouvable par l'indice d'une sélection

    public void setup(String epiId, List<String> transcription) {
        setIdLabel(epiId);
        setTranscription(transcription);
    }

    private void setIdLabel(String epiId) { idLabel.setText(epiId); }

    private String formatTranscriptionLine(String nonFormattedLine) {
        return nonFormattedLine
                .replaceAll("\\(.*\\)", "")
                .replace("[", " ")
                .replace("-", " ")
                .replace("]", " ")
                .replace("+", " ")
                .replace("\n", "")
                .strip();
    }

    private void setTranscription(List<String> transcriptions) {

        if (!transcriptionVBox.getChildren().isEmpty()) resetTranscriptionPanel();

        symbolList = new ArrayList<>();

        for (int i = 0; i < transcriptions.size(); i++) {
            String line = formatTranscriptionLine( transcriptions.get(i) );

            if (line.isEmpty() || line.isBlank()) continue;
            if (line.contains("Face")) throw new RuntimeException(); //RENVOYER UNE EXCEPTION DE TRANSCRIPTION INVALIDE (exemple Face a : Face b : etc)

            String[] words = line.split(" ");
            int lineLength = Arrays.stream(words).mapToInt(String::length).sum();

            if (lineLength >= MAX_TRANSCRIPTION_WORD_COUNT) {
                createHboxTooLargeLine(words);
            }else
                createHboxLine(words);            
        }
    }

    private void createHboxTooLargeLine(String[] words) {
        int lineLength = Arrays.stream(words).mapToInt(String::length).sum();
        if (lineLength >= MAX_TRANSCRIPTION_WORD_COUNT && words.length > 1) {
            String[] wordsSplit0 = Arrays.copyOfRange(words, 0, words.length / 2);
            String[] wordsSplit1 = Arrays.copyOfRange(words, words.length / 2, words.length);

            createHboxTooLargeLine(wordsSplit0);
            createHboxTooLargeLine(wordsSplit1);
        }else
            createHboxLine(words);
    }

    private void createHboxLine(String[] words) {
        HBox lineBox = new HBox(5);
        lineBox.setAlignment(Pos.CENTER);

        createWordPanel(lineBox, words);
        transcriptionVBox.getChildren().add(lineBox);
    }

    private void resetTranscriptionPanel() {
        transcriptionVBox.getChildren().clear();
    }

    private void createWordPanel(HBox lineBox, String[] words) {
        for (int j = 0; j < words.length; j++) {
            String word = words[j].replace(" ", "");
            if (word.isEmpty() || word.isBlank()) continue;

            int btnIndex = symbolList.size();
            symbolList.add(word);

            Button symbolBtn = new Button(word);
            symbolBtn.setId("symbolBtn");
            symbolBtn.setOnAction( e -> symbolBtnOnClick(btnIndex, word) );
            lineBox.getChildren().add(symbolBtn);
        }
    }

    private void symbolBtnOnClick(int btnIndex, String btnTxt) {
        System.out.println("BTN : " + btnIndex + " -> " + btnTxt); //wip pour l'issue de sélection, va changer
    }
}
