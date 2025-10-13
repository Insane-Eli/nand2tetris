
/** The analyzer program operates on a given source, where source is either a file name of the form
 * Xxx.jack or a directory name containing one or more such files. For each source Xxx.jack file,
 * the analyzer goes through the following logic:  *
 * 1. Create a JackTokenizer from the Xxx.jack input file.
 * 2. Create an output file called Xxx.xml and prepare it for writing.
 * 3. Use the CompilationEngine to compile the input JackTokenizer into the output file.
 * */

import java.io.*;

class JackAnalyzer {

    public static FileWriter fw;
    public static String shortOutputFileName;

    public static void main(String[] args) {
        // Check that we have an argument, otherwise, error out
        if (args[0] == null) {
            System.out.println("JackAnalyzer.main() error: no argument");
            return;
        }

        String sourceFilename = args[0];
        File inputFile = new File(sourceFilename);
        if (inputFile.isDirectory()) {
            // If we have a directory of files, loop through each file
            // with the .jack extension and process each file
            traverseDirectory(inputFile);
        } else {
            translateFile(inputFile);
        }

    }

    private static void traverseDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // Verify that the filename has an appropriate extension
                if (file.isFile() && file.getName().endsWith(".jack")) {
                    translateFile(file);
                }
            }
        }
    }

    private static void translateFile(File jackFile) {
        // Otherwise, process the individual file using either processTokenizer() for
        // Stage 1 or process() for Stage 2 of the Chapter 10 projects.
        String sourceFileName = jackFile.getPath();
        String outputFileName = jackFile.getPath().replace(".jack", ".vm");
        shortOutputFileName = jackFile.getName().replace(".jack", ".vm");
        headerLine();
        System.out.println("Now compiling: " + sourceFileName + "\n");
        processTokenizer(sourceFileName, outputFileName);
    }

    public static void headerLine(){
        for(int i = 0; i < 20; i++){
            System.out.print("-----");
        }
        System.out.println();
    }


    /* Stage 1 wants us to test the tokenizer. To do this, create a JackTokenizer
		object and advance through every token. For each token, print out a line
		of XML output as defined in Section 10.5 Stage 1.
     */
    private static void processTokenizer(String sourceFileName, String outputFileName) {
        try {
            System.out.print("Creating new CompilationEngine() for " + shortOutputFileName + "... ");
            CompilationEngine engine = new CompilationEngine(sourceFileName, outputFileName);        
        } catch (Exception e) {
            System.out.println("JackAnalyzer.processTokenizer() error: " + e);
        }
	}
}
