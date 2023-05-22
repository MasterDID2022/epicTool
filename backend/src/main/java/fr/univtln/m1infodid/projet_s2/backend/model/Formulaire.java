package fr.univtln.m1infodid.projet_s2.backend.model;

import lombok.*;
import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FORMULAIRE")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(staticName = "of")
public class  Formulaire {
    @Id
    @Column(name = "ID_FORMULAIRE")
    @Generated
    private int id;
    private String nom;
    private String prenom;

    @Column(unique=true)
    private String email;
    private String mdp;
    private String affiliation;
    private String commentaire;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formulaire formulaire)) return false;
        return this.id == formulaire.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();

    }

    public String toString() {
        return "Demande inscription: n°" + getId() + " pour le visiteur " + getNom() + getPrenom() + " d'adresse mail " + getEmail() + " de mot de passe" + getMdp() + " et d'affiliation  " + getAffiliation() + ". \n" + getCommentaire();
    }
}