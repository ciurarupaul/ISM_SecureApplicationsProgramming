package ro.ism.sap.java.utils;

import java.util.Base64;

public class Utility {

    public static String getHexString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for(byte byteValue : value) {
            sb.append(String.format("%02X ", byteValue));   // we choose to have '0' displayed with two digits and a space: "00 00"
        }
        return sb.toString();
    }

    // -------------------------------------- day03

    public static String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(byte value : data) {
            sb.append(String.format("%02X ", value));
        }
        return sb.toString();
    }

    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }
}
