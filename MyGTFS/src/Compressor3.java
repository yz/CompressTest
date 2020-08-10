import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Compressor3 {

    public static void main(String args[]) throws IOException {
        String[][] dataArray = GTFSdatatoArray("src/stop_times_mine");


        //TODO: Replace long strings of "D1" with "D1X20" to represent "D1" repeated 20 times, etc; same for multiple I's
        compressArray(dataArray);



        //Compress the array
        //LOOK FOR "1,1"??
        /*for(int i2 = dataArray.length - 1; i2 >= 2; i2--) {
            *//*for(int j2 = 0; j2 < numberOfCols; j2++) {
                if(isInteger(dataArray[i2][j2])) {
                    dataArray[i2][j2] = 0 + "";
                }
            }*//*
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
        }*/


        printArray(dataArray);
        //writeArray(dataArray);
    }

    /**
     * Takes in the address of the text file, then parses the text into a 2D array of Strings
     * @param addressOfFile the address of the text file
     * @return the contents of the text file in array form
     */
    public static  String[][] GTFSdatatoArray(String addressOfFile) throws FileNotFoundException {
        //Generate scanner of the input text file
        File readFile = new File("src/stop_times_mine.txt");
        Scanner scan = new Scanner(readFile);

        //scan the input text file and parse it to an array
        int i = 0;
        int j = 0;
        int numberOfRows = 85;     //FIND A WAY TO COUNT THE NUMBER OF LINES AND COLUMNS, OR MAKE THE ARRAY ADJUSTABLE
        int numberOfCols = 9;
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
        return dataArray;
    }

    /**
     * Takes the given Array and compresses it
     * @param dataArray the array to compress
     */
    public static void compressArray(String[][] dataArray) {
        String[] heads = dataArray[0];
        String[] RCQ = new String[heads.length];
        for(int i = 0; i < heads.length; i++) {
            RCQ[i] = "R" + heads[i];
        }
        for(int i = 1; i < dataArray.length; i++) {
            for(int currentHead = 0; currentHead < heads.length; currentHead++) {
                if(heads[currentHead].equals("trip_id") || heads[currentHead].equals("stop_sequence")) {
                    RCQ[currentHead] = RCQ[currentHead].concat(
                            encodeINC(dataArray[i][currentHead], dataArray[i - 1][currentHead]));
                } else if(heads[currentHead].equals("stop_id")) { //NOTE: would it be more efficient to do an increasing prefix check?
                    /*RCQ[currentHead] = RCQ[currentHead].concat(
                            encodeSUCC(dataArray[i][currentHead], dataArray[i - 1][currentHead]));*/
                } else {
                    RCQ[currentHead] =RCQ[currentHead].concat(" " + dataArray[i][currentHead]);
                }
            }

        }

        for(int i = 0; i < RCQ.length; i++) {
            System.out.println(RCQ[i]);
        }


    }

    /**
     * Takes in an item of a column and encodes it depending on the item that preceeds it in the column.
     * @param curr the item to encode
     * @param prev the item that preceeds curr in its column
     * @return "I" if curr is equal to prev, the difference between the items if applicable, or
     *          curr if there is no simple way to encode it.
     */
    public static String encodeINC(String curr, String prev) {
        //System.out.println("Curr = " + curr + ", prev = " + prev);
        String returnCode = "";
        String currSuffix = findSuffix(curr);
        String prevSuffix = findSuffix(prev);
        String currPrefix = curr.substring(0, curr.length() - currSuffix.length());
        String prevPrefix = prev.substring(0, prev.length() - prevSuffix.length());

        if(curr.equals(prev)) {
            returnCode = "I";
        } else if (currPrefix.equals(prevPrefix)) { /*NOTE: the given algorithm has a condition that I do not understand the need for*/
            returnCode = "D" + (Integer.parseInt(currSuffix) - Integer.parseInt(prevSuffix));
        } else {
            returnCode = "R" + curr;
        }
        return returnCode;
    }

    /**
     * Takes in a string and finds the longest suffix that is an integer
     * @param thisString the string to fix a suffix of
     * @return the integer suffix of the string
     */
    public static String findSuffix(String thisString) {
        String returnSuffix = "";
        for(int i = 1; i <= thisString.length(); i++) {
            String currentSuffix = thisString.substring(thisString.length() - i);
            if(isInteger(currentSuffix)) {
                returnSuffix = currentSuffix;
            }  else {
                return returnSuffix;
            }
        }
        return returnSuffix;
    }

    /**
     * Checks if a string can be parsed into an integer
     * I borrowed this method from the Internet and it's also said to be not very fast, so this should be replaced
     * by a better method in future versions
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
    public static void printArray(String[][] dataArray) {
        System.out.println("Array print time");
        for(int i = 0; i < dataArray.length; i++) {
            for(int j = 0; j < dataArray[0].length; j++) {
                System.out.print(dataArray[i][j] + " ");
            }
            System.out.println();
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





