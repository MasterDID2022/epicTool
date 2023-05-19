package fr.univtln.m1infodid.projet_s2.backend.server;

import fr.univtln.m1infodid.projet_s2.backend.DAO.FormulaireDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.univtln.m1infodid.projet_s2.backend.SI;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ApiTest {

    public static Api api;

    @BeforeAll
    public static void setupApi(){
        SI.setSecretKey("leNomDuChienEstLongCarC'ESTainsiqu'ILlef@ut");
        api = new Api();
        api.getNewKey();
    }


    @Test
    void shouldReturnAcorretFormulaire() {
        String inputJson = "{\n" +
                "  \"idFormulaire\":\"1\",\n" +
                "  \"nomFormulaire\":\"A\",\n" +
                "  \"prenomFormulaire\":\"B\",\n" +
                "  \"emailFormulaire\":\"test@gmail.com\",\n" +
                "  \"affiliationFormulaire\":\"prof\",\n" +
                "  \"commentaireAffiliation\":\"none\"" +
                "}";

        Api api = new Api();
        Response r = api.receiveFormulaire(inputJson);
        assertEquals(r.toString(), Response.ok().build().toString());
        FormulaireDAO.deleteFormulaire(1);
    }

    @Test
    void shouldCleanAnnotaionFromToken() {
        Api api = new Api();
        String json = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiYW5ub3RhdGV1ciIsImV4cCI6MTY4NDY1OTQ3Mn0.XX8oNeS8HOpLYiUyXEOKIk03DV9DbBZVkVvKB7_O-Ew\",\"idEpigraphe\":\"43\",\"listePoly\":[[38.0,66.0,54.0,68.0],[135.0,197.0,31.0,27.0]]}";
        api.cleanAndVerifyTokenOf(json);
    }


    void shouldNotReturnAcorretFormulaire() {
        String inputJson = "{\n" +
                "  \"idFormulaire\":\"1\",\n" +
                "  \"nomFormulaire\":\"A\",\n" +
                "  \"prenomFormulaire\":\"B\",\n" +
                "  \"emailFormulaire\":\"test@gmail.com\",\n" +
                "  \"affiliationFormulaire\":\"prof\"\n" +
                "  \"commentaireAffiliation\":\"none\"" +
                "}";
        Api api = new Api();
        Response r = api.receiveFormulaire(inputJson);
        assertNotEquals(r.toString(), Response.ok().build().toString());
    }



}