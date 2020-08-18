import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Decompressor3 {
    static long startTime = System.nanoTime();
    public static void main(String args[]) throws IOException {
        String source_address = "src/stop_times_mine_compressed.txt";
        String product_address = "src/stop_times_mine_decompressed.txt";
        //String[] CompressedArray = GTFScompressedDatatoArray(source_address);


        System.out.println("hello");


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




        //print2DArray(dataArray);
        //printArray();
        //writeArray(dataArray);
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

    public static void printArray(String[] compressedArray) {
        for (String s : compressedArray) {
            System.out.println(s);
        }
    }

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
