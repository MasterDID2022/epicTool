package fr.univtln.m1infodid.projet_s2.backend.model;

import fr.univtln.m1infodid.projet_s2.backend.SI;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idAnnotation;

    @ManyToOne
    @JoinColumn(name = "idEpigraphe")
    private Epigraphe epigraphe;
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
        try {
            this.epigraphe = SI.CreateEpigraphie(idEpigraphe);
            this.listCoordonesPoints = new ArrayList<>();
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not get Epigraph");
        }
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
                ", idEpigraphe=" +getEpigraphe().getId() +
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