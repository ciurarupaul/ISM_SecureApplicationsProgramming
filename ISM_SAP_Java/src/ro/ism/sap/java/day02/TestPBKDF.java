package ro.ism.sap.java.day02;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class TestPBKDF {

    public static byte[] getPBKDF(String password, byte[] salt, int noIterations, int outputSize) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory pbkdf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec algSpec = new PBEKeySpec(password.toCharArray(), salt, noIterations, outputSize);

        double tStart = System.currentTimeMillis();
        byte[] result = pbkdf.generateSecret(algSpec).getEncoded();
        double tFinal = System.currentTimeMillis();

        System.out.println("PBKDF generated in: " + (tFinal - tStart) + "ms");
        return result;

    }

    public static void main (String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] userJohnHash = getPBKDF("12345678", "ashgwer".getBytes(), 100_000, 80);      // 105ms
        byte[] userAnnaHash = getPBKDF("12345678", "wrfsefk".getBytes(), 1_000_000, 80);    // 325ms
        byte[] userFayeHash = getPBKDF("12345678", "pwqvyad".getBytes(), 2_000_000, 80);    // 515ms
        byte[] userCarlHash = getPBKDF("12345678", "kavtlla".getBytes(), 5_000_000, 80);    // 1156ms
    }
}
