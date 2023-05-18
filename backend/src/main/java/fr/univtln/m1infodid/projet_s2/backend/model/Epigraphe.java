package fr.univtln.m1infodid.projet_s2.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "EPIGRAPHE")
@Getter
@Setter
public class Epigraphe {
    @Id
    @Column(name = "ID")
    private int id;
    private String imgUrl;
    private Date date;
    @JsonIgnore
    private LocalDate fetchDate;
    private String translation;
    private String name;
    @ElementCollection
    private List<String> text;
    @OneToMany(mappedBy = "epigraphe", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Annotation> annotations = new HashSet<>();
    private Epigraphe ( int id, String imgUrl, Date date, LocalDate fetchDate, String translation, String name, List<String> text, Collection<Annotation> annotations ) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.date = date;
        this.fetchDate = fetchDate;
        this.translation = translation;
        this.name = name;
        this.text = text;
        this.annotations.addAll(annotations);
    }
    private Epigraphe ( int id, String imgUrl, Date date, LocalDate fetchDate, String translation, String name, List<String> text) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.date = date;
        this.fetchDate = fetchDate;
        this.translation = translation;
        this.name = name;
        this.text = text;
    }

    public Epigraphe () {
    }

    public static Epigraphe of ( int id, String imgUrl, Date date, LocalDate fetchDate, String translation, String name, List<String> text, List<Annotation> annotations ) {
        return new Epigraphe(id, imgUrl, date, fetchDate, translation, name, text, annotations);
    }
    public static Epigraphe of ( int id, String imgUrl, Date date, LocalDate fetchDate, String translation, String name, List<String> text ) {
        return new Epigraphe(id, imgUrl, date, fetchDate, translation, name, text);
    }


    @Override
    public boolean equals ( Object o ) {
        if (this == o) return true;
        if(!(o instanceof Epigraphe epigraphe)) return false;
        return this.id == epigraphe.id && (fetchDate.equals(epigraphe.fetchDate));
    }

    @Override
    public int hashCode () {
        return getClass().hashCode();
    }

    public String toString () {
        return "Epigraphie : n°" + getId() + ", " + getName() + ", " + getText() + ", " + getDate() + ", " + getImgUrl() + ", " + getTranslation();
    }
    public void  addAnnotation(Annotation annotation){
        this.getAnnotations().add(annotation);
    }
}