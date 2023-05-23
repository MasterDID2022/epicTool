package fr.univtln.m1infodid.projet_s2.backend.model;

import com.fasterxml.jackson.annotation.*;
import fr.univtln.m1infodid.projet_s2.backend.Facade;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "idEpigraphe", "iduser" }) })
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private int idAnnotation;

    @ManyToOne
    @JoinColumn(name = "idEpigraphe",nullable = false)
    @JsonIgnore
    private Epigraphe epigraphe;
    @ManyToOne
    @JoinColumn(name="iduser")
    @JsonIgnore
    private Utilisateur utilisateur;
    @JsonProperty("email")
    public String getEmail () {
        return utilisateur != null ? utilisateur.getEmail() : "test@test.fr";
    }
@JsonProperty("idEpigraphe")
    public int getIdEpigraphe () {
        return epigraphe.getId();
    }

    @ElementCollection
    @Column(nullable = false)
    private Map<Integer,Polygone> listCoordonesPoly = new HashMap<>();
    @JsonCreator
    public Annotation(@JsonProperty("idEpigraphe") Integer epiId,
                      @JsonProperty("email") String userEmail,
                      @JsonProperty("listePoly") Map<Integer,Polygone> listpoly) {
        this.listCoordonesPoly = listpoly;
        Facade.updateEpigraphe(this,epiId,userEmail);
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
            this.epigraphe = SI.createEpigraphie(idEpigraphe);
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
        if (o == null || getClass() != o.getClass()) return false;
        Annotation that = (Annotation) o;
        return this.idAnnotation == that.idAnnotation ||
                Objects.equals(epigraphe, that.epigraphe) && Objects.equals(utilisateur, that.utilisateur);
    }

    @Override
    public int hashCode () {
        return Objects.hash(epigraphe, utilisateur);
    }
}