package fr.univtln.m1infodid.projet_s2.backend;

import java.util.Date;

public class Epigraphe {
    private int id;
    private String imgUrl ;
    private Date date;
    private String texte;
    private String traduction;
    private String nom;
    public static Epigraphe off(int id, Date date, String texte, String traduction, String nom, String imgUrl) {
        return new Epigraphe(id, date, texte, traduction, nom, imgUrl);
    }
    private Epigraphe(int id, Date date, String texte, String traduction, String nom, String imgUrl) {
        this.id = id;
        this.date = date;
        this.texte = texte;
        this.traduction = traduction;
        this.nom = nom;
        this.imgUrl = imgUrl;
    }
    private Epigraphe() {
    }
    public static Epigraphe of() {
        return new Epigraphe();
    }
    // Getters and setters

    public int getId() {
        return id;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
    @Override
    public String toString() {
        return "Epigraphie : n°" + getId() + ", " + getNom() + ", " + getTexte()+ ", " + getDate()+ ", " + getImgUrl()+ ", " + getTraduction();
    }
}

