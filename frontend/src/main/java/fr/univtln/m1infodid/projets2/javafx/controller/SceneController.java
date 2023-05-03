package fr.univtln.m1infodid.projets2.javafx.controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* Sert à changer de scène, cette classe doit être utilisé uniquement pour ce but */
public final class SceneController {

    private SceneController() { throw new IllegalStateException("Ne dois pas être instancié"); }
    
    /**
     * La méthode sceneSwitch effectue le changement de scène en récupérant
     * le fichier FXML de la nouvelle scène à charger puis la charger et
     * l'afficher sur l'écran de l'application
     * @param stage la Scène principale de l'application sur laquelle le changement est effectué
     * @param url le chemin du fichier FXML à charger
     * @param width la largeur de la nouvelle fenêtre
     * @param height la hauteur de la nouvelle fenêtre
     * @return la Scène principale modifié
     * @throws IOException renvoi une erreur si le fichier FXML n'a pas pu être chargé
     */
    private static Scene sceneSwitch(Stage stage, String url, int width, int height) throws IOException{
        URL urlRef = SceneController.class.getResource(url);
        FXMLLoader fxmlLoader = new FXMLLoader(urlRef);
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root, width, height);
        
        stage.setScene(scene);
        stage.show();

        return scene;
    }

    /**
     * Cette méthode permet de changer de scène vers le menu de l'application
     * @param stage la Scène principale de l'application à modifié
     * @return la Scène modifié
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static Scene switchToMenu(Stage stage) throws IOException {
        Scene scene = sceneSwitch(stage, "/fr.univtln.m1infodid.projets2.frontend.javafx.view/menu.fxml", 780, 504);
        scene.getStylesheets().add("/styles/menu.css");
        return scene;
    }

    /**
     * Cette méthode permet de changer de scène vers la page de visualisation d'épigraphie à partir de l'id de fiche
     * @param stage la Scène principale de l'application à modifié
     * @return la Scène modifié
     * @throws IOException renvoi une erreur si le fichier FXML du menu n'a pas pu être chargé
     */
    public static Scene switchToPageVisualisation(Stage stage) throws IOException {
        Scene scene = sceneSwitch(stage, "/fr.univtln.m1infodid.projets2.frontend.javafx.view/page-visualisation.fxml", 780, 504);
        scene.getStylesheets().add("/styles/page-visualisation.css");
        return scene;
    }
}
