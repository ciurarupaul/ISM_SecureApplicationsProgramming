package ro.ism.sap.java.day03.authenticated_encryption;

import ro.ism.sap.java.utils.Utility;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class TestChaChaPoly {

    public static byte[] encrypt(byte[] data, byte[] key, byte[] nounce, String aadValue)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        if(key == null || key.length != 32) {       // 256/8
            throw new IllegalArgumentException("key");
        }
        if(nounce == null || nounce.length != 12) { // 96/8
            throw new IllegalArgumentException("nounce");
        }

        Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key, "ChaCha20-Poly1305"),
                new IvParameterSpec(nounce));       // keep using the IvParameterSpec for the nounce

        // ------------------------------------------------
        // --------  MANDATORY, ADD THE AAD VALUE  --------
        // ------------------------------------------------
        cipher.updateAAD(aadValue.getBytes());

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key, byte[] nounce, String aadValue)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        if(key == null || key.length != 32) {       // 256/8
            throw new IllegalArgumentException("key");
        }
        if(nounce == null || nounce.length != 12) { // 96/8
            throw new IllegalArgumentException("nounce");
        }

        Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(key, "ChaCha20-Poly1305"),
                new IvParameterSpec(nounce));

        cipher.updateAAD(aadValue.getBytes());

        return cipher.doFinal(ciphertext);
    }

    public static void main(String[] args)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String transaction = "Send 100 to John";
        byte[] nounce = new byte[12];       // PREDEFINED VALUE; ALWAYS USE 12Bytes FOR CHACHA
        new SecureRandom().nextBytes(nounce);
        String messageTag = "XBank v1";

        // TODO: derive using HKDF a new key from the master one
        // generate a random key
        //      != generate a random sequence
        KeyGenerator kg = KeyGenerator.getInstance("ChaCha20");
        kg.init(256);   // in bits, not bytes
        byte[] key = kg.generateKey().getEncoded();

        byte[] ciphertext = encrypt(transaction.getBytes(), key, nounce, messageTag);
        System.out.println("Cipher with AAD tag: " + Utility.byteToHexString(ciphertext));

        // test normal decryption
        byte[] initialData = decrypt(ciphertext, key, nounce, messageTag);
        System.out.println("Initial message: " + new String(initialData));

        // test the bitflip attack
        // flip bit 4 of byte 2 *count from 0 from left*
        ciphertext[2] = (byte) (ciphertext[2] ^ (1 << 4));
        try{
            initialData = decrypt(ciphertext, key, nounce, messageTag);
        } catch (AEADBadTagException ex){
            System.out.println("Vader altered the ciphertext :(");
        }
    }
}
