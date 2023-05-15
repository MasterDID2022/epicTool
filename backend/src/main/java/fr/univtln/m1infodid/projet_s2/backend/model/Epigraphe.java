package fr.univtln.m1infodid.projet_s2.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "EPIGRAPHE")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Epigraphe {
    @Id
    @Column(name = "ID")
    private int id;
    private String imgUrl;
    private Date date;
    private LocalDate fetchDate;
    private String translation;
    private String name;
    @ElementCollection
    private List<String> text;

    @Override
    public boolean equals ( Object o ) {
        if (this == o) return true;
        if(!(o instanceof Epigraphe epigraphe)) return false;
        return this.id == epigraphe.id;
    }

    @Override
    public int hashCode () {
        return getClass().hashCode();
    }

    public String toString () {
        return "Epigraphie : n°" + getId() + ", " + getName() + ", " + getText() + ", " + getDate() + ", " + getImgUrl() + ", " + getTranslation();
    }
}