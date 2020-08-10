import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Compressor3 {

    public static void main(String args[]) throws IOException {
        String[][] dataArray = GTFSdatatoArray("src/stop_times_mine");
        int windowSize = 100;

        //TODO: Replace long strings of "D1" with "D1X20" to represent "D1" repeated 20 times, etc; same for multiple I's
        compressArray(dataArray, windowSize);

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
        int numberOfRows = 250;     //FIND A WAY TO COUNT THE NUMBER OF LINES AND COLUMNS, OR MAKE THE ARRAY ADJUSTABLE
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
     * @param windowSize size of the sliding window (number of elements to look back towards, when applicable)
     */
    public static void compressArray(String[][] dataArray, int windowSize) {
        String[] heads = dataArray[0];
        String[] RCQ = new String[heads.length];
        for(int i = 0; i < heads.length; i++) {
            RCQ[i] = "R" + heads[i];
        }
        for(int currentRow = 1; currentRow < dataArray.length; currentRow++) {
            for(int currentHead = 0; currentHead < heads.length; currentHead++) {
                if(heads[currentHead].equals("trip_id") || heads[currentHead].equals("stop_sequence")) {
                    RCQ[currentHead] = RCQ[currentHead].concat(
                            " " + encodeINC(dataArray[currentRow][currentHead], dataArray[currentRow - 1][currentHead]));
                } else if(heads[currentHead].equals("stop_id")) { //NOTE: would it be more efficient to do an increasing prefix check?
                    String[] elementWindowSI = elementWindow(windowSize, dataArray, currentRow, currentHead);
                    RCQ[currentHead] = RCQ[currentHead].concat(
                            " " + encodeSUCC(dataArray[currentRow][currentHead], elementWindowSI));
                } else {
                    RCQ[currentHead] =RCQ[currentHead].concat(" " + dataArray[currentRow][currentHead]);
                }
            }

        }

        for (String s : RCQ) {
            System.out.println(s);
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
     * Takes in an element and the past X elements in that column (depending on window size),
     * then encodes curr based on those past elements
     * Returns "R" + curr otherwise
     * @param curr the item to encode
     * @param elementWindow an array of the past X elements
     * @return "M" if the element before curr has an identical predecessor in the past X elements
     *      * and the element directly after the predecessor is identical to curr
     */
    public static String encodeSUCC(String curr, String[] elementWindow) {
        String returnCode = "";
        int pos = -1;
        for(int i = 1; i < elementWindow.length; i++) {
            if(elementWindow[0].equals(elementWindow[i])) {
                pos = i;
                break;
            }
        }
        //NOTE: Though the paper uses "pos >= 0", I have noticed that pos could never be 0;
        //by using "pos > 0", the code should function the same way, and doesn't break
        //if we somehow end up with pos = 0 and try accessing elementWindow[-1].
        if(pos > 0 && elementWindow[pos - 1].equals(curr)) {
            returnCode = "M";
        } else {
            returnCode = "R" + curr;
        }
        return returnCode;
    }

    public static String[] elementWindow(int windowSize, String[][]dataArray, int currentRow, int currentCol) {
        String[] returnWindow;
        if(currentRow > windowSize + 1) {
            returnWindow = new String[windowSize];
        } else {
            returnWindow = new String[currentRow - 1];
        }
        for(int j = 0; j < returnWindow.length; j++) {
            returnWindow[j] = dataArray[currentRow - j - 1][currentCol];
        }
        return returnWindow;
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





