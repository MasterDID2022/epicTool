package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

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
        Epigraphe epigraphe = epigrapheDAO.persistID(-1);
        assertEquals(epigraphe, epigrapheDAO.findById(-1));
    }

    @Test
    void PersistIdTest () {
        Epigraphe epigraphe = epigrapheDAO.persistID(-1);
        assertEquals(epigraphe, epigrapheDAO.findById(-1));

    }

    @Test
    void removeTest () {
        epigrapheDAO.persistID(-3);
        final int size_before = epigrapheDAO.findAll().size();
        assertDoesNotThrow(() -> epigrapheDAO.remove(-3));
        assertEquals(size_before-1,epigrapheDAO.findAll().size());
    }


}
