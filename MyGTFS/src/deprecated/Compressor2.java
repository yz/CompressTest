import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Compressor2 {

//TO DO: replace replaceArray with an input that allows user to choose the substrings to replace, and write
    //the chosen words and key to the top of the product file. In Decompressor, read the key to decompress.

    public static void main(String args[]) throws IOException {
        //Set up the file to read from
        File readFile = new File("src/gtfs/transfers.txt");
        Scanner scan = new Scanner(readFile);


        //Set up the file to write to
        File writeFile = new File("src/TransfersProduct.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter("src/TransfersProduct.txt");

        //parse the source file into an array
        int i = 0;
        int j = 0;
        int numberOfRows = 617;     //FIND A WAY TO COUNT THE NUMBER OF LINES AND COLUMNS, OR MAKE THE ARRAY ADJUSTABLE
        int numberOfCols = 4;
         String[][] dataArray = new String[numberOfRows][numberOfCols];
        while(i < numberOfRows) {
            String scanLine = scan.nextLine();
            //System.out.println(scanLine);
            while(j < numberOfCols) {
                if(scanLine.contains(",")) {
                    dataArray[i][j] = scanLine.substring(0, scanLine.indexOf(","));
                    System.out.println("Cut , to get: " + dataArray[i][j]);
                } else {
                    dataArray[i][j] = scanLine;
                    System.out.println("used full string to get: " + dataArray[i][j]);
                }

                scanLine = scanLine.substring(scanLine.indexOf(",") + 1, scanLine.length());
                j++;
            }
            j = 0;
            i++;
        }


        System.out.println("Array print time");
        for(int ii = 0; ii < dataArray.length; ii++) {
            for(int jj = 0; jj < dataArray[0].length; jj++) {
                System.out.print(dataArray[ii][jj] + " ");
            }
            System.out.println();
        }



        //read lines from input, replace key words, write to output
        /*while (scan.hasNextLine()) {
            String scanLine = scan.nextLine();
            for(int i = 0; i < replaceArray.length; i++) {
                int oldLength = scanLine.length();
                scanLine = scanLine.replaceAll(replaceArray[i], i + "");
                timesReplacedArray[i] += ((oldLength - scanLine.length()) / (replaceArray[i].length() - 1));
            }
            writer.write(scanLine + "\n");
        }*/



        //show what words were replaced most
        /*for(int i = 0; i < replaceArray.length; i++) {
            System.out.println(replaceArray[i] + " has been replaced " + timesReplacedArray[i] + " times.");
        }*/


        writer.close();
    }

}
