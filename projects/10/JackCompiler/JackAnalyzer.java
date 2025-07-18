
// single file placeholder
//
// public class JackAnalyzer {
//     public static void main(String[] args) {
//         String sourceFileName = args[0];
//         String outputFileName = sourceFileName.replace(".jack", "ELI.xml");

// 		try{
// 			CompilationEngine engine = new CompilationEngine(sourceFileName, outputFileName);
// 			System.out.println();
// 		} catch (Exception e){
// 			System.out.println("JackAnalyzer.main() error");
// 		}
//     }
// }

/** The analyzer program operates on a given source, where source is either a file name of the form 
	Xxx.jack or a directory name containing one or more such files. For each source Xxx.jack file, 
	the analyzer goes through the following logic: 
	
	1. Create a JackTokenizer from the Xxx.jack input file.
	2. Create an output file called Xxx.xml and prepare it for writing.
	3. Use the CompilationEngine to compile the input JackTokenizer into the output file.
**/
import java.io.*;

class JackAnalyzer {

	public static FileWriter fw;

	public static void main (String[] args) {
		// Check that we have an argument, otherwise, error out
		if (args[0] == null){
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
		String sourceFilename = jackFile.getPath();
		String outputFilename = jackFile.getPath().replace(".jack", ".xml");
		processTokenizer(sourceFilename, outputFilename);
	}
	

	/* Stage 1 wants us to test the tokenizer. To do this, create a JackTokenizer
		object and advance through every token. For each token, print out a line
		of XML output as defined in Section 10.5 Stage 1.
			*/ 
	private static void processTokenizer (String sourceFilename, String outputFilename) {
		try{

		// Create a tokenizer based on the source file
		FileReader reader = new FileReader(sourceFilename);
		JackTokenizer jt = new JackTokenizer(reader);

		// Create a new file for the output
		File outputFile = new File(outputFilename);

		// For every token, print the XML output to the output file
		FileWriter fw = new FileWriter(outputFile);
		w("<tokens>");

		while(jt.hasMoreTokens()){
			jt.advance();
			printToken(jt);
		}

		// Close the output file
		w("</tokens>");
		fw.close();

		} catch (Exception e){
			System.out.println("JackAnalyzer.processTokenizer(): " + e);
		}
	}

	private static void w(String s){
		try{
			fw.write(s);
		} catch (Exception e){
			System.out.println("JackAnalyzer.w() error");
		}
	}

	
	private static void printToken(JackTokenizer jc) {
		// Print the opening tag based on the token type
		
		// Print the value of the tag
		
		// Print the closing of the tag based on the token type
	}
	
	/* Stage 2 is to complete the parser. This will use recursion to traverse down
		a tree. At the top-most level, we will need to call compileClass. This will
		call other compile methods that will recursively call others. This means that
		our process method only need to call a single compile method.
			*/ 
	private static void process (String sourceFilename, String outputFilename) {
		// Create a new CompilationEngine using the source and destination file paths
		
		// Call compileClass on the CompilationEngine
		
		/* Note: Section 10.3.3 does not mention the need for a close() method.
			This is either an oversight or it is expected that the class is 
			self-rectifying in this regard. As compileClass() must be called
			next after the constructor, closing outputFile could occur in the 
			final stages of compileClass(). 
			
			Recommended process would be to allow JackAnalyzer to have control over
			when the CompilationEngine will close the outputFile. */
	}
	


}