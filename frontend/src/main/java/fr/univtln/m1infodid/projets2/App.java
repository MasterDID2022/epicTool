package fr.univtln.m1infodid.projets2;

import fr.univtln.m1infodid.projets2.javafx.app.MainApp;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 */
@Slf4j
public class App {
    public static void main ( String[] args ) {
        log.info("Hello World!");
        Application.launch(MainApp.class); //lance l'application javaFX
    }
}
