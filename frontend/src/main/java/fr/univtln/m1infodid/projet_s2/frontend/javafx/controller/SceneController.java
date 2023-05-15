package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/* Sert à changer de scène, cette classe doit être utilisé uniquement pour ce but */
public final class SceneController {

    public record SceneData<T>(Scene scene, T controller) {}

    private SceneController () {
        throw new IllegalStateException("Ne dois pas être instancié");
    }

    /**
     * La méthode sceneSwitch effectue le changement de scène en récupérant
     * le fichier FXML de la nouvelle scène à charger puis la charger et
     * l'afficher sur l'écran de l'application
     *
     * @param stage  la Scène principale de l'application sur laquelle le changement est effectué
     * @param url    le chemin du fichier FXML à charger
     * @param width  la largeur de la nouvelle fenêtre
     * @param height la hauteur de la nouvelle fenêtre
     * @return le Controller de la nouvelle scène
     * @throws IOException renvoi une erreur si le fichier FXML n'a pas pu être chargé
     */
    private static <T> SceneData<T> sceneSwitchAndLoad( Stage stage, String url, int width, int height, String... stylePath ) throws IOException {
        URL urlRef = SceneController.class.getResource(url);
        FXMLLoader fxmlLoader = new FXMLLoader(urlRef);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().addAll(stylePath);

        stage.setScene(scene);
        stage.show();

        return new SceneData<>(scene, fxmlLoader.getController() );
    }

    private static <T> void sceneSwitch(Stage stage, SceneData<T> sceneData) {
        stage.setScene(sceneData.scene);
        stage.show();
    }

    /**
     * Cette méthode permet de changer vers une Scène en utilisant son SceneData
     *
     * @param <T> le Type de controller utilisé par SceneData
     * @param stage la Scène principale de l'application à modifié
     * @param sceneData l'objet SceneData contenant la scène et le controller d'une scène à afficher
     */
    public static <T> void switchToScene(Stage stage, SceneData<T> sceneData) {
        sceneSwitch(stage, sceneData);
    }

    /**
     * Cette méthode permet de changer de scène vers le menu de l'application
     *
     * @param stage la Scène principale de l'application à modifié
     * @return le Controller de la nouvelle scène
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static <T> SceneData<T> switchToMenu ( Stage stage ) throws IOException {
        return sceneSwitchAndLoad(stage,
                "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/menu.fxml",
                780,
                504,
                "/styles/menu.css",
                "/styles/alert.css");
    }

    /**
     * Cette méthode permet de changer de scène vers la page de visualisation d'épigraphie à partir de l'id de fiche
     *
     * @param stage la Scène principale de l'application à modifié
     * @return la Scène modifié
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static <T> SceneData<T> switchToPageVisualisation ( Stage stage ) throws IOException {
        return sceneSwitchAndLoad(stage,
                "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/page-visualisation.fxml",
                780,
                504,
                "/styles/page-visualisation.css",
                "/styles/transcription.css",
                "/styles/traduction.css",
                "/styles/alert.css");
    }

    /**
     * Cette méthode permet de changer de scène vers la page du formulaire
     *
     * @param stage la Scène principale de l'application à modifié
     * @return la Scène modifié
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static <T> SceneData<T> switchToPageFormulaire ( Stage stage ) throws IOException {
        return sceneSwitchAndLoad(stage,
                "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/formulaire.fxml",
                780,
                504,
                "/styles/formulaire.css",
                "/styles/alert.css");
    }

    public static <T> SceneData<T> switchToPageGestionFormulaire ( Stage stage ) throws IOException {
        return sceneSwitchAndLoad(stage,
                "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/gestionAdhesion/gestionFormulaire.fxml",
                780,
                504,
                "/styles/gestionFormulaire.css",
                "/styles/alert.css");
    }

    public static <T> SceneData<T> switchToPageGestionFormulairUI2 ( Stage stage ) throws IOException {
        return sceneSwitchAndLoad(stage,
                "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/gestionAdhesion/affichageDemande.fxml",
                780,
                504,
                "/styles/affichageDemande.css",
                "/styles/alert.css");
    }

}
