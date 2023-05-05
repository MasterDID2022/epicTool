package fr.univtln.m1infodid.projets2;

import java.util.Date;

public class Epigraphe {
    private static int count = 0;
    private int id;
    private Date date;
    private String texte;
    private String traduction;
    private String nom;

    public Epigraphe(Date date, String texte, String traduction, String nom) {
        this.id = ++count;
        this.date = date;
        this.texte = texte;
        this.traduction = traduction;
        this.nom = nom;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getTraduction() {
        return traduction;
    }

    public void setTraduction(String traduction) {
        this.traduction = traduction;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}

