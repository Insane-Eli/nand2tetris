import java.io.*;

public class VMWriter {

    public File outputFile;
    public FileWriter fw;
    public String labelPrefix;

    public VMWriter(String outputFilePath){ // Creates a new file and prepares it for writing.
        try {

            System.out.println("DONE!");

            outputFile = new File(outputFilePath);
            fw = new FileWriter(outputFile);
            labelPrefix = outputFile.getName().replaceFirst("(?i)\\.vm$", "");
        } catch (IOException e){
            System.out.print("vmwriter 1");
        }
    }

    public void w(String s){
        try {
            System.out.println(s);
            fw.write(s + "\n");
        } catch (IOException e){
            // theres no way ts breaking man im not writing a catch
            System.out.print("vmwriter 2");
        }
    }


    // just for the record i know the switches and the enums are stupid but i'm just followed specification
    public void writePush(String segment, int index) { // Writes a VM push command.
        w("push " + segment + " " + index);
    }
    
    public void writePop(String segment, int index) { // Writes a VM pop command.
        w("pop " + segment + " " + index);
    }

    public void writeArithmetic(String c) { // Writes a VM arithmetic command.
        w(c);
    }

    public void writeLabel(String label){ // Writes a VM label command.
        w("label " + labelPrefix + "$" + label);
    }

    public void writeGoto(String label){ // Writes a VM goto command.
        w("goto " + labelPrefix + "$" + label);
    }

    public void writeIf(String label){ // Writes a VM if-goto command.
        w("if-goto " + labelPrefix + "$" + label);        
    }

    public void writeCall(String name, int nArgs){ // Writes a VM call command.
        w("call " + name + " " + nArgs);
    }

    public void writeFunction(String className, String functionName, int nLocals){ // Writes a VM function command.
        w("function " + className + "." + functionName + " " + nLocals);
    }

    public void writeReturn(){ // Writes a VM return command.
        w("return");
    }

    public void close(){ // Closes the output file
        try{
            System.out.println("closing vmwriter!!!");
            fw.close();
        } catch (IOException e){
            System.out.print("vmw close broke");
        }
    }
}