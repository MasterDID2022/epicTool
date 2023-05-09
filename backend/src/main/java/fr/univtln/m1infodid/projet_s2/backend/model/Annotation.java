package fr.univtln.m1infodid.projet_s2.backend.model;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
public class Annotation {
    @Id
    @Generated
    @Column(name = "ID")
    private int idAnnotation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Epigraphe epigraphe;
    private int idEpigraphe;
    @PostLoad
    void postload() {
        if (epigraphe != null)  idEpigraphe= epigraphe.getId();
    }
    public Annotation () {
    }
    @ElementCollection
    private List<Point> listCoordonesPoints;

    /**
     * Factory annotation
     * @param idAnnotation de epicherchell
     * @param idEpigraphe du system
     * @deprecated  Should not be used, the idAnnotation is ignored.
     * @return
     */
    @Deprecated(forRemoval = true)
    public static Annotation of(Integer idAnnotation,Integer idEpigraphe){
        return new Annotation(idEpigraphe);
    }
    public static Annotation of(int idEpigraphe){
        return new Annotation(idEpigraphe);
    }
    public static Annotation of(Epigraphe epigraphe){
        return new Annotation(epigraphe.getId());
    }

    /**
     * Construteur privée pour la facotry
     * @param idEpigraphe
     */

    private Annotation(int idEpigraphe){
        this.idEpigraphe = idEpigraphe;
        this.listCoordonesPoints = new ArrayList<>();
    }


    /**
     * Methode pour ajouter un couple de points a une annotation
     * @param x
     * @param y
     */
    public void addPoints(double x, double y){
       this.listCoordonesPoints.add(new Point(x,y));
    }


    @Override
    public String toString() {
        return "Annotation{" +
                "idAnnotation=" + idAnnotation +
                ", idEpigraphe=" + idEpigraphe +
                ", listCoordonesPoints=" + listCoordonesPoints +
                '}';
    }

    @Override
    public boolean equals ( Object o ) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Annotation that = (Annotation) o;
        return  Objects.equals(getIdAnnotation(), that.getIdAnnotation());
    }

    @Override
    public int hashCode () {
        return getClass().hashCode();
    }
}