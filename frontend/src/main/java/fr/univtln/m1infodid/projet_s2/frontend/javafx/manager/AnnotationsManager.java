package fr.univtln.m1infodid.projet_s2.frontend.javafx.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.scene.image.ImageView;
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
    public void initialize(Pane paneCanvas, ImageView imgView) { 
        this.paneCanvas = paneCanvas;
        this.imgView = imgView;

        reset();
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
        paneCanvas.getChildren().clear();
    }

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

    private void onDragEnter(double x, double y) { createSymbolSelection(x, y); }
    private void onDragOver(double x, double y) { updateRectSize(x, y); }
    private void onDragDone(double x, double y) { updateRectSize(x, y); }

    /**
     * Crée un nouveau Rectangle d'annotation si il n'en existe pas pour le symbole selectionné
     * @param x Coordonnée X de la souris
     * @param y Coordonnée Y de la souris
     */
    private void createSymbolSelection(double x, double y) {
        Rectangle r;
        if (currentRectSelected != null && !rectMap.isEmpty() && rectMap.get(currentIndexSelected) == currentRectSelected)
            r = rectMap.get(currentIndexSelected);
        else {
            r = new Rectangle();
            r.setId("annotationRect");
            r.getStyleClass().add( SELECTED_RECT_CLASS_FLAG );
            
            String rectColor = colorMap.get(currentSymbolSelected);
            r.setStyle("-fx-stroke:" + rectColor + "; -fx-fill:" + rectColor + ";");

            rectMap.put(currentIndexSelected, r);
            paneCanvas.getChildren().add(r);
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
}
