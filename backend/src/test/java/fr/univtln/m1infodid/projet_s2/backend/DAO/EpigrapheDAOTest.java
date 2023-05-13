package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.SI;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


final class EpigrapheDAOTest extends EpigrapheDAOTestManager {
    @Test
    void findAllTest () {
        EpigrapheDAO epigrapheDAO = EpigrapheDAO.create(em);

        List<Epigraphe> epigrapheList;
        List<Epigraphe> epigrapheList1;
        epigrapheList = em.createQuery("SELECT e FROM Epigraphe e",Epigraphe.class).getResultList();
        epigrapheList1 = epigrapheDAO.findAll();

        assertEquals(epigrapheList1, epigrapheList);
    }

    @Test
    void findIdTest () {
        Epigraphe epigraphe = epigrapheDAO.persist(Epigraphe.of(-1,"",new Date(),LocalDate.now(),"","",""));
        assertEquals(epigraphe, epigrapheDAO.findById(-1));
    }

    @Test
    void PersistIdTest () {
        Epigraphe epigraphe = epigrapheDAO.persist(Epigraphe.of(-1,"",new Date(),LocalDate.now(),"","",""));
        assertEquals(epigraphe, epigrapheDAO.findById(-1));
    }

    @Test
    void removeTestId () {
        final List<Epigraphe> size_before = epigrapheDAO.findAll();
        epigrapheDAO.persist(Epigraphe.of(-3,"",new Date(),LocalDate.now(),"","",""));
        assertDoesNotThrow(() -> epigrapheDAO.removeById(-3));
        assertEquals(size_before, epigrapheDAO.findAll());
    }

    @Test
    void removeTestEpigraphe()
    {
        List<Epigraphe> listBefore = epigrapheDAO.findAll();
        Epigraphe epigraphe = Epigraphe.of(-3,"Exemple",new Date(), LocalDate.now(),"Exemple",
                "Exemple","Exemple");
        epigrapheDAO.persist(epigraphe);
        assertDoesNotThrow(() -> epigrapheDAO.remove(epigraphe));
        assertEquals(listBefore, epigrapheDAO.findAll());
    }

    @Test
    void findByIdCacheTest() throws Exception {
        Epigraphe epigraphe = SI.CreateEpigraphie(4);
        epigraphe.setFetchDate(LocalDate.now().minusDays(3));
        epigrapheDAO.persist(epigraphe);
        assertNotEquals(epigrapheDAO.findById(4),epigraphe);
        assertDoesNotThrow(()->epigrapheDAO.removeById(4));
    }



}
