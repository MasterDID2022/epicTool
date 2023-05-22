package fr.univtln.m1infodid.projet_s2.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    @Column(unique = true)
    @Email(regexp = "A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")
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

    @Override
    public String toString() {
        return "Demande inscription: n°" + id +
                " pour le visiteur " + prenom + " " + nom +
                " d'adresse mail " + email +
                " et d'affiliation " + affiliation +
                ".\n" + commentaire;
    }

}