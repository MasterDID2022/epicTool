package fr.univtln.m1infodid.projet_s2.backend.DAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class EpigrapheDAO {
    public Boolean persistID ( Integer id ) {
        log.info(id.toString());
        return Boolean.TRUE;
    }
}
