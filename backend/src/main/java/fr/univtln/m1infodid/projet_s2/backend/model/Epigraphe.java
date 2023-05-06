package fr.univtln.m1infodid.projet_s2.backend.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Date;

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
    private String imgUrl ;
    private Date date;
    private String translation;
    private String name;
    private String text;

    @Override
    public boolean equals ( Object o ) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Epigraphe epigraphe = (Epigraphe) o;
        return this.id == epigraphe.id;
    }

    @Override
    public int hashCode () {
        return getClass().hashCode();
    }
    public String toString() {
        return "Epigraphie : n°" + getId() + ", " + getName() + ", " + getText()+ ", " + getDate()+ ", " + getImgUrl()+ ", " + getTranslation();
    }
}