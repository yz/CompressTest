import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Decompressor3 {
    static long startTime = System.nanoTime();
    public static void main(String args[]) throws IOException {
        String source_address = "src/stop_times_mine_compressed.txt";
        String product_address = "src/stop_times_mine_decompressed.txt";
        String[] CompressedArray = GTFScompressedDatatoArray(source_address);
        String[][] CompressedArray2D = GTFSArrayto2DArray(CompressedArray);




/*
        int i = 0;
        int j = 0;
        while (scan.hasNextLine()) {
            String scanLine = scan.nextLine();
            while(scanLine.length() > 0) {
                if(scanLine.contains(",")) {
                    dataArray[i][j] = scanLine.substring(0, scanLine.indexOf(","));
                    scanLine = scanLine.substring(scanLine.indexOf(",") + 1, scanLine.length());
                } else {
                    dataArray[i][j] = scanLine;
                    scanLine = "";
                }

                j++;
            }
            j = 0;
            i++;
        }
*/




        print2DArray(CompressedArray2D);
        //printArray(CompressedArray);
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
     * Takes in a 2D String array and writes it to a file
     * In future versions, allow input of the file location.
     * @param dataArray the array to print
     * @throws IOException if the address of the file is invalid
     */
    public static void writeArray(String[][] dataArray) throws IOException {
        //Set up the file to write to
        File writeFile = new File("src/DecompressedTransfersProduct.txt");
        writeFile.createNewFile();
        FileWriter writer = new FileWriter("src/DecompressedTransfersProduct.txt");

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
