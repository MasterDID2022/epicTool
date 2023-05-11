package fr.univtln.m1infodid.projet_s2.frontend.server;

import java.util.regex.Pattern;

/**
 * Classe de verification de la validite des entree du
 * logicielle
 */
public class Verifcation {
    private static final String NUMBER_ONLY_REGEX = "\\d+"; // [0-9]+

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    private Verifcation() {
        throw new IllegalStateException("Cette classe ne devrait pas etre instancier");
    }

    /**
     * Vérifie si l'entrée est valide selon le regex, celle ci vérifie que l'entrée
     * est bien un nombre
     *
     * @param input la chaîne de caractère à tester
     * @return le résultat du regex, boolean
     */
    public static boolean isInputOnlyInteger(String input) {
        return input.matches(NUMBER_ONLY_REGEX);
    }

    /**
     * Vérifie si l'entrée est valide selon le regex, celle ci vérifie que l'entrée
     * est bien un mail correct
     *
     * @param email la chaîne de caractère à tester
     * @return le résultat du regex, boolean
     */
    public static boolean isInputAvalideEmail(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches();
    }

}
