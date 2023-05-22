package fr.univtln.m1infodid.projet_s2.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(name = "FORMULAIRE")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(staticName = "of")
public class  Formulaire {
    @Id
    @Column(name = "ID_FORMULAIRE")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nom;
    private String prenom;

    @Column(unique=true)
    private String email;
    private String mdp;
    private String affiliation;
    private String commentaire;

    private Formulaire(String nom, String prenom, String email, String mdp, String affiliation, String commentaire) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.affiliation = affiliation;
        this.commentaire = commentaire;
    }

    public static Formulaire of(String nom, String prenom, String email, String mdp, String affiliation, String commentaire) {
        return new Formulaire(nom,prenom,email,mdp,affiliation,commentaire);
    }

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