package ro.ism.sap.java.day03.authenticated_encryption;

import ro.ism.sap.java.utils.Utility;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestAESGCM {

    public static byte[] encrypt(byte[] data, byte[] nounce, byte[] key, String aadValue)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(128, nounce));
        cipher.updateAAD(aadValue.getBytes());

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] nounce, byte[] key, String aadValue)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(128, nounce));
        cipher.updateAAD(aadValue.getBytes());

        return cipher.doFinal(data);
    }

    public static void main(String[] args)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String message = "Send 100 to John";
        String key = "passwordpassword";
        byte[] nounce = new byte[16];
        new SecureRandom().nextBytes(nounce);

        byte[] ciphertext = encrypt(message.getBytes(), nounce, key.getBytes(), "ISM v1");
        System.out.println("Cipher: " + Utility.byteToHexString(ciphertext));

        byte[] initial = decrypt(ciphertext, nounce, key.getBytes(), "ISM v1");
        System.out.println("Initial: " + Utility.byteToHexString(initial));

        try {
            initial = decrypt(ciphertext, nounce, key.getBytes(), "ISM v2");
            System.out.println("Initial: " + Utility.byteToHexString(initial));
        } catch (AEADBadTagException ex) {
            System.out.println("The client got a message from a different protocol version");
        }

        // bitflipping
        ciphertext[0] = (byte) 0xFF;
        try {
            initial = decrypt(ciphertext, nounce, key.getBytes(), "ISM v1");
            System.out.println("Initial: " + Utility.byteToHexString(initial));
        } catch (AEADBadTagException ex) {
            System.out.println("Vader altered the ciphertext :(");
        }
    }
}
