package fr.univtln.m1infodid.projet_s2.frontend.server;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Class lanceur du serveur REST en contact avec la BD
 */
@Slf4j

public class Serveur {
    private static HttpServer server;

    public static final String BASE_URI = "http://127.0.0.1:8042/api/";
    private Serveur () {
        throw new IllegalStateException("Ne doit pas être instancié");
    }

    /**
     * Lance le serveur de l'API rest
     * du front end
     */
    public static void lanceur () {
        final ResourceConfig rc = new ResourceConfig().packages("fr.univtln.m1infodid.projet_s2.frontend.server");
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        log.info("l'API rest est active.");
    }

    public static void arret () {
        server.shutdownNow();

    }
}
