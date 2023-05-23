package fr.univtln.m1infodid.projet_s2.frontend.javafx.controller.epigraphie;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ListeAnnotationData {
    private int idEpigraphe;
    private String email;
    private Map<Integer,List<Double>> annotations;


}
