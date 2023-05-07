package fr.univtln.m1infodid.projet_s2.backend.exceptions;

public class ExtractionXml extends Exception{
    public ExtractionXml(){
        super("Une erreur est survenue lors de l'extraction des données du fichier XML");
    }
}
