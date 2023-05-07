package fr.univtln.m1infodid.projets2;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import fr.univtln.m1infodid.projets2.Facade;

/**
 * Hello world!
 */
@Slf4j
public class App {

    public static void main(String[] args) throws Exception {
        String id = "42";
        String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?d=" + id;
        /*ArrayList<String> content = SI.extractTextAndImageFromXml(id, xmlUrl);
        Epigraphe epigraphe = SI.CreateEpigraphie(content);
        System.out.println(epigraphe);*/

        // l'appelle de la méthode createEpigraphieInstanceFromXml de la classe Façade
        Facade facade = new Facade();
        Epigraphe epigraphe2 = facade.createEpigraphieInstanceFromXml(id, xmlUrl);
        System.out.println(epigraphe2);


    }
}
