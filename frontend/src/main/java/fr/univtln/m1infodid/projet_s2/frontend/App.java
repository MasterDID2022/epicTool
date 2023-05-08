package fr.univtln.m1infodid.projet_s2.frontend;

import fr.univtln.m1infodid.projet_s2.frontend.javafx.app.MainApp;
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
