package ro.ism.sap.java.utils;

public class Utiliy {
     public static String getHexString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for(byte byteValue : value) {
            sb.append(String.format("%02X ", byteValue));   // we choose to have '0' displayed with two digits and a space: "00 00"
        }
        return sb.toString();
    }
}
