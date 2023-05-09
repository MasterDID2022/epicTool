package fr.univtln.m1infodid.projet_s2.backend.exceptions;


public class RecuperationXml extends Exception {
    public RecuperationXml () {
        super("Erreur d'E/S lors de la récupération du fichier XML");
    }
}
