import java.io.*;

/* ok so basically when we were assembling the chapter before, we were making something that could translate ONE file to hack
 * now, we gotta be able to convert a WHOLE ASS FOLDER, which means unlike assembler.java, we gotta do ALL THIS bullshit
 * 
/*this guy stinks! :< */

public class VMTranslator {
	static FileWriter fileWriter; // we writing multiple files ofc
	static int debugLevel = 10; // tf is this thang haha?

	public static void main(String args[]) {
		// Get input file or directory name
		String inFileName = args[0]; // grab the name of the file or folder we're translating
		File inputFile = new File(inFileName); // get that jawn in a "file" format so we can do stuff with it

		if (inputFile.isDirectory()) {
			// Handle directory input (folders)
			traverseDirectory(inputFile);
		} else {
			// Handle single file input (.vm)
			translateFile(inputFile);
		}
	}

	//extrrract all ze files from the folder
	private static void traverseDirectory(File directory) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					// Recursively process subdirectory
					traverseDirectory(file);
				} else if (file.getName().endsWith(".vm")) {
					// Process VM file
					translateFile(file);
				}
			}
		}
	}

	//look at the files
	private static void translateFile(File vmFile) {
		String inFileName = vmFile.getAbsolutePath();
		String outFileName = inFileName.replace(".vm", ".asm");

		// Create a Parser object for the provided input file
		try (BufferedReader br = new BufferedReader(new FileReader(inFileName))) {
			Parser parser = new Parser(br);

			// Create a CodeWriter object
			File outFile = new File(outFileName);
			fileWriter = new FileWriter(outFile);
			CodeWriter codeWriter = new CodeWriter(fileWriter);
			codeWriter.setFileName(inFileName);

			// Loop while the Parser object has more commands
			while (parser.hasMoreCommands()) {
				parser.advance();

				switch (parser.commandType()) {
					case 0:
						codeWriter.writePushPop("push", parser.arg1(), parser.arg2());
						break;
					case 1:
						codeWriter.writePushPop("pop", parser.arg1(), parser.arg2());
						break;
					case 2:
						codeWriter.writeArithmetic("add");
						break;
					case 3:
						codeWriter.writeArithmetic("sub");
						break;
					case 4:
						codeWriter.writeArithmetic("neg");
						break;
					case 5:
						codeWriter.writeArithmetic("eq");
						break;
					case 6:
						codeWriter.writeArithmetic("gt");
						break;
					case 7:
						codeWriter.writeArithmetic("lt");
						break;
					case 8:
						codeWriter.writeArithmetic("and");
						break;
					case 9:
						codeWriter.writeArithmetic("or");
						break;
					case 10:
						codeWriter.writeArithmetic("not");
						break;
					case 11:
						codeWriter.writeLabel(parser.arg1());
						break;
					case 12:
						codeWriter.writeGoto(parser.arg1());
						break;
					case 13:
						codeWriter.writeIf(parser.arg1());
						break;
//					case 14:
//						codeWriter.writeCall("call", 5);
//						break;
//					case 15:
//						codeWriter.writeReturn();
//						break;
//					case 16:
//						codeWriter.writeFunction("function", 5);
//						break;
					default:
						System.out.print("?");
				}
			}

			codeWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
