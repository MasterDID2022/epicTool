package fr.univtln.m1infodid.projets2;

import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 */
@Slf4j
public class App {

    public static void main(String[] args) {
        String id = "42";
        String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id" + id;

        // l'appelle de la méthode createEpigraphieInstanceFromXml de la classe Façade
        Facade facade = new Facade();
        Epigraphe epigraphe2 = facade.createEpigraphieInstanceFromXml(id, xmlUrl);
        System.out.println(epigraphe2);
    }
}
