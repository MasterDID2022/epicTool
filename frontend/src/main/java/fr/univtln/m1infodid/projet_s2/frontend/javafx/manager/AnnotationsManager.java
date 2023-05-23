package fr.univtln.m1infodid.projet_s2.frontend.javafx.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.Facade.ROLE;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.ListeAnnotationData;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie.TranscriptionController;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Cette classe s'occupe de gérer l'annotation d'une épigraphie
 */
public class AnnotationsManager {

    private static AnnotationsManager instance;

    private Pane paneCanvas;
    private ImageView imgView;

    private static final String SELECTED_RECT_CLASS_FLAG = "annotationRectSelected";

    private String currentIndexSelected;
    private String currentSymbolSelected;
    
    private Map<String, String> colorMap; //key = btnSymbol, value = hex color
    private Map<String, Rectangle> rectMap; //key = btnIndex to String, value = Rectangle obj
    private Rectangle currentRectSelected;

    private double initX;
    private double initY;

    private Random rand = new Random();

    private TranscriptionController transcriptionController;
    private boolean canEdit = false;

    private String importedEmail;

    public static AnnotationsManager getInstance() { 
        if (instance == null) instance = new AnnotationsManager();
        return instance;
    }

    /**
     * Initialise l'environnement d'annotation
     * 
     * @param paneCanvas le Pane parent pour les futures annotations
     * @param imgView le contenant de l'image à annoter
     */
    public void initialize(Pane paneCanvas, ImageView imgView, TranscriptionController transcriptionController) { 
        this.paneCanvas = paneCanvas;
        this.imgView = imgView;
        this.transcriptionController = transcriptionController;
        this.importedEmail = "";

        reset();
    }

    public void setupEditBasedOnRole() {
        boolean roleCheck = Facade.getRole() != ROLE.VISITEUR;
        if (!importedEmail.isBlank() && !importedEmail.isEmpty())
            setCanEdit(roleCheck && Facade.getEmail().equals(importedEmail));
        else
            setCanEdit( roleCheck );
        if (!canEdit) return;
        setupMouseEvent();
    }

    /**
     * Réinitialise proprement l'environnement d'annotation
     */
    public void reset() {
        rectMap = new HashMap<>();
        colorMap = new HashMap<>();

        currentIndexSelected = "-1";
        currentSymbolSelected = "NONE";
        currentRectSelected = null;
        importedEmail = "";
        paneCanvas.getChildren().clear();
        removeMouseEvent();
    }

    /**
     * Actualise l'affichage des annotations d'une épigraphie par import de annotationData
     */
    public void importAnnotation(ListeAnnotationData annotationData) {
        reset();

        for (Integer rectId : annotationData.getAnnotations().keySet()) {
            List<Double> poly = annotationData.getAnnotations().get(rectId);

            currentIndexSelected = String.valueOf( rectId.intValue() );
            currentSymbolSelected = transcriptionController.getSymbolByIndex( rectId.intValue() );

            if (!colorMap.containsKey(currentSymbolSelected)){
                String rColor = getRandomColorHex();
                colorMap.put(currentSymbolSelected, rColor);
            }

            Rectangle r = createRectangle();
            r.setX(poly.get(0));
            r.setY(poly.get(1));
            r.setWidth(poly.get(2)); //potentiel problème si backend envois height puis width, à inverser dans ce cas
            r.setHeight(poly.get(3)); //potentiel problème si backend envois height puis width, à inverser dans ce cas
        }

        importedEmail = annotationData.getEmail();
        transcriptionController.updateTranscriptionBtnsColor();
        currentIndexSelected = "-1";
        currentSymbolSelected = "NONE";
        currentRectSelected = null;
    }

    public String getImportedEmail() { return importedEmail; }

    /**
     * Permet de générer des couleurs aléatoires pour l'annotation
     * @return une chaîne de caractère format hexadécimal d'une couleur aléatoire
     */
    public String getRandomColorHex() {
        Color c = Color.rgb( rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return String.format("#%02x%02x%02x", (int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255));
    }

    public Map<String,Rectangle> getAnnotationsRectMap() { return rectMap; }
    public Map<String, String> getAnnotationsColorMap() { return colorMap; }

    public String getHexColorOfSymbol(String btnSymbol) { 
        if (colorMap.containsKey(btnSymbol)) return colorMap.get(btnSymbol);
        return "#ffffff";
    }

    /**
     * Met en place les évènements souris à capturer pour le tracer d'annotations
     * Respectivement: 
     * - onDragDetected = Déclenché par un maintien du clic gauche souris et un mouvement
     * - onMouseDragged = Déclenché par le déplacement de la souris en maintenant constamment le clic gauche
     * - onMouseReleased = Déclenché par le relachement du clic gauche
     */
    private void setupMouseEvent() {
        imgView.setOnDragDetected( e -> onDragEnter(e.getX(), e.getY()) );
        imgView.setOnMouseDragged( e -> onDragOver(e.getX(), e.getY()) );
        imgView.setOnMouseReleased( e -> onDragDone(e.getX(), e.getY()) );
    }

    private void removeMouseEvent() {
        imgView.setOnDragDetected( null );
        imgView.setOnMouseDragged( null );
        imgView.setOnMouseReleased( null );
    }

    private void onDragEnter(double x, double y) { createSymbolSelection(x, y); }
    private void onDragOver(double x, double y) { updateRectSize(x, y); }
    private void onDragDone(double x, double y) { updateRectSize(x, y); }

    /**
     * Crée un nouveau Rectangle d'annotation si il n'en existe pas pour le symbole selectionné
     * @param x Coordonnée X de la souris
     * @param y Coordonnée Y de la souris
     */
    private void createSymbolSelection(double x, double y) {
        if (currentIndexSelected.equals("-1") || currentSymbolSelected.equals("NONE")) return;

        Rectangle r;
        if (currentRectSelected != null && !rectMap.isEmpty() && rectMap.get(currentIndexSelected) == currentRectSelected)
            r = rectMap.get(currentIndexSelected);
        else {
            r = createRectangle();
        }

        r.setX(x);
        r.setY(y);
        r.setWidth(0);
        r.setHeight(0);
        initX = x;
        initY = y;

        currentRectSelected = r;
    }

    /**
     * Crée un rectangle (sans définir sa position ni sa taille)
     * Ajoute les infos nécéssaires sur le rectangle (son id + styleClass)
     * Défini la couleur aléatoire du rectangle
     * Puis l'affiche dans le paneCanvas
     * 
     * @return Le rectangle crée (ce qui permet de définir la position et taille par la suite)
     */
    private Rectangle createRectangle() {
        Rectangle r = new Rectangle();
        r.setId("annotationRect");
        r.getStyleClass().add( SELECTED_RECT_CLASS_FLAG );
        
        String rectColor = colorMap.get(currentSymbolSelected);
        r.setStyle("-fx-stroke:" + rectColor + "; -fx-fill:" + rectColor + ";");

        rectMap.put(currentIndexSelected, r);
        paneCanvas.getChildren().add(r);

        return r;
    }

    /**
     * Actualise la taille du Rectangle d'annotation avec la position actuel de la souris
     */
    private void updateRectSize(double x, double y) {
        if (currentRectSelected == null) return;
        if (x >= imgView.getFitWidth()) return;
        if (y >= imgView.getFitHeight()) return;

        currentRectSelected.setWidth(x - initX);
        currentRectSelected.setHeight(y - initY);
    }

    /**
     * Méthode de retour pour un clic de bouton de transcription
     * Défini les différentes couleurs et rectangle à annoter si jamais il en existe déjà pour ce symbole
     * @param btnIndex l'indice du Bouton de transcription
     * @param btnTxt le symbole associé au Bouton de transcription
     */
    public void symbolBtnOnClick(int btnIndex, String btnTxt) {
        if (!canEdit) return;
        if (!currentIndexSelected.equals("-1") && rectMap.containsKey(currentIndexSelected)) {
            rectMap.get(currentIndexSelected).getStyleClass().remove( SELECTED_RECT_CLASS_FLAG );
        }

        currentIndexSelected = String.valueOf(btnIndex);
        currentSymbolSelected = btnTxt;

        if (!colorMap.containsKey(currentSymbolSelected)){
            String rColor = getRandomColorHex();
            colorMap.put(currentSymbolSelected, rColor);
        }        

        if (rectMap.containsKey(currentIndexSelected)){
            currentRectSelected = rectMap.get(currentIndexSelected);
            currentRectSelected.getStyleClass().add( SELECTED_RECT_CLASS_FLAG );
        }
        else 
            currentRectSelected = null;
    }

    public void setCanEdit(boolean value) { this.canEdit = value; }
}
