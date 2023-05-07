package fr.univtln.m1infodid.projet_s2.backend.exceptions;


public class ListeVide extends Exception{
    public ListeVide(){
        super("Liste vide, impossible d'instancier une Epigraphie !");
    }
}
