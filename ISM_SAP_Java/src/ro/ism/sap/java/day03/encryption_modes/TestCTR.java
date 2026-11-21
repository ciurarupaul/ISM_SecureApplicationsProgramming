package ro.ism.sap.java.day03.encryption_modes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestCTR {


    //
    //  There is some issue with this implementation; check repo
    //  https://github.com/catalinboja/ism-sap-2026
    //


    public static void encrypt(byte[] key, String algorithm, String inputFilePath, String outputFilePath)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        File input = new File(inputFilePath);
        File output = new File(outputFilePath);

        if(!input.exists()) throw new FileNotFoundException("NO input file!");
        if(!output.exists()) output.createNewFile();

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);

        // create a cipher object
        Cipher cipher = Cipher.getInstance(algorithm + "/CTR/NoPadding");

        byte[] IV = new byte[cipher.getBlockSize()];    // we use an all 0 IV
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm), new IvParameterSpec(IV));

        byte[] block = new byte[cipher.getBlockSize()];
        int onOfBytesWeGot = 0;

        while ((onOfBytesWeGot = fis.read(block)) > 0) {    // remember -1 is returned when you're past eof
            // encrypt current block
            byte[] cipherBlock = cipher.update(block, 0, onOfBytesWeGot);
            fos.write(cipherBlock);
        }

        // don't forget the last one
        fos.write(cipher.doFinal());
        fis.close();
        fos.close();
    }

    public static void decrypt(byte[] key, String algorithm, String inputFilePath, String outputFilePath)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        File input = new File(inputFilePath);
        File output = new File(outputFilePath);

        if(!input.exists()) throw new FileNotFoundException("NO input file!");
        if(!output.exists()) output.createNewFile();

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);

        Cipher cipher = Cipher.getInstance(algorithm + "/CTR/NoPadding");
        byte[] IV = new byte[cipher.getBlockSize()];    // all 0 IV
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm), new IvParameterSpec(IV));

        int noBytes = 0;
        byte[] block = new byte[cipher.getBlockSize()];
        while((noBytes = fis.read(block)) > 0) {
            byte[] plaintext = cipher.update(block, 0, noBytes);
            fos.write(plaintext);
        }

        fos.write(cipher.doFinal());
        fis.close();
        fos.close();
    }

    public static void main(String[] args)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String key = "passwordism12345";
        encrypt(key.getBytes(), "AES", "message.txt", "message.ctr");
        decrypt(key.getBytes(), "AES", "message.ctr", "message5.txt");
        System.out.println("Done");
    }
}

// DES 8B; AES 16B for key sizes