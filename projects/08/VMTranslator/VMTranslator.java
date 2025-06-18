
import java.io.*;

public class VMTranslator {

    static FileWriter fileWriter;
    static int debugLevel = 10; // not sure if we need this but probably don't touch it

    public static void main(String args[]) {
        String inputPath = args[0];
        File inputFile = new File(inputPath);
    
        if (inputFile.isDirectory()) {
            // Make one output .asm file named after the directory
            String outputFileName = inputFile.getAbsolutePath() + "/" + inputFile.getName() + ".asm";
    
            try {
                fileWriter = new FileWriter(outputFileName);
                CodeWriter codeWriter = new CodeWriter(fileWriter);
                codeWriter.writerInit(); // writes bootstrap code
    
                traverseDirectory(inputFile, codeWriter);
    
                codeWriter.close();
            } catch (IOException e) {
                System.out.println("Error writing output file");
            }
        } else {
            // One .vm file → one .asm file
            String outputFileName = inputPath.replace(".vm", ".asm");
    
            try {
                fileWriter = new FileWriter(outputFileName);
                CodeWriter codeWriter = new CodeWriter(fileWriter);
    
                translateFile(inputFile, codeWriter);
    
                codeWriter.close();
            } catch (IOException e) {
                System.out.println("Error writing output file");
            }
        }
    }
    

    private static void traverseDirectory(File directory, CodeWriter codeWriter) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseDirectory(file, codeWriter);
                } else if (file.getName().endsWith(".vm")) {
                    translateFile(file, codeWriter);
                }
            }
        }
    }
    

    private static void translateFile(File vmFile, CodeWriter codeWriter) {
        String inFileName = vmFile.getAbsolutePath();
        System.out.println("Translating: " + inFileName);
    
        try (BufferedReader br = new BufferedReader(new FileReader(inFileName))) {
            Parser parser = new Parser(br);
            codeWriter.setFileName(vmFile.getName()); // updates static labels for this file
    
            while (parser.hasMoreCommands()) {
                parser.advance();
    
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
                    case 14 -> codeWriter.writeCall(parser.arg1(), parser.arg2());
                    case 15 -> codeWriter.writeReturn();
                    case 16 -> codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    default -> System.out.println("Unknown Command Type: " + parser.commandType());
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: VMTranslator.translateFile()");
        }
    }
    
}
