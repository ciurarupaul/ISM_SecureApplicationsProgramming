package ro.ism.sap.java.day02;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class TestFiles {

    // interacting with the file system
    public static void displayContent(String path) {
        File location = new File(path);
        if (location.exists()) {
            // check if the path is a folder
            if (location.isDirectory()) {
                // show content
                File[] content = location.listFiles();
                for (File entry : content) {
                    System.out.print(entry.getName());
                    if (entry.isFile()){
                        System.out.println('\t' + " -- is a file");
                    }
                    if(entry.isDirectory()) {
                        System.out.println("--> DIRECTORY content");
                        displayContent(entry.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        displayContent("C:\\Users\\PaulCiuraru\\Downloads");

        // create a folder
        File newFolder = new File("Files");
        if(!newFolder.exists()) {
            newFolder.mkdir();
        }

        // interact with text files
        File textFile = new File(newFolder.getName() + File.separator + "Message.txt");
        if(!textFile.exists()) {
            textFile.createNewFile();
        }

        // write into text files
        FileWriter fileWriter = new FileWriter(textFile, true);       // expects a text file
        fileWriter.write("Log messages: \n");
        PrintWriter printWriter = new PrintWriter(fileWriter);  // expect a FileWriter; you may jump this step and pass directly the text file
        printWriter.println("10:00 Authorized access by admin");
        printWriter.println("11:00 Authorized access by admin");

        // you can't open a file more than once at a time; it doesn't support concurrent access
        // FileWriter can append; it's constructor also takes boolean append, which appends instead of truncating
        // the File class in Java is not the same as in C/C++, it doesn't hold a file, but rather a relation to the file system

        printWriter.close();

        // read from text files
        FileReader fileReader = new FileReader(textFile);
        // read it line by line
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.readLine();  // reads one line at a time
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            System.out.println("Line: " + line);
        }

        bufferedReader.close(); // it's the same if you close the connection to the file from the fileReader or the bufferedReader; closing it from any node of the pipeline does the job

        // ----------------------------------
        //               NewIO
        // ----------------------------------
        // the equivalent of File from the classic IO is Path

        Path path = textFile.toPath();
        ArrayList<String> content = (ArrayList<String>) Files.readAllLines(path);   // interact with the content of the file
        System.out.println("-----------------------------------------------");
        for(String row: content) {
            System.out.println(row);
        }

        // interacting with binary files
        // write into binary files
        File dataFile = new File("Files" + File.separator + "MyData.bin");
        if(!dataFile.exists()) {
            dataFile.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(dataFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);   // purpose of this is to speed up writing
        DataOutputStream dos = new DataOutputStream(bos);           // used to serialize primitives, strings
        // you may use a FilterOutputStream to filter out sensitive data e.g. logging personal information
        // ObjectOutputStream oos = new ObjectOutputStream(fos);
        // many possible nodes in the pipeline

        dos.writeInt(23);           // 4B
        dos.writeDouble(100.4);     // 8B
        dos.writeUTF("Hello, ISM");// you can determine this
        dos.writeBoolean(true);     // 1B
        dos.close();                   // 25B for the whole file

        /*
            00 00 00 17 40 59 19 99 99 99 99 9A 00 0A 48 65
            6C 6C 6F 2C 20 49 53 4D 01

            ** ** ** ** ** ** ** ** ** ** ** ** 00 0A ** **
            ** ** ** ** ** ** ** ** **
            that's how Java handles strings; you have 10 (0Ah) characters
        */

        FileInputStream fis = new FileInputStream(dataFile);
        DataInputStream dis = new DataInputStream(fis);
        int intValue = dis.readInt();
        double doubleValue = dis.readDouble();
        String stringValue = dis.readUTF(); // the system will read those 2 bytes, then read the stings for as long as those bytes say
        dis.close();

        System.out.println('\n' + stringValue);

        RandomAccessFile raf = new RandomAccessFile("Files" + File.separator + "values.bin", "rw");
        byte[] values = { 0x2f, 0x05, 0x44, 0x34 };
        raf.write(values);
        raf.close();

        raf = new RandomAccessFile("Files" + File.separator + "values.bin", "r");
        raf.seek(2);
        byte value = raf.readByte();
        raf.close();
        System.out.println("Value is: " + value);
    }
}
