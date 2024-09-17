import java.lang.*;
import java.io.*;
public class VMTranslator {
    public static void main(String args[]){
    //makes two strings, one for the file coming in, and one for the file going out
    String inFileName = args[0];
    String outFileName = inFileName.replace(".vm", ".asm");
    
    //sends the file coming in to the parser
    Parser parser = new Parser(inFileName, outFileName);
    }
}