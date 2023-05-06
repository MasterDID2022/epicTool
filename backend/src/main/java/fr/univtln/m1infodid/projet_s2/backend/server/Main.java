package fr.univtln.m1infodid.projet_s2.backend.server;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Class lanceur du serveur REST en contact avec la BD
 */
@Slf4j

public class Main {
    public static final String BASE_URI = "http://127.0.0.1:8080/api/";

    /**
     * Lance le serveur de l'API rest
     *
     * @param args
     */
    public static void main ( String[] args ) {
        final ResourceConfig rc = new ResourceConfig().packages("fr.univtln.m1infodid.projet_s2.backend.server");
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        log.info("l'API rest est active <C-c> pour la fermer");
    }
}