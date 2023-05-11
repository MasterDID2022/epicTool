package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/* Sert à changer de scène, cette classe doit être utilisé uniquement pour ce but */
public final class SceneController {

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
    private static <T> T sceneSwitch ( Stage stage, String url, String stylePath, int width, int height ) throws IOException {
        URL urlRef = SceneController.class.getResource(url);
        FXMLLoader fxmlLoader = new FXMLLoader(urlRef);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(stylePath);

        stage.setScene(scene);
        stage.show();

        return fxmlLoader.getController();
    }

    /**
     * Cette méthode permet de changer de scène vers le menu de l'application
     *
     * @param stage la Scène principale de l'application à modifié
     * @return le Controller de la nouvelle scène
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static <T> T switchToMenu ( Stage stage ) throws IOException {
        T controller = sceneSwitch(stage, "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/menu.fxml", "/styles/menu.css", 780, 504);
        return controller;
    }

    /**
     * Cette méthode permet de changer de scène vers la page de visualisation d'épigraphie à partir de l'id de fiche
     *
     * @param stage la Scène principale de l'application à modifié
     * @return la Scène modifié
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static <T> T switchToPageVisualisation ( Stage stage ) throws IOException {
        T controller = sceneSwitch(stage, "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/page-visualisation.fxml", "/styles/page-visualisation.css", 780, 504);
        return controller;
    }

    /**
     * Cette méthode permet de changer de scène vers la page du formulaire
     *
     * @param stage la Scène principale de l'application à modifié
     * @return la Scène modifié
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static <T> T switchToPageFormulaire ( Stage stage ) throws IOException {
        T controller = sceneSwitch(stage, "/fr.univtln.m1infodid.projet_s2.frontend.javafx.view/formulaire.fxml", "/styles/formulaire.css", 780, 504);
        return controller;
    }


}
