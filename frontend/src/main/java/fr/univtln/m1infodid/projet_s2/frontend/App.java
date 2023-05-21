package fr.univtln.m1infodid.projet_s2.frontend;

import lombok.extern.slf4j.Slf4j;
import fr.univtln.m1infodid.projet_s2.frontend.javafx.app.MainApp;
import javafx.application.Application;

@Slf4j
public class App {
	public static void main(String[] args) {
		Application.launch(MainApp.class);
		log.info(Facade.afficherUtilisateurs().toString());
	}

}
