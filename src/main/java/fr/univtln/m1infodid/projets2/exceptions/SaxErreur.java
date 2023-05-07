package fr.univtln.m1infodid.projets2.exceptions;

public class SaxErreur extends  Exception{
    /*
    indique une erreur de syntaxe dans le document XML
     */
    public SaxErreur(){
        super("Erreur SAX lors de l'analyse du fichier XML");
    }
}
