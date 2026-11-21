package ro.ism.sap.java.day02;

import ro.ism.sap.java.utils.Utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestSecureRandom {
    public static byte[] getRandomValue(int numberOfBytes) throws NoSuchAlgorithmException {
        // create a default PRNG - SHA1PRNG
        SecureRandom secureRandom = new SecureRandom();
        // alternative - you choose the algorithm
        SecureRandom secureRandom2 = SecureRandom.getInstance("DRBG");

        byte[] result = new byte[numberOfBytes];
        secureRandom.nextBytes(result);

        return result;
    }

    public static byte[] getRandomValueWithSeed(int numberOfBytes, byte[] seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);

        byte[] result = new byte[numberOfBytes];
        secureRandom.nextBytes(result);

        return result;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // use the PRNG to generate a DES key - 64 bits/8B
        byte[] desKey = getRandomValue(8);
        System.out.println("DES random key: " + Utility.getHexString(desKey));

        // test with seed
        String seedSecret = "ismsecret";
        byte[] authKey = getRandomValueWithSeed(8, seedSecret.getBytes());
        System.out.println("DES random key: " + Utility.getHexString(authKey));
    }
}
