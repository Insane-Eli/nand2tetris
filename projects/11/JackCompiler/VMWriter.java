import java.io.*;

public class VMWriter {

    public enum Segment {
        CONST,
        ARG, 
        LOCAL, 
        STATIC,
        THIS, 
        THAT, 
        POINTER, 
        TEMP
    }

    public enum command {
        ADD, 
        SUB, 
        NEG, 
        EQ, 
        GT, 
        LT, 
        AND, 
        OR, 
        NOT
    }

    public VMWriter(File f){ // Creates a new file and prepares it for writing.

    }

    public void writePush(Segment s, int Index){ // Writes a VM push command.

    }

    public void writePop(Segment s, int Index){ // Writes a VM pop command.
        
    }

    public void writeArithmetic(command c){ // Writes a VM arithmetic command.
        
    }

    public void writeLabel(String label){ // Writes a VM label command.
        
    }

    public void writeGoto(String label){ // Writes a VM goto command.
        
    }

    public void writeIf(String label){ // Writes a VM if-goto command.
        
    }

    public void writeCall(String name, int nArgs){ // Writes a VM call command.
        
    }

    public void writeFunction(String name, int nLocals){ // Writes a VM function command.
        
    }

    public void writeReturn(){ // Writes a VM return command.
        
    }

    public void close(){ // Closes the output file

    }
}