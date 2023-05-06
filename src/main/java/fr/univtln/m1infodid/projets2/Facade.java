package fr.univtln.m1infodid.projets2;

import java.util.ArrayList;

public class Facade {
    /**
     * @param id l'id de la fiche
     * @param xmlUrl l'url de la fiche
     * @return une instance de la classe Epigraphe contenant les informations extraites de la fiche XML correspondante
     */
    public Epigraphe createEpigraphieInstanceFromXml(String id, String xmlUrl) {
        ArrayList<String> contentList = SI.extractTextAndImageFromXml(id, xmlUrl);
        return SI.CreateEpigraphie(contentList);
    }



}
