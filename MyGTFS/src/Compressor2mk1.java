import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Compressor2mk1 {

//TO DO: replace replaceArray with an input that allows user to choose the substrings to replace, and write
    //the chosen words and key to the top of the product file. In Decompressor, read the key to decompress.

    public static void main(String args[]) throws IOException {
        //Set up the file to read from
        File readFile = new File("src/gtfs/transfers.txt");
        Scanner scan = new Scanner(readFile);




        //parse the source file into an array
        int i = 0;
        int j = 0;
        int numberOfRows = 617;     //FIND A WAY TO COUNT THE NUMBER OF LINES AND COLUMNS, OR MAKE THE ARRAY ADJUSTABLE
        int numberOfCols = 4;
         String[][] dataArray = new String[numberOfRows][numberOfCols];
        while(i < numberOfRows) {
            String scanLine = scan.nextLine();
            while(j < numberOfCols) {
                if(scanLine.contains(",")) {
                    dataArray[i][j] = scanLine.substring(0, scanLine.indexOf(","));
                    //System.out.println("Cut , to get: " + dataArray[i][j]);
                } else {
                    dataArray[i][j] = scanLine;
                    //System.out.println("used full string to get: " + dataArray[i][j]);
                }

                scanLine = scanLine.substring(scanLine.indexOf(",") + 1, scanLine.length());
                j++;
            }
            j = 0;
            i++;
        }


        //Compress the array
        for(int i2 = numberOfRows - 1; i2 >= 2; i2--) {
            /*for(int j2 = 0; j2 < numberOfCols; j2++) {
                if(isInteger(dataArray[i2][j2])) {
                    dataArray[i2][j2] = 0 + "";
                }
            }*/
            if(dataArray[i2][0].equals(dataArray[i2][1])) {
                //REPLACE BELOW LINE WITH "1"??
                dataArray[i2][1] = 1 + "";

            }
            if(isInteger(dataArray[i2][0]) && isInteger(dataArray[i2-1][0])) {
                dataArray[i2][0] = (Integer.parseInt(dataArray[i2][0]) - Integer.parseInt(dataArray[i2-1][0])) + "";
            }
            if(dataArray[i2][2].equals("2")) {
                dataArray[i2][2] = "";
            }
            if(dataArray[i2][3].equals("180")) {
                dataArray[i2][3] = "";
            }
        }


        //printArray(dataArray);
        writeArray(dataArray);

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







    }
    //checks if a string can be parsed into an integer
    //I borrowed this method from the Internet and it's also said to be not very fast, so this should be replaced
    //by a better
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }

    //Prints the array
    public static void printArray(String[][] dataArray) {
        System.out.println("Array print time");
        for(int i = 0; i < dataArray.length; i++) {
            for(int j = 0; j < dataArray[0].length; j++) {
                System.out.print(dataArray[i][j] + " ");
            }
            System.out.println();
        }
    }

    //Writes to the file
    public static void writeArray(String[][] dataArray) throws IOException {
        //Set up the file to write to
        File writeFile = new File("src/TransfersProduct.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter("src/TransfersProduct.txt");

        System.out.println("write time");
        for(int i = 0; i < dataArray.length; i++) {
            //System.out.println(dataArray[i][0] + dataArray[i][1] + dataArray[i][2] + dataArray[i][3]);
            if(dataArray[i][2].equals("") && dataArray[i][3].equals("")) {
                writer.write(dataArray[i][0] + "," + dataArray[i][1] + "\n");
            } else {
                writer.write(dataArray[i][0] + "," + dataArray[i][1] + "," + dataArray[i][2] + "," + dataArray[i][3] + "\n");
            }

        }
        writer.close();
    }
}





