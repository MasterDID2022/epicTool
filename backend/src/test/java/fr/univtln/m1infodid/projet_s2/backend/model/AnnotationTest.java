package fr.univtln.m1infodid.projet_s2.backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationTest {
    @Test
    void retourneAnnotation(){
        assertEquals(Annotation.of(1,1).getClass(),Annotation.class);
    }

    @Test
    void ajoutPointTest(){
        Annotation annotation = Annotation.of(1,1);
        int avantChangement = annotation.getListCoordonesPoints().size();
        annotation.addPoints(1,1);
        assertEquals(avantChangement+1,annotation.getListCoordonesPoints().size());
    }

    @Test
    void afficheAnnotation(){
        Annotation annotation = Annotation.of(1);
        assertEquals("Annotation{idAnnotation=0, idEpigraphe=1, listCoordonesPoints=[]}",annotation.toString());
    }


}