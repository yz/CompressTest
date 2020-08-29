import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Decompressor3 {
    static long startTime = System.nanoTime();
    public static void main(String args[]) throws IOException, ParseException {
        String source_address = "src/stop_times_mine_compressed.txt";
        String product_address = "src/stop_times_mine_decompressed.txt";
        String[] CompressedArray = GTFScompressedDatatoArray(source_address);
        String[][] CompressedArray2D = GTFSArrayto2DArray(CompressedArray);
        GTFSDecompress2DArray(CompressedArray2D);
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
                for(int i = 0; i < dataArray.length; i++) {
                    dataArray[i][j] = "0";
                }
                String currentString = CompressedArray[j];
                while(currentString.length() != 0) {    //this is pretty unstable and depends on the fact that the last character of the string is a space.
                    int indexToUpdate = Integer.parseInt(currentString.substring(0, currentString.indexOf(":")));
                    dataArray[indexToUpdate][j] = currentString.substring(
                            currentString.indexOf(":") + 1, currentString.indexOf(" "));

                    currentString = currentString.substring(currentString.indexOf(" ") + 1);
                }
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

    public static void GTFSDecompress2DArray(String[][] CompressedArray2D) throws ParseException {
        String[] heads = CompressedArray2D[0];
        int SICol = 3, ATCol = 1, DTCol = 2;
        for(int i = 0; i < heads.length; i++) {
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

        for(int currentRow = 0; currentRow < CompressedArray2D.length; currentRow++) {
            String currentElement = CompressedArray2D[currentRow][SICol];
            if(currentElement.substring(0,1).equals("R")) {
                CompressedArray2D[currentRow][SICol] = currentElement.substring(1);
            } else if(currentElement.substring(0,1).equals("M")) {
                int currentPointer = currentRow - 2;
                String predecessor = CompressedArray2D[currentRow - 1][SICol];
                while(currentPointer >= 0) {
                    if(CompressedArray2D[currentPointer][SICol].equals(predecessor)) {
                        CompressedArray2D[currentRow][SICol] = CompressedArray2D[currentPointer + 1][SICol];
                        break;
                    }
                    currentPointer--;
                }
            }
        }

        for(int currentRow = 0; currentRow < CompressedArray2D.length; currentRow++) {
            for(int currentCol = 0; currentCol < CompressedArray2D[0].length; currentCol++) {
                String currentElement = CompressedArray2D[currentRow][currentCol];

                if(currentElement.length() > 0) {
                    if(currentElement.substring(0,1).equals("R")) {
                        CompressedArray2D[currentRow][currentCol] = currentElement.substring(1);
                    } else if(currentElement.equals("I")) {
                        CompressedArray2D[currentRow][currentCol] = CompressedArray2D[currentRow - 1][currentCol];
                    } else if(currentElement.substring(0,1).equals("D")) {      //Note: this only counts for stop sequences and not time differences
                        String prev = CompressedArray2D[currentRow - 1][currentCol];
                        String prevSuffix = findSuffix(prev);
                        String prevPrefix = prev.substring(0, prev.length() - prevSuffix.length());
                        String currSuffix = currentElement.substring(1);
                        CompressedArray2D[currentRow][currentCol] = prevPrefix + (Integer.parseInt(currSuffix) + Integer.parseInt(prevSuffix));
                    } /*else if(currentElement.substring(0,1).equals("M")) {
                        if(currentCol == ATCol) {
                            int currentPointer = currentRow - 2;
                            String predecessorID = CompressedArray2D[currentRow - 1][SICol];
                            String currentID = CompressedArray2D[currentRow][SICol];
                            //String predecessor = CompressedArray2D[currentRow - 1][currentCol];
                            while(currentPointer > 0) {
                                System.out.println("PredecessorID: " + predecessorID + " currentID: " + currentID +
                                        " pointer current: " + CompressedArray2D[currentPointer][SICol] + " Pointer future: " + CompressedArray2D[currentPointer + 1][SICol]);
                                if(CompressedArray2D[currentPointer + 1][SICol].equals(currentID)
                                && CompressedArray2D[currentPointer][SICol].equals(predecessorID)) {
                                    System.out.println("Found match");
                                    System.out.println("timediff = " + CompressedArray2D[currentPointer + 1][ATCol] + " - " + CompressedArray2D[currentPointer][DTCol]);
                                    long timeDiff = timeDifference(CompressedArray2D[currentPointer + 1][ATCol], CompressedArray2D[currentPointer][DTCol]);
                                    String timeDiffString = toTimeString(timeDiff);
                                    System.out.println("CurrentDT = " + timeDiffString + " + " + CompressedArray2D[currentRow - 1][DTCol]);
                                    long currentDT = timeSum(" " + timeDiffString, CompressedArray2D[currentRow - 1][DTCol]);       //CANT GET SECOND TERM OF CURRENTDT TO WORK
                                    String currentDTString = toTimeString(currentDT);
                                    CompressedArray2D[currentRow][currentCol] = " " + currentDTString;
                                    break;
                                }
                                currentPointer--;
                            }
                        }
*//*                        if(currentCol == SICol) {      //Remove this condition once other cases of "M" are resolved
                            int currentPointer = currentRow - 2;
                            String predecessor = CompressedArray2D[currentRow - 1][currentCol];
                            while(currentPointer >= 0) {
                                if(CompressedArray2D[currentPointer][currentCol].equals(predecessor)) {
                                    CompressedArray2D[currentRow][currentCol] = CompressedArray2D[currentPointer + 1][currentCol];
                                    break;
                                }
                                currentPointer--;
                            }
                        }*//*
                    } */else if(currentElement.substring(0,1).equals("A")) {
                        CompressedArray2D[currentRow][currentCol] = CompressedArray2D[currentRow][ATCol];
                    }
                }


            }
        }



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
        return ((date1.getTime() - date2.getTime()) / 1000);
    }

    public static String toTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        time *= 1000;
        return format.format(time);
    }

    /**
     * Takes in two strings that are in the form of times, then outputs their difference
     * in seconds
     * @param timeString1 the time to subtract from
     * @param timeString2 the time to subtract to time1
     * @return the time difference, in seconds
     */
    public static long timeSum(String timeString1, String timeString2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(timeString1);
        Date date2 = format.parse(timeString2);
        return ((date2.getTime() + date1.getTime()) / 1000);
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
