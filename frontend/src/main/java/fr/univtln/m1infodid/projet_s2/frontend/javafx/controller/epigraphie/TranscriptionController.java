package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.univtln.m1infodid.projet_s2.frontend.javafx.manager.AnnotationsManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TranscriptionController{

    private static final int MAX_TRANSCRIPTION_CHAR_COUNT = 18;
    private static final int MAX_TRANSCRIPTION_LINE_COUNT = 6;
    
    @FXML private AnchorPane transcriptionPane;
    @FXML private Label idLabel;
    @FXML private VBox transcriptionVBox;

    private List<String> symbolList; //contient les symboles de la transcriptions dans l'ordre retrouvable par l'indice d'une sélection
    private Button lastSelectedButton;

    private AnnotationsManager annotationsManager;

    /**
     * Méthode qui initialise la partie transcription en changeant la label d'id Épigraphique et instancie le clavier de transcription
     * @param epiId l'id de l'épigraphie
     * @param transcription la liste des mots de la transcription divisé par ligne
     */
    public void setup(String epiId, List<String> transcription) {
        setIdLabel(epiId);
        setTranscription(transcription);
    }

    private void setIdLabel(String epiId) { idLabel.setText(epiId); }

    /**
     * Formatte la châine de caratère en entrée pour éliminer le bruit inutile à la transcription
     * tel que les morceaux entre paranthèses (..)
     * les potentiels manques de mot avant ou après une phrase symbolisé par [---]
     * ou le symbole + et les retours à la ligne
     * 
     * @param nonFormattedLine la chaîne de caratère à formatter
     * @return la chaîne de caractère formatée
     */
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

    /**
     * Instancie le clavier de transcription en respectant certaines contraintes de positionnement
     * Comme le nombre de caractère sur une ligne, si c'est au delà de MAX_TRANSCRIPTION_CHAR_COUNT, la ligne est subdivisé en 2
     * De même si le nombre de ligne par défaut est trop important, la taille de la police d'écriture est réduite
     * Finalement après que le clavier est crée, cette méthode sélectionne le premier bouton par défaut pour l'annotation
     * 
     * @param transcriptions la liste séparé par ligne de la transcription
     */
    private void setTranscription(List<String> transcriptions) {

        if (annotationsManager == null) annotationsManager = AnnotationsManager.getInstance();

        if (!transcriptionVBox.getChildren().isEmpty()) {
            resetTranscriptionPanel();
            annotationsManager.reset();
        }

        annotationsManager.setupEditBasedOnRole();

        symbolList = new ArrayList<>();

        if (transcriptions.size() > MAX_TRANSCRIPTION_LINE_COUNT){
            transcriptionVBox.setStyle("-fx-font-size: 8px;");
            transcriptionVBox.setSpacing(3);
        }
        else{
            transcriptionVBox.setStyle("-fx-font-size: 15px;");
            transcriptionVBox.setSpacing(5);
        }

        for (int i = 0; i < transcriptions.size(); i++) {
            String line = formatTranscriptionLine( transcriptions.get(i) );

            if (line.isEmpty() || line.isBlank() || line.contains("Face")) continue;
            //RENVOYER UNE EXCEPTION DE TRANSCRIPTION INVALIDE ? (exemple Face a : Face b : etc)

            String[] words = line.split(" ");
            int lineLength = Arrays.stream(words).mapToInt(String::length).sum();

            if (lineLength >= MAX_TRANSCRIPTION_CHAR_COUNT) {
                createHboxTooLargeLine(words);
            }else
                createHboxLine(words);            
        }

        //par défaut, sélection du premier symbole
        /*if (symbolList.size() > 1) {
            annotationsManager.symbolBtnOnClick(0, symbolList.get(0));
            symbolBtnOnClick( ((Button)((HBox)transcriptionVBox.getChildren().get(0)).getChildren().get(0)) );
        }*/
    }

    /**
     * Crée la ligne de bouton pour la transcription en subdivisant en plusieurs lignes si nécessaires
     * 
     * @param words la liste des mots contenu dans une ligne
     */
    private void createHboxTooLargeLine(String[] words) {
        int lineLength = Arrays.stream(words).mapToInt(String::length).sum();
        if (lineLength >= MAX_TRANSCRIPTION_CHAR_COUNT && words.length > 1) {
            String[] wordsSplit0 = Arrays.copyOfRange(words, 0, words.length / 2);
            String[] wordsSplit1 = Arrays.copyOfRange(words, words.length / 2, words.length);

            createHboxTooLargeLine(wordsSplit0);
            createHboxTooLargeLine(wordsSplit1);
        }else
            createHboxLine(words);
    }

    /**
     * Crée une ligne de mot avec une HBox englobante
     */
    private void createHboxLine(String[] words) {
        HBox lineBox = new HBox(5);
        lineBox.setAlignment(Pos.CENTER);

        createWordPanel(lineBox, words);
        transcriptionVBox.getChildren().add(lineBox);
    }

    /**
     * Efface tout le clavier de transcription
     */
    private void resetTranscriptionPanel() {
        transcriptionVBox.getChildren().clear();
    }

    /**
     * Crée une ligne de bouton pour chaque mot et les places dans la ligne HBox
     * 
     * @param lineBox HBox parent
     * @param words la liste des mots à traiter
     */
    private void createWordPanel(HBox lineBox, String[] words) {
        for (int j = 0; j < words.length; j++) {
            String word = words[j].replace(" ", "");
            if (word.isEmpty() || word.isBlank()) continue;

            int btnIndex = symbolList.size();
            symbolList.add(word);

            Button symbolBtn = new Button(word);
            symbolBtn.setId("symbolBtn");
            symbolBtn.setOnAction( e -> { annotationsManager.symbolBtnOnClick(btnIndex, word); symbolBtnOnClick(symbolBtn); } );
            lineBox.getChildren().add(symbolBtn);
        }
    }

    /**
     * Méthode de retour pour un clic d'un bouton de transcription
     */
    private void symbolBtnOnClick(Button btn) {
        
        if (lastSelectedButton != null) {
            String btnColor = annotationsManager.getHexColorOfSymbol(lastSelectedButton.getText());
            lastSelectedButton.setStyle( lastSelectedButton.getStyle().replace("-fx-text-fill: " + btnColor + ";", "-fx-text-fill: -fx-white;") );
        }
        lastSelectedButton = btn;
        setBtnColorFromAnnotation(btn, true);
    }

    public String getSymbolByIndex(int index) { return symbolList.get(index); }

    public void updateTranscriptionBtnsColor() {
        for (Node line : transcriptionVBox.getChildren()) {
            HBox lineBox = (HBox) line;
            for (Node child : lineBox.getChildren()) {
                Button btn = (Button) child;
                setBtnColorFromAnnotation(btn, false);
            }
        }

        annotationsManager.setupEditBasedOnRole();
    }

    private void setBtnColorFromAnnotation(Button btn, boolean selected) {
        String btnColor = annotationsManager.getHexColorOfSymbol(btn.getText());
        if (!selected)
            btn.setStyle("-fx-border-color:" + btnColor + "; -fx-text-fill: -fx-white;");
        else
            btn.setStyle("-fx-border-color:" + btnColor + "; -fx-text-fill: " + btnColor + ";");
    }
}
