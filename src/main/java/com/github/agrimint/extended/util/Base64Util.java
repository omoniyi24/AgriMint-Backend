package com.github.agrimint.extended.util;

import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

/**
 * @author OMONIYI ILESANMI
 */

public class Base64Util {

    public static String decryptTeamActivationKey(String key) {
        byte[] decode = Base64Utils.decodeFromString(key);
        return new String(decode, StandardCharsets.UTF_8);
    }

    public static String ecryptTeamActivationKey(String key) {
        return Base64Utils.encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }
}
