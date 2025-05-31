
import java.io.*;

public class VMTranslator {

    static FileWriter fileWriter;
    static int debugLevel = 10; // not sure if we need this but probably don't touch it

    public static void main(String args[]) {
        // Get input file or directory name
        String inFileName = args[0];
        File inputFile = new File(inFileName);

        if (inputFile.isDirectory()) {
            // Handle directory input
            traverseDirectory(inputFile);
        } else {
            // Handle single file input
            translateFile(inputFile);
        }
    }

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

                // advance through the command
                parser.advance();

                // and do something with the codeWriter depending on what it is
                switch (parser.commandType()) {

                    case 0 -> codeWriter.writePushPop("push", parser.arg1(), parser.arg2());
                    case 1 -> codeWriter.writePushPop("pop", parser.arg1(), parser.arg2());
                    case 2 -> codeWriter.writeArithmetic("add");
                    case 3 -> codeWriter.writeArithmetic("sub");
                    case 4 -> codeWriter.writeArithmetic("neg");
                    case 5 -> codeWriter.writeArithmetic("eq");
                    case 6 -> codeWriter.writeArithmetic("gt");
                    case 7 -> codeWriter.writeArithmetic("lt");
                    case 8 -> codeWriter.writeArithmetic("and");
                    case 9 -> codeWriter.writeArithmetic("or");
                    case 10 -> codeWriter.writeArithmetic("not");
                    case 11 -> codeWriter.writeLabel(parser.arg1());
                    case 12 -> codeWriter.writeGoto(parser.arg1());
                    case 13 -> codeWriter.writeIf(parser.arg1());

                    default -> System.out.print("?");
                }
            }

            codeWriter.close();
        } catch (IOException e) {
            System.out.println("IOException: VMTranslator.translateFile()");
        }
    }
}
