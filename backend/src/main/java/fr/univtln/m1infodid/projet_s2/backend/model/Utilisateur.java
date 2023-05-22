package fr.univtln.m1infodid.projet_s2.backend.model;

import fr.univtln.m1infodid.projet_s2.backend.DAO.UtilisateurDAO;

import java.nio.charset.StandardCharsets;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Entity
@Getter
@Setter
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    public enum Role {
        ANNOTATEUR,
        GESTIONNAIRE,
        DEMANDEUR,
    }

    @Email
    @Column(unique = true)
    private String email;
    private Role role;

    private String mdp;
    private String sale;
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Annotation> annotations;
    private Utilisateur(String email, String mdp) {
        generateSalt();
        this.email = email;
        this.role = Role.ANNOTATEUR;
        this.mdp = hash(mdp, this.sale);
    }

    public Utilisateur() {
    }

    public static Utilisateur of(String email, String mdp) {
        return new Utilisateur(email, mdp);
    }

    /**
     * Hash un mot de passe avec un sel, retourne une chaine de character traduite d'un byte
     *
     * @param password le mot de passe
     * @param salt     le sale
     * @return
     */
    public static String hash(String password, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 10_000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashPassword = factory.generateSecret(spec).getEncoded();
            StringBuilder passwordHex = new StringBuilder();
            for (byte hashedByte : hashPassword) {
                String hex = Integer.toHexString(0xff & hashedByte);
                if (hex.length() == 1) {
                    passwordHex.append('0');
                }
                passwordHex.append(hex);
            }
            return passwordHex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Err: methode de hash inconnue");
            return null;
        }
    }

    /**
     * Genere un sel aleatoire pour le mdp
     * @return
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] newSale = new byte[20];
        random.nextBytes(newSale);
        this.sale = Base64.getEncoder().encodeToString(newSale);
        return this.sale;
    }

    /**
     * Prend un email et retourne le role en String attaches au mail
     * @param email de l'utilisateur dont on veut le role
     * @return annotateur ou gestionnaire en String
     */
    public static Optional<String> getRoleOf(String email) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU")) {
            EntityManager em = emf.createEntityManager();
            try (UtilisateurDAO dao = UtilisateurDAO.create(em)) {
                Optional<Utilisateur> searchUserWithEmail = dao.findByEmail(email);
                if (searchUserWithEmail.isEmpty()) {
                    return Optional.empty();
                }
                Utilisateur userOfEmail = searchUserWithEmail.get();
                return Optional.of(userOfEmail.getRole().toString());
            } catch (Exception e) {
                log.error("Err: impossible d'instancier la DAO de Utilisateur");
                return Optional.empty();
            }
        }
    }

    /**
     * Prend un mot de passe en claire et un email, retrouve le sel associe a l'email, hash et verifie
     * la corespondance.
     * En cas de succe la fonction renvoie true, false sinon
     *
     * @param passwordToVerify mot de passe en claire
     * @param email            email lier au mot de passe a verifier
     * @return
     */
    public static Boolean checkPassword(String passwordToVerify, String email) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("EpiPU")) {
            EntityManager em = emf.createEntityManager();
            try (UtilisateurDAO dao = UtilisateurDAO.create(em)) {
                Optional<Utilisateur> searchUserWithEmail = dao.findByEmail(email);
                if (searchUserWithEmail.isEmpty()) {
                    return false;
                }
                Utilisateur userOfEmail = searchUserWithEmail.get();
                String hashedPassword = Utilisateur.hash(passwordToVerify, userOfEmail.getSale());
                return userOfEmail.getMdp().equals(hashedPassword);
            } catch (Exception e) {
                log.error("Err: impossible d'instancier la DAO de Utilisateur");
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "IdUser=" + String.valueOf(this.getId()) +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", sale='" + sale + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur utilisateur)) return false;
        return this.id == utilisateur.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
