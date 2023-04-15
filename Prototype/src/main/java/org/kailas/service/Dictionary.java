package org.kailas.service;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {
     static final Map<String, String> rules = new HashMap<String, String>() {{
        put("lowercase", "abcdefghijklmnopqrstuvwxyz");
        put("uppercase", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        put("digits", "0123456789");
        put("symbols", "!@#$%^&*()_+-=[]{}|,.<>/?\\\"';:");
    }};

}
