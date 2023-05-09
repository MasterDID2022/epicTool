package fr.univtln.m1infodid.projet_s2.backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationTest {
    @Test
    void shouldReturnAnnotation(){
        assertEquals(Annotation.of(1,1).getClass(),Annotation.class);
    }

    @Test
    void shouldAddPoint(){
        Annotation annotation = Annotation.of(1,1);
        int lenAvantChangement = annotation.getListCoordonesPoints().size();
        annotation.addPoints(1,1);
        assertEquals(lenAvantChangement+1,annotation.getListCoordonesPoints().size());
    }

    @Test
    void shouldPrintAnnotation(){
        Annotation annotation = Annotation.of(1,1);
        assertEquals("Annotation{idAnnotation=1, idEpigraphe=1, listCoordonesPoints=[]}",annotation.toString());
    }


}