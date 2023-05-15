package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.exceptions.*;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Facade {
    /**
     * @param id     l'id de la fiche
     * @param xmlUrl l'url de la fiche
     * @return une instance de la classe Epigraphe contenant les informations extraites de la fiche XML correspondante
     */


    public Epigraphe createEpigraphieInstanceFromXml ( String id, String xmlUrl ) {

        List<List<String>> contentList = new ArrayList<>();
        try {
            contentList = SI.extractContentFromXML(id, xmlUrl);
        } catch (UrlInvalide u) {
            log.info(u.getMessage());
        } catch (SaxErreur s) {
            log.info(s.getMessage());
        } catch (DomParser d) {
            log.info(d.getMessage());
        } catch (ExtractionXml e) {
            log.info(e.getMessage());
        } catch (RecuperationXml r) {
            log.info(r.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Epigraphe ep = new Epigraphe();
        try {
            ep = SI.CreateEpigraphie(contentList);

        } catch (ListeVide l) {
            log.info(l.getMessage());
        }
        return ep;
    }


}
