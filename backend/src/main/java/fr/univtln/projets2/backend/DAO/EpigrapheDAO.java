package fr.univtln.projets2.backend.DAO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class EpigrapheDAO {
    public Boolean persistID(Integer id){
        log.info(id.toString());
        return Boolean.TRUE;
    }
}
