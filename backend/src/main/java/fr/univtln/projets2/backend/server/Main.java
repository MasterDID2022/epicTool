package fr.univtln.projets2.backend.server;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Class lanceur du serveur REST en contact avec la BD
 */
@Slf4j

public class Main {
	public static final String BASE_URI = "http://0.0.0.0:8080/api/";

	/**
	 * Lance le serveur HTTP pour communiquer avec l'API
	 * 
	 * @return serveur HTTP
	 */
	public static HttpServer startServer() {
		final ResourceConfig rc = new ResourceConfig().packages("fr.univtln.projets2.backend.server");
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	/**
	 * Lance le serveur
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final HttpServer server = startServer();
		log.info("l'API rest est active <C-c> pour la fermer");
	}
}
