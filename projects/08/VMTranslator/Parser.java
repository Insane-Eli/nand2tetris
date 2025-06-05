
import java.io.*;

public class Parser {

    // Create a String for analyzing the current line in the input file
    String currentLine;
    BufferedReader BR;
    String currentCommand;
    String[] stringArr;
    String arg1;
    int arg2;


    /* The constructor opens an input file/stream and gets ready to 
		parse it. The argument will identify the output file / stream.
		This can be done by sending in a stream */
    public Parser(BufferedReader inBR) {
        BR = inBR;
    }

    public boolean hasMoreCommands() {
        // Loop through the file until a valid line of code is found by 
        // filtering out full-line comments and blank lines. The current line of
        // analysis should be stored in currentLine
        try {
            while ((currentLine = BR.readLine()) != null) {
                // If the first character is a valid character of code, return true
                if (!(currentLine.startsWith("//") || currentLine.isBlank())) {

                    // Remove the comments after code that appear in Chapter 8
                    if (currentLine.contains("//")) {
                        currentLine = currentLine.split("//")[0].trim();
                    }

                    // Remove any leading whitespace from the current line
                    currentLine = currentLine.trim();

                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: Parser.hasMoreCommands()");
        }
        return false;
    }

    // advance() will match the current
    public void advance() {
        System.out.println("// " + currentLine);

        arg1 = ""; // word
        arg2 = -1; // index

        // We split our array into the components for analysis
        stringArr = currentLine.split(" ");
        currentCommand = stringArr[0];
        System.out.print("command = " + currentCommand + ", ");

        // Our array is either going to be of length 3 or 1
        // We get arg1 based on the number of elements in stringArr

        if (stringArr.length > 1) { // If the command has a second element, it must be a word
            
            arg1 = stringArr[1];
            System.out.print("arg1 = " + arg1 + ", "); // DEBUG JAWN


            if (stringArr.length > 2) { // If the command has a third element, it must be a number, RIGHT???

                try {
                    arg2 = Integer.parseInt(stringArr[2]);
                    System.out.print("arg2 = " + arg2); // DEBUG JAWN
                } catch (NumberFormatException e) {
                    System.out.println("NumberFormatException: Parser.advance()");
                }

            }

        } else { // * prolly a computation or shart
            arg1 = stringArr[0];
            System.out.print("arg1 = " + arg1); // DEBUG JAWN
        }

        System.out.println(); // DEBUG JAWN

    }


    /* The book indicates command as C_ARITHMETIC, C_PUSH, C_POP, etc. This would suggest the
		use of a Java ENUM. To make sure the prep documents are compilable, String
		was used instead of an ENUM. The coders should feel free to replace the
		return type as fits their implementation. */
    public int commandType() {
        return switch (currentCommand) {
            case "push" -> 0;
            case "pop" -> 1;
            case "add" -> 2;
            case "sub" -> 3;
            case "neg" -> 4;
            case "eq" -> 5;
            case "gt" -> 6;
            case "lt" -> 7;
            case "and" -> 8;
            case "or" -> 9;
            case "not" -> 10;

            // Chapter 8
            case "label" -> 11;
            case "goto" -> 12;
            case "if-goto" -> 13;
            case "call" -> 14;
            case "return" -> 15;
            case "function" -> 16;

            default -> -1;
        };
    }

    public String arg1() {
        return arg1;
    }

    public int arg2() {
        return arg2;
    }
}
