package fr.univtln.m1infodid.projet_s2.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Polygone {
    private double centreX;
    private double centreY;
    private double height;
    private double width;

    private Polygone ( double x, double y, double height, double width ) {

        this.centreX = x;
        this.centreY = y;
        this.height = height;
        this.width = width;
    }

    public static Polygone create ( double x, double y, double height, double width ) {
        return new Polygone(x, y, height, width);
    }

    @JsonCreator
    public static Polygone create ( List<Double> parameters ) {
        if (parameters.size() != 4) {
            throw new IllegalArgumentException("Invalid parameters for creating Polygone");
        }
        return new Polygone(parameters.get(0), parameters.get(1), parameters.get(2), parameters.get(3));
    }


    @JsonValue
    private List<Double> toList () {
        return Arrays.asList(centreX, centreY, height, width);
    }
}