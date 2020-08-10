import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Compressor {

//TO DO: replace replaceArray with an input that allows user to choose the substrings to replace, and write
    //the chosen words and key to the top of the product file. In Decompressor, read the key to decompress.

    public static void main(String args[]) throws IOException {
        //Set up the file to read from
        File readFile = new File("src/MySample.txt");
        Scanner scan = new Scanner(readFile);


        //Set up the file to write to
        File writeFile = new File("src/product.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter("src/product.txt");

        //specific words to replace, and recording the number of times each word is replaced
        String[] replaceArray = {"the", "of", "to", "and", "we", " a ", "in", "our", "that", "not"};
        int[] timesReplacedArray = new int[replaceArray.length];



        //read lines from input, replace key words, write to output
        while (scan.hasNextLine()) {
            String scanLine = scan.nextLine();
            for(int i = 0; i < replaceArray.length; i++) {
                int oldLength = scanLine.length();
                scanLine = scanLine.replaceAll(replaceArray[i], i + "");
                timesReplacedArray[i] += ((oldLength - scanLine.length()) / (replaceArray[i].length() - 1));
            }
            writer.write(scanLine + "\n");
        }



        //show what words were replaced most
        /*for(int i = 0; i < replaceArray.length; i++) {
            System.out.println(replaceArray[i] + " has been replaced " + timesReplacedArray[i] + " times.");
        }*/


        writer.close();
    }

}
