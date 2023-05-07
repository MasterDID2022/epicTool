package fr.univtln.m1infodid.projets2;

import fr.univtln.m1infodid.projets2.Exceptions.*;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Facade {
    private static Logger log = Logger.getLogger(App.class.getName());
    /**
     * @param id l'id de la fiche
     * @param xmlUrl l'url de la fiche
     * @return une instance de la classe Epigraphe contenant les informations extraites de la fiche XML correspondante
     */

    public Epigraphe createEpigraphieInstanceFromXml(String id, String xmlUrl)  {
        ArrayList<String> contentList = null;
        try {
            contentList = SI.extractTextAndImageFromXml(id, xmlUrl);
        } catch (UrlInvalide u) {
            log.warning(u.getMessage());
        } catch (SaxErreur s) {
            log.warning(s.getMessage());
        } catch (DomParser d) {
            log.warning(d.getMessage());
        } catch (ExtractionXml e) {
            log.warning(e.getMessage());
        } catch (RecuperationXml r) {
            log.warning(r.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Epigraphe ep = Epigraphe.of();
        try {
            ep = SI.CreateEpigraphie(contentList);

        } catch (ListeVide l) {
            log.warning(l.getMessage());
        }
        return ep ;
    }



}
