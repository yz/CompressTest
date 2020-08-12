import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class Compressor3 {

    public static void main(String args[]) throws IOException, ParseException {
        String[][] dataArray = GTFSdatatoArray("src/stop_times_mine");
        int windowSize = 100;



        //TODO: Replace long strings of "D1" with "D1X20" to represent "D1" repeated 20 times, etc; same for multiple I's
        String[] compressedArray = compressArray(dataArray, windowSize);

        //print2DArray(dataArray);
        printArray(compressedArray);
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
        //int numberOfRows = 250;     //FIND A WAY TO COUNT THE NUMBER OF LINES AND COLUMNS, OR MAKE THE ARRAY ADJUSTABLE
        int numberOfRows = readLines("src/stop_times_mine.txt");
        int numberOfCols = 9;
        String[][] dataArray = new String[numberOfRows][numberOfCols];
        while(i < numberOfRows) {
            String scanLine = scan.nextLine();
            while(j < numberOfCols) {
                if(scanLine.contains(",")) {
                    dataArray[i][j] = scanLine.substring(0, scanLine.indexOf(","));
                } else {
                    dataArray[i][j] = scanLine;
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
    public static String[] compressArray(String[][] dataArray, int windowSize) throws ParseException {
        String[] heads = dataArray[0];
        String[] RCQ = new String[heads.length];
        int SICol = 3, ATCol = 1, DTCol = 2;
        for(int i = 0; i < heads.length; i++) {
            RCQ[i] = "R" + heads[i];

            //Remember what indices represent the columns with these data types
            switch (heads[i]) {
                case "stop_id":
                    SICol = i;
                    break;
                case "arrival_time":
                    ATCol = i;
                    break;
                case "departure_time":
                    DTCol = i;
                    break;
            }

        }
        for(int currentRow = 1; currentRow < dataArray.length; currentRow++) {
            for(int currentHead = 0; currentHead < heads.length; currentHead++) {
                if(heads[currentHead].equals("trip_id") || heads[currentHead].equals("stop_sequence")) {
                    RCQ[currentHead] = RCQ[currentHead].concat(
                            " " + encodeINC(dataArray[currentRow][currentHead], dataArray[currentRow - 1][currentHead]));
                } else if(heads[currentHead].equals("stop_id")) { //NOTE: would it be more efficient to do an increasing prefix check?
                    String[] elementWindowSI = elementWindow(windowSize, dataArray, currentRow, SICol);
                    RCQ[currentHead] = RCQ[currentHead].concat(
                            " " + encodeSUCC(dataArray[currentRow][currentHead], elementWindowSI));
                } else if(heads[currentHead].equals("arrival_time")) {
                    String[] elementWindowSI = elementWindow(windowSize, dataArray, currentRow, SICol);
                    String[] elementWindowAT = elementWindow(windowSize, dataArray, currentRow, ATCol);
                    String[] elementWindowDT = elementWindow(windowSize, dataArray, currentRow, DTCol);
                    RCQ[currentHead] = RCQ[currentHead].concat(
                            " " + encodeAT(dataArray[currentRow][currentHead], dataArray[currentRow][SICol],
                            elementWindowAT, elementWindowDT, elementWindowSI));
                } else {
                    RCQ[currentHead] =RCQ[currentHead].concat(" " + dataArray[currentRow][currentHead]);
                }
            }

        }
        //Quick simplifier of the simpler columns that didn't get compressed
        //NOTE: This leaves a space at the end of these lines. While this is easily fixable by erasing the last
        //character, I wonder if there's a more elegant way to format this.
        for(int currentHead = 0; currentHead < heads.length; currentHead++) {
            if(heads[currentHead].equals("pickup_type") || heads[currentHead].equals("drop_off_type")) {
                RCQ[currentHead] = nonZeroCompressor(RCQ[currentHead]);
            }
        }

        return RCQ;
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
     * Takes in an element, the corresponding stop ID, and the previous X
     * arrival times, departure times, and stop IDs, then encodes curr based on those elements
     * @param curr the item to encode
     * @param currSI the stop ID corresponding to curr
     * @param elementWindowAT the past X arrival times
     * @param elementWindowDT the past X departure times
     * @param elementWindowSI the past X stop IDs
     * @return "M" if there is a previous perfect match, "D###" if there is a match with
     * a time difference, and "R" + curr if there is no match found
     */
    public static String encodeAT(String curr, String currSI,
                                  String[] elementWindowAT, String[] elementWindowDT, String[] elementWindowSI) throws ParseException {
        if(elementWindowDT.length > 0) {
            long timediff = timeDifference(curr, elementWindowDT[0]);
            for(int i = 1; i < elementWindowAT.length; i++) {
                //System.out.println("For AT " + curr + "; SI is " + currSI + " and elementWindowSI[i - 1] is " + elementWindowSI[i - 1] + "...also elementWindowSI[0] is " + elementWindowSI[0] + " and elementWindowSI[i] is " + elementWindowSI[i]);
                if(currSI.equals(elementWindowSI[i - 1]) && elementWindowSI[0].equals(elementWindowSI[i]) &&
                        timeDifference(elementWindowAT[i - 1], elementWindowDT[i]) == timediff) {
                    return "M";
                } else if(currSI.equals(elementWindowSI[i - 1]) && elementWindowSI[0].equals(elementWindowSI[i])) {
                    return "D" + (timediff - timeDifference(elementWindowAT[i - 1], elementWindowDT[i]));
                }
            }
        }
        return "R" + curr;
    }

    public static String nonZeroCompressor(String column) {
        String returnString = "";
        int currentIndex = 0;
        while(column.contains(" ")) {
            String currentTerm = column.substring(0, column.indexOf(" "));
            if(!currentTerm.equals("0")) {
                returnString = returnString.concat(currentIndex + ":" + currentTerm + " ");
            }
            column = column.substring(column.indexOf(" ") + 1);
            currentIndex++;
        }
        return returnString;
    }

    /**
     * Takes in two strings that are in the form of times, then outputs their difference
     * in seconds
     * @param timeString1 the time to subtract from
     * @param timeString2 the time to subtract to time1
     * @return the time difference, in seconds
     */
    public static long timeDifference(String timeString1, String timeString2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(timeString1);
        Date date2 = format.parse(timeString2);
        return ((date2.getTime() - date1.getTime()) / 1000);
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
     * Takes in the address of a file and outputs the number of lines in the file.
     * NOTE: this is a naive way of counting the number of lines. there are faster,
     * more efficient ways to count them online, but I'd rather not use them
     * until I understand them.
     * @param fileAddress the address of the file
     * @return the number of lines in the file
     */
    public static int readLines(String fileAddress) throws FileNotFoundException {
        File readFile = new File(fileAddress);
        Scanner scan = new Scanner(readFile);
        int numberOfLines = 0;
        while(scan.hasNextLine()) {
            scan.nextLine();
            numberOfLines++;
        }
        return numberOfLines;
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





