package com.kailas.dpm.util;

import com.kailas.dpm.entities.AppUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilTest {

    @Test
    void generateToken() throws Exception {
        AppUser appUser = new AppUser();
        TokenUtil tokenUtil = new TokenUtil("87737BC61EEE29B01EFD1187B58381C496B4185C79F865B4FA93191101C370A8", 60L);
        tokenUtil.generateToken(appUser);
    }
}