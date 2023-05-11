package fr.univtln.m1infodid.projet_s2.backend.server;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiTest {

    @Test
    void shouldReturnAcorretAnnotation(){
        String inputJson = "{\n" +
                "  \"idEpigraphe\":\"1\",\n" +
                "  \"idAnnotation\":\"42\",\n" +
                "  \"annotations\":{\n" +
                "    \"1\":{\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    },\n" +
                "    \"2\":{\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    },\n" +
                "    \"3\":{\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    },\n" +
                "    \"4\":{\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Api api = new Api();
        Response r = api.receiveAnnotation(inputJson);
        assertEquals(r.toString(), Response.ok().build().toString());
    }


    @Test
    void shouldNotReturnAcorretAnnotation(){
        String inputJson = "{\n" +
                "  \"idEpigraphe\":\"1\",\n" +
                "  \"idAnnotation\":\"42\",\n" +
                "  \"annotations\":{\n" +
                "    \"1\":\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    },\n" +
                "    \"2\":{\n" +
                "    },\n" +
                "    \"3\":{\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    },\n" +
                "    \"4\":{\n" +
                "      \"x\":[1,2],\n" +
                "      \"y\":[4,2]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Api api = new Api();
        Response r = api.receiveAnnotation(inputJson);
        assertNotEquals(r.toString(), Response.ok().build().toString());
    }


    @Test
    void shouldReturnAcorretFormulaire(){
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
    }


    @Test
    void shouldNotReturnAcorretFormulaire(){
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