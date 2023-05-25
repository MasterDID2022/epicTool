package fr.univtln.m1infodid.projet_s2.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Polygone {
    @NotNull
    private Polygon polygon;
    @Transient
    private Coordinate centre;
    @Transient
    private double height;
    @Transient
    private double width;

    private Polygone ( double x, double y, double width, double height ) {

        this.centre = new Coordinate(x, y);
        this.height = height;
        this.width = width;
        polygon = createPolygon(centre, width, height);
    }

    public static Polygone create ( double x, double y, double width, double height ) {
        return new Polygone(x, y, width, height);
    }

    @JsonCreator
    public static Polygone create ( List<Double> parameters ) {
        if (parameters.size() != 4) {
            throw new IllegalArgumentException("Invalid parameters for creating Polygone");
        }
        return new Polygone(parameters.get(0), parameters.get(1), parameters.get(2), parameters.get(3));
    }

    @PostLoad
    private void preCompute () {
        Coordinate[] coordinates = polygon.getCoordinates();
        if (coordinates.length == 5) {
            // 5 points (4 coins + 1er coin pour fermer le poly)
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            // Calcul des tailles sans considérer l'ordre
            /*
           h ->  xmin,ymin --------------- xmax, ymin
           e    |                                |
           i    |                                |
           g    |       centre (xmin+xmax/2)     |
           h    |              ymin+ymax)/2      |
           t    |                                |
           | ->  xmin, ymax ------------ xmax, xmax
                 | - - - - w i d t h - - - - - - |
             */
            for (int i = 0; i < 4; i++) {
                Coordinate coordinate = coordinates[i];
                if (coordinate.x < minX) {
                    minX = coordinate.x;
                }
                if (coordinate.x > maxX) {
                    maxX = coordinate.x;
                }
                if (coordinate.y < minY) {
                    minY = coordinate.y;
                }
                if (coordinate.y > maxY) {
                    maxY = coordinate.y;
                }
            }

            this.centre = new Coordinate((minX + maxX) / 2, (minY + maxY) / 2);
            this.height = Math.abs(maxY - minY);
            this.width = Math.abs(maxX - minX);
        }
    }

    private Polygon createPolygon ( Coordinate centre, double width, double height ) {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(4);
        shapeFactory.setCentre(centre);
        shapeFactory.setWidth(width);
        shapeFactory.setHeight(height);
        return shapeFactory.createRectangle();
    }

    @JsonValue
    private List<Double> toList () {
        if (centre == null) preCompute();
        return Arrays.asList(centre.x, centre.y, width, height);
    }
}