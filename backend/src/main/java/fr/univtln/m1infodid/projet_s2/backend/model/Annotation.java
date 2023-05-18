package fr.univtln.m1infodid.projet_s2.backend.model;

import com.fasterxml.jackson.annotation.*;
import fr.univtln.m1infodid.projet_s2.backend.Facade;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idAnnotation;

    @ManyToOne
    @JoinColumn(name = "idEpigraphe",nullable = false)
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private Epigraphe epigraphe;
    @Column(nullable = false)
    @ElementCollection
    private List<Polygone> listCoordonesPoly = new ArrayList<>();


    @JsonCreator
    public Annotation(@JsonProperty("idEpigraphe") Integer id,
                      @JsonProperty("listePoly") List<Polygone> listpoly) {
        this.listCoordonesPoly = listpoly;
        Facade.updateEpigraphe(this,id);
  }

    /**
     * Factory annotation
     * @param idEpigraphe
     */

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
            this.epigraphe = Facade.createEpigraphie(idEpigraphe);
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not get Epigraph");
        }
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "idAnnotation=" + idAnnotation +
                ", idEpigraphe=" +getEpigraphe().getId() +
                ", listCoordonesPoly=" + listCoordonesPoly +
                '}';
    }

    @Override
    public boolean equals ( Object o ) {
        if (this == o) return true;
        if (!(o instanceof Annotation annotation)) return false;
        return Objects.equals(getIdAnnotation(), annotation.getIdAnnotation());
    }

    @Override
    public int hashCode () {
        return getClass().hashCode();
    }
}