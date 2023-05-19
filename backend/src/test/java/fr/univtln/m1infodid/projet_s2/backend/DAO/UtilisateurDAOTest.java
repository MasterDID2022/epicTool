package fr.univtln.m1infodid.projet_s2.backend.DAO;

import fr.univtln.m1infodid.projet_s2.backend.model.Utilisateur;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UtilisateurDAOTest {
    private static Utilisateur mockUtilisateur;
    Utilisateur uti;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static  UtilisateurDAO dao;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        emf = Persistence.createEntityManagerFactory("EpiPU");
        em = emf.createEntityManager();
        dao = UtilisateurDAO.create(em);
        mockUtilisateur = Utilisateur.of("mock@mock.com","1234");
        dao.persist(mockUtilisateur);
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        em.getTransaction().begin();
        Query q= em.createQuery("DELETE FROM Utilisateur ");
        q.executeUpdate();
        em.getTransaction().commit();
        emf.close();
    }

    private Utilisateur rePeristMockUser(){
        mockUtilisateur = Utilisateur.of("mock@mock.com","1234");
        dao.persist(mockUtilisateur);
        return mockUtilisateur;
    }

    @Test
    void ShouldFindTheMockUser(){
        assertNotNull(dao.findById(mockUtilisateur.getId()));
    }

    @Test
    void ShouldRemoveTheMockUserWithID(){
        dao.remove(mockUtilisateur.getId());
        assertNull(dao.findById(mockUtilisateur.getId()));
        rePeristMockUser();
    }

    @Test
    void ShouldRemoveTheMockUserWithEmail(){
        dao.remove(mockUtilisateur.getEmail());
        assertNull(dao.findById(mockUtilisateur.getId()));
        rePeristMockUser();
    }

    @Test
    void ShouldRemoveTheMockUserWithUser(){
        dao.remove(mockUtilisateur);
        assertNull(dao.findById(mockUtilisateur.getId()));
        rePeristMockUser();
    }

    @Test
    void createAnotherMockUser(){
        uti = Utilisateur.of("null","1234");
        dao.persist(uti);
        assertNotEquals(0,uti.getId());
        dao.remove(uti.getId());
    }


    @Test
    void findAll(){
        Utilisateur userFindall= Utilisateur.of("findAll@gmail.com","ntm");
        log.info(Utilisateur.hash("1234","I5Cqt+cfO74yW7FTKW9pJLGxBNY="));
        log.info(dao.findAll().toString());
        Integer oldNumber = dao.findAll().size();
        dao.persist(userFindall);
        assertEquals(oldNumber+1,dao.findAll().size());
    }

    @Test
    void testFindByID(){
        assertNotNull(dao.findById(mockUtilisateur.getId()));
    }


    @Test
    void testFindByEmail(){
        assertTrue(dao.findByEmail(mockUtilisateur.getEmail()).isPresent());
    }

    @Test
    void ShouldNotFindByEmail(){
        assertTrue(dao.findByEmail("WRONGEMAIL@GMAIL.COM").isEmpty());
    }

}