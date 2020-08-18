import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Decompressor2mk1 {

    public static void main(String args[]) throws IOException {
        //Set up the file to read from
        File readFile = new File("src/TransfersProduct.txt");
        Scanner scan = new Scanner(readFile);

        //declare a 2D array representing the product document
        int numberOfRows = 617;     //FIND A WAY TO COUNT THE NUMBER OF LINES AND COLUMNS, OR MAKE THE ARRAY ADJUSTABLE
        int numberOfCols = 4;
        String[][] dataArray = new String[numberOfRows][numberOfCols];

        //Reads the reading file into the array
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

        //Decompresses the file by decoding the array
        //I DONT KNOW IF THIS FULLY WORKS OR NOT SINCE I'M DOING THIS KINDA RUSHED; TRY TO BREAK THIS
        for(int i2 = 2; i2 < dataArray.length; i2++) {
            if(isInteger(dataArray[i2][0]) && isInteger(dataArray[i2-1][0])) {
                dataArray[i2][0] = (Integer.parseInt(dataArray[i2][0]) + Integer.parseInt(dataArray[i2-1][0])) + "";
            }

            if(dataArray[i2][1].equals("1")) {
                dataArray[i2][1] = new String(dataArray[i2][0]);
            }


            if(dataArray[i2][2] == null || dataArray[i2][2].equals("")) {
                dataArray[i2][2] = "2";
            }
            if(dataArray[i2][3] == null || dataArray[i2][3].equals("")) {
                dataArray[i2][3] = "180";
            }
        }


        //printArray(dataArray);
        writeArray(dataArray);
        //decompresses by retranslating the array into original format




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
