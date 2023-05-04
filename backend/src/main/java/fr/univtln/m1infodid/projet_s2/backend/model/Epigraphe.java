package fr.univtln.m1infodid.projet_s2.backend.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@ToString
@Entity
@Table(name = "EPIGRAPHE")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Epigraphe {

    @Id
    @Setter
    @Getter
    @Column(name = "ID")
    private int id;


}
