package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class Facade {
    private Facade () {
        throw new IllegalStateException("Ne doit pas être instancié");
    }

    /**
     * @param id l'id de la fiche
     * @return une instance de la classe Epigraphe contenant les informations extraites de la fiche XML correspondante
     */

    public static Epigraphe createEpigraphie ( int id ) throws Exception {
        return SI.createEpigraphie(id);
    }


}
