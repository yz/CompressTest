import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Decompressor3 {
    static long startTime = System.nanoTime();
    public static void main(String args[]) throws IOException {
        String source_address = "src/stop_times_mine_compressed.txt";
        String product_address = "src/stop_times_mine_decompressed.txt";
        String[] CompressedArray = GTFScompressedDatatoArray(source_address);
        String[][] CompressedArray2D = GTFSArrayto2DArray(CompressedArray);
        //GTFSDecompress2DArray(CompressedArray2D);
        String[] DecompressedArray = GTFSDecompressed2DArraytoArray(CompressedArray2D);





        //print2DArray(CompressedArray2D);
        printArray(DecompressedArray);
        //writeArray(dataArray);
    }

    /**
     * Takes in the address of the source file and converts the text into an array of Strings.
     * @param source_address the address of the source file
     * @return the source file converted into an array of strings
     * @throws FileNotFoundException if the address is not found
     */
    public static String[] GTFScompressedDatatoArray(String source_address) throws FileNotFoundException {
        File readFile = new File(source_address);
        Scanner scan = new Scanner(readFile);
        int i = 0;
        int numberOfCols = 9;
        String[] dataArray = new String[numberOfCols];
        while(i < numberOfCols) {
            String scanLine = scan.nextLine();
            dataArray[i] = scanLine;
            i++;
        }
        return dataArray;
    }


    /**
     * Takes in a String array and converts it to a 2D String array
     * NOTE: currently uses a magic number. try to find a fix.
     * @param CompressedArray the 1-dimensional String array
     * @return the array converted to 2D
     */
    public static String[][] GTFSArrayto2DArray(String[] CompressedArray) {
        int numberOfRows = 250;
        int numberOfCols = 9;
        String[][] dataArray = new String[numberOfRows][numberOfCols];
        for(int j = 0; j < dataArray[0].length; j++) {
            if(!CompressedArray[j].substring(0,1).equals("R")) {
            } else {
                String currentString = CompressedArray[j];
                for(int i = 0; i < dataArray.length; i++) {
                    if(currentString.contains(" ")) {
                        dataArray[i][j] = currentString.substring(0, currentString.indexOf(" "));
                    } else {
                        dataArray[i][j] = currentString;
                    }
                    currentString = currentString.substring(currentString.indexOf(" ") + 1);
                }
            }
        }
        return dataArray;
    }


    public static String[] GTFSDecompressed2DArraytoArray(String[][] Decompressed2DArray) {
        int numberOfRows = 250;

        String[] returnArray = new String[numberOfRows];
        for(int i = 0; i < Decompressed2DArray.length; i++) {
            returnArray[i] = Decompressed2DArray[i][0];
        }
        for(int i = 0; i < Decompressed2DArray.length; i++) {
            for(int j = 1; j < Decompressed2DArray[0].length; j++) {
                returnArray[i] = returnArray[i] + "," + Decompressed2DArray[i][j];
            }
        }
        return returnArray;
    }

    /**
     * Checks if a string can be parsed into an integer
     * I borrowed this method from the Internet and it's also said to be not very proper or fast,
     * so this should be replaced by a better method in future versions
     * @param input the string to check
     * @return true if the string is an integer, false if it is not an integer
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }

    /**
     * Takes in a 2D String array and prints its contents to the console
     * @param dataArray the array to print.
     */
    public static void print2DArray(String[][] dataArray) {
        System.out.println("Array print time");
        for(int i = 0; i < dataArray.length; i++) {
            for(int j = 0; j < dataArray[0].length; j++) {
                System.out.print(dataArray[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Takes in String array and prints its contents to the console
     * @param compressedArray the array to print.
     */
    public static void printArray(String[] compressedArray) {
        for (String s : compressedArray) {
            System.out.println(s);
        }
    }

    /**
     * Takes in a String array and writes it to a file
     * In future versions, allow input of the file location.
     * @param dataArray the array to print
     * @throws IOException if the address of the file is invalid
     */
    public static void writeArray(String[] dataArray, String product_address) throws IOException {
        //Set up the file to write to
        File writeFile = new File(product_address);
        writeFile.createNewFile();
        FileWriter writer = new FileWriter(product_address);

        System.out.println("write time");
        for(int i = 0; i < dataArray.length; i++) {
            writer.write(dataArray[i] + "\n");
        }
        writer.close();
    }


}
