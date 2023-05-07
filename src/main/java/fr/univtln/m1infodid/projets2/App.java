package fr.univtln.m1infodid.projets2;

import java.util.logging.Logger;

/**
 * Hello world!
 */

public class App {
    private static java.util.logging.Logger log = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        String id = "42";
        String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=" + id;

        // l'appelle de la méthode createEpigraphieInstanceFromXml de la classe Façade
        Facade facade = new Facade();
        Epigraphe epigraphe2 = facade.createEpigraphieInstanceFromXml(id, xmlUrl);
        log.info(epigraphe2.toString());
    }
}
