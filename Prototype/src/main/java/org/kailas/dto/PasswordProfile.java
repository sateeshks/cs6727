package org.kailas.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
    public class PasswordProfile {
        String domain;
        boolean lowerCase;
        boolean  upperCase;
        boolean digits;
        boolean symbols;
        int length =16;
        int revision =1;
        String exlude;
        List<String> rules = new ArrayList<>();



        public List<String> getRules(){
            if(isDigits())
                rules.add("digits");
            if(isLowerCase())
                rules.add("lowercase");
            if(isUpperCase())
                rules.add("uppercase");
            if(isSymbols())
                rules.add("symbols");
            return rules;
        }

    }




