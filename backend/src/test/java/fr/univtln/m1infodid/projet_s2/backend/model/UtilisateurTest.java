package fr.univtln.m1infodid.projet_s2.backend.model;

import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UtilisateurTest {
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


    @Test
    void ShouldCreateAUser(){
        Utilisateur utilisateur = Utilisateur.of("null","1234");
        assertNotNull(utilisateur);
    }

    @Test
    void ShouldHashPassword(){
        String mdp ="1234";
        String sale = "sale";
        String hashedPassword = Utilisateur.hash(mdp,sale);
        assertEquals("67763ef90bfc58f5d2286e5563f0d8b36f1eb3aa7b51e7cfae42ec0dd2a87f81",hashedPassword);
    }


    @Test
    void ShouldReturnSalt(){
        Utilisateur utilisateur = Utilisateur.of("null","1234");
        log.info(utilisateur.getSale());
        assertNotNull(utilisateur.getSale());
    }

    @Test
    void ShouldAcceptAuthentification(){
        assertTrue(Utilisateur.checkPassword("1234",mockUtilisateur.getEmail()));
    }

    @Test
    void ShouldNotAcceptAuthentificationBecauseOfIncorrectPassword(){
        assertFalse(Utilisateur.checkPassword("1111",mockUtilisateur.getEmail()));
    }

    @Test
    void ShouldNotAcceptAuthentificationBecauseOfIncorrectEmail(){
        assertFalse(Utilisateur.checkPassword("1234","mockery@mock.fr"));
    }

    @Test
    void ShouldBeEqual(){
        Utilisateur mockeUserFromBD = dao.findByEmail(mockUtilisateur.getEmail()).get();
        Utilisateur anotherMockeUserFromBD = dao.findByEmail(mockUtilisateur.getEmail()).get();
        assertEquals(mockeUserFromBD,(anotherMockeUserFromBD));
    }

    @Test
    void ShouldNotBeEqual(){
        Utilisateur mockeUserFromBD = dao.findByEmail(mockUtilisateur.getEmail()).get();
        assertNotEquals(mockeUserFromBD,("JusteAstring"));
    }

    @Test
    void ShouldPrint(){
        Utilisateur mockUtilisateur = Utilisateur.of("mock@mock.com","1234");
        assertNotNull(mockUtilisateur.toString());
    }

    @Test
    void ShouldBeHAshable(){
        Utilisateur mockUtilisateur = Utilisateur.of("mock@mock.com","1234");
        assertTrue(0 != mockUtilisateur.hashCode());
    }

}