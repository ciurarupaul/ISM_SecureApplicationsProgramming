package ro.ism.sap.java.day03.encryption_modes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

// the algorithm does not matter; the mode does - ecb/cbc/gsm/ctr..
public class TestECB {

    public static void encrypt(byte[] key, String algorithm, String inputFilePath, String outputFilePath)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File input = new File(inputFilePath);
        if(!input.exists()) {
            System.out.println("********* NO INPUT FILE!");
            throw new FileNotFoundException("no input file");
        }
        FileInputStream fis = new FileInputStream(input);

        File output = new File(outputFilePath);
        if(!output.exists()) {
            output.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(output);

        // create a cipher object
        Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm));    // the key MUST be exactly the size expected by the alg

        // the block size is set and different for each algorithm
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
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File input = new File(inputFilePath);
        File output = new File(outputFilePath);

        if(!input.exists()) throw new FileNotFoundException("NO input file!");
        if(!output.exists()) output.createNewFile();

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);

        Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm));

        byte[] block = new byte[cipher.getBlockSize()];
        int noBytes = 0;
        while((noBytes = fis.read(block)) > 0) {
            byte[] plaintext = cipher.update(block, 0, noBytes);
            fos.write(plaintext);
        }

        fos.write(cipher.doFinal());

        fis.close();
        fos.close();
    }

    public static void main(String[] args)
            throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String key = "passwordism12345";
        encrypt(key.getBytes(), "AES", "message.txt", "message.enc");   // file names are NOT case-sensitive
        decrypt(key.getBytes(), "AES", "message.enc", "message2.txt");
    }
}
