package fr.univtln.m1infodid.projets2;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;

/**
 * Hello world!
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        String id = "42";
        String xmlUrl = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=" + id;
        Epigraphe epi = SI.extractTextAndImageFromXml(id, xmlUrl);
        System.out.println(epi);

    }
}
