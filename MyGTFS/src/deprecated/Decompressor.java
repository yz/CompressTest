import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Decompressor {

    public static void main(String args[]) throws IOException {
        //Set up the file to read from
        File readFile = new File("src/product.txt");
        Scanner scan = new Scanner(readFile);


        //Set up the file to write to
        File writeFile = new File("src/decompressedProduct.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter("src/decompressedProduct.txt");

        //Declaring the key that will decompress the file
        String[] replaceArray = {"the", "of", "to", "and", "we", " a ", "in", "our", "that", "not"};

        while (scan.hasNextLine()) {
            String scanLine = scan.nextLine();

            for(int i = 0; i < replaceArray.length; i++) {
                scanLine = scanLine.replaceAll(i + "", replaceArray[i]);
            }
            writer.write(scanLine + "\n");
        }

        writer.close();

    }
}
