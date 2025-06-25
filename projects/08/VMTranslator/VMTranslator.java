import java.io.*;

public class VMTranslator {

    static FileWriter fileWriter;
    static int debugLevel = 10; // not sure if we need this but probably don't touch it

    static File outputFile;
    static File inputFile;
    static CodeWriter codeWriter;
   public static void main(String args[]) {
    String inFilePath = args[0];
    inputFile = new File(inFilePath);

    String outputFileName;

    if (inputFile.isDirectory()) {
        outputFileName = inputFile.getAbsolutePath() + "/" + inputFile.getName() + ".asm";
    } else {
        outputFileName = inputFile.getPath().replace(".vm", ".asm");
    }

    outputFile = new File(outputFileName);

    try {
        fileWriter = new FileWriter(outputFile);
        codeWriter = new CodeWriter(fileWriter);

        if (inputFile.isDirectory()) {
            bootstrapBuddy(inputFile);
            traverseDirectory(inputFile);
        } else {
            if(inputFile.getName().equals("Sys.vm")){
                codeWriter.writeInit();
            }
            translateFile(inputFile);
        }

        codeWriter.close();
    } catch (IOException e) {
        System.out.println("error");
    }
}

    // private helper method to detect if theres a Sys.vm file within the directory and add bootstrap code accordingly
    private static void bootstrapBuddy(File directory){
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equals("Sys.vm")) {
                    codeWriter.writeInit();
                    break;
                }
            }
        }
    }

    private static void traverseDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".vm")) {
                    translateFile(file);
                }
            }
        }
    }
    
    

    private static void translateFile(File vmFile) {
        // Create a Parser object for the provided input file
        try (BufferedReader br = new BufferedReader(new FileReader(vmFile))) {
            
            Parser parser = new Parser(br);

            codeWriter.setFileName(vmFile.getName().replace(".vm", ""));
            
            System.out.println("Translating: " + vmFile.getName());

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

                    // Chapter 8
                    case 11 -> codeWriter.writeLabel(parser.arg1());
                    case 12 -> codeWriter.writeGoto(parser.arg1());
                    case 13 -> codeWriter.writeIf(parser.arg1());
                    case 14 -> codeWriter.writeCall(parser.arg1(), parser.arg2());
                    case 15 -> codeWriter.writeReturn();
                    case 16 -> codeWriter.writeFunction(parser.arg1(), parser.arg2());

                    default -> System.out.println("Error: VMTranslator.java: translatefile(): Unknown Command Type (" + parser.commandType() + ")");
                }
            }

        } catch (IOException e) {
            System.out.println("IOException: VMTranslator.translateFile()");
        }
    }
}
