package xmw.exa.util;

import java.util.UUID;

public class Util {
    private Util() {
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
