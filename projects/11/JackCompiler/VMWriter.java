import java.io.*;

public class VMWriter {

    public File outputFile;
    public FileWriter fw;
    public String labelPrefix;

    public VMWriter(String outputFilePath){ // Creates a new file and prepares it for writing.
        try {
            outputFile = new File(outputFilePath);
            fw = new FileWriter(outputFile);
            labelPrefix = outputFile.getName().replaceFirst("(?i)\\.vm$", "");
        } catch (Exception e){
            // theres no way ts breaking man im not writing a catch
        }
    }

    public void w(String s){
        try {
            fw.write(s + "\n");
        } catch (Exception e){
            // theres no way ts breaking man im not writing a catch
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

    public void writeFunction(String name, int nLocals){ // Writes a VM function command.
        w("function " + name + " " + nLocals);
    }

    public void writeReturn(){ // Writes a VM return command.
        w("return");
    }

    public void close(){ // Closes the output file
        try{
            fw.close();
        } catch (Exception e){
            // it wont happen bro trust
        }
    }
}