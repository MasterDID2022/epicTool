package fr.univtln.m1infodid.projets2.Exceptions;

public class SaxErreur extends  Exception{
    public SaxErreur(){
        super("Erreur SAX lors de l'analyse du fichier XML");
    }
}
