package fr.univtln.m1infodid.projet_s2.frontend.javafx.app;

import fr.univtln.m1infodid.projet_s2.frontend.Facade;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.SceneType;
import fr.univtln.m1infodid.projet_s2.frontend.server.Serveur;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Classe principale pour démarrer l'appli javaFX
 */
public class MainApp extends Application {


    @Override
    public void start ( Stage primaryStage ) throws Exception {
        Platform.runLater(Serveur::lanceur);
        //chargement des polices d'écriture pour l'application
        Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Product-Sans-Regular.ttf"), 10);
        Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Product-Sans-Bold.ttf"), 10);
        Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Product-Sans-Italic.ttf"), 10);

        //changement de scène vers la vue du menu 
        Facade.initStage(primaryStage);
        Facade.showScene(SceneType.MENU);

        //définition du titre de la fenêtre de l'application puis la montre à l'écran
        primaryStage.setTitle("Projet Épigraphie S2");
        primaryStage.setResizable(false); //?
        primaryStage.show();
    }

    @Override
    public void stop () {
        Platform.runLater(Serveur::arret);
    }

}
