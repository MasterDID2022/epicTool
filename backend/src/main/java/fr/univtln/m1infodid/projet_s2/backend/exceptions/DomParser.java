package fr.univtln.m1infodid.projet_s2.backend.exceptions;

public class DomParser extends Exception{
    /*
    levée lors de la création d'un objet DocumentBuilder dans le cadre du traitement des documents XML par un parseur
     */
    public DomParser(){
        super("Erreur de configuration du parseur DOM");
    }
}
