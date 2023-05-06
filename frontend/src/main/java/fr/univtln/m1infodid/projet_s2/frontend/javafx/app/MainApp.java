package fr.univtln.m1infodid.projet_s2.frontend.javafx.app;

import fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.SceneController;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Classe principale pour démarrer l'appli javaFX
 */
public class MainApp extends Application {

    @Override
    public void start ( Stage primaryStage ) throws Exception {

        //chargement des polices d'écriture pour l'application
        Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Product-Sans-Regular.ttf"), 10);
        Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Product-Sans-Bold.ttf"), 10);

        //changement de scène vers la vue du menu 
        SceneController.switchToMenu(primaryStage);

        //définition du titre de la fenêtre de l'application puis la montre à l'écran
        primaryStage.setTitle("Projet Épigraphie S2");
        primaryStage.show();
    }

}
