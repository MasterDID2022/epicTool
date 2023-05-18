package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.DAO.EpigrapheDAO;
import fr.univtln.m1infodid.projet_s2.backend.exceptions.*;
import fr.univtln.m1infodid.projet_s2.backend.model.Annotation;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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

    public static Epigraphe createEpigraphie(int id) throws Exception {
        return SI.CreateEpigraphie(id);
    }
    public static void updateEpigraphe ( Annotation that, Integer id ) {
    try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU");
         EntityManager em = emf.createEntityManager();
         EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(em)) {
        that.setEpigraphe(epigrapheDAO.getEpigrapheNoCommit(id));
        that.getEpigraphe().addAnnotation(that);
        em.getTransaction().begin();
        em.merge(that.getEpigraphe());
        em.getTransaction().commit();
    } catch (Exception e) {
        throw new IllegalArgumentException("L'épigraphe" + id + "n'existe pas");
    }
}

}
