package fr.univtln.m1infodid.projet_s2.backend.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode
public class Annotation {
    private Integer idAnnotation;
    private Integer idEpigraphe;
    @EqualsAndHashCode.Exclude
    private List<List<Integer>> listCoordonesPoints;

    /**
     * Factory annotation
     * @param idAnnotation de epicherchell
     * @param idEpigraphe du system
     * @return
     */
    public static Annotation of(Integer idAnnotation,Integer idEpigraphe){
        return new Annotation(idAnnotation,idEpigraphe);
    }

    /**
     * Construteur privée pour la facotry
     * @param idAnnotation
     * @param idEpigraphe
     */
    private Annotation(Integer idAnnotation, Integer idEpigraphe) {
        this.idAnnotation = idAnnotation;
        this.idEpigraphe = idEpigraphe;
        this.listCoordonesPoints = new ArrayList<>();
    }

    /**
     * Methode pour ajouter un couple de points a une annotation
     * @param x
     * @param y
     */
    public void addPoints(Integer x, Integer y){
        List<Integer> couplePoint = new ArrayList<>();
        couplePoint.add(x);couplePoint.add(y);
        this.listCoordonesPoints.add(couplePoint);
    }


    @Override
    public String toString() {
        return "Annotation{" +
                "idAnnotation=" + idAnnotation +
                ", idEpigraphe=" + idEpigraphe +
                ", listCoordonesPoints=" + listCoordonesPoints +
                '}';
    }
}
