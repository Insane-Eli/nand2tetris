
import java.io.*;

public class CodeWriter {

    FileWriter printWriter;
    String fileName;
    String staticName;
    int comparisonLabelTag = 1;
    int functionLabelTag = 0;

    /* The project for chapter 7 only deals with arithmetic command and push/pop.
		functions, call, return, label will be dealt with in chapter 8.	*/

    /* The constructor opens an output file/stream and gets ready to 
		write into it. The argument will identify the output file / stream.
		This can be done by sending in a stream */

    public CodeWriter(FileWriter FW) {
        printWriter = FW;
		writerInit();
    }

     // this is the write command that writes to the new file, but was shortened to 'w' so it could be typed fast
    public void w(String segment) {
        try {
            printWriter.write(segment + "\n");
        } catch (IOException e) {
			System.out.println("IOException: CodeWriter.w()");
        }
    }

    // I know this is a silly setup since we could throw this all in the constructor, but it says we need a setFileName() function in the book, so I must've done something a different way somewhere along the road that made it useless?
    public void setFileName(String inFileName) { 
        fileName = inFileName;
        staticName = new File(fileName).getName().replace(".vm", ""); // only takes the name of the file without including the .vm extension or directory, used for static variables
    }

    // This is the assembly for all of the arithmetic commands.
    public void writeArithmetic(String command) {
        try {
            switch (command) {
                case "add" -> {
                    w("// add");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("M=D+M"); // Add the two together
                }

                case "sub" -> {
                    w("// sub");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("M=M-D"); // Subtract the two (element below - element above)
                }

                case "neg" -> {
                    w("// neg");
                    w("@SP"); // Go to the stack pointer
                    w("A=M-1"); // Go to whatever's at the top of the stack
                    w("M=-M"); // Make it negative
                }

                case "eq" -> {
                    w("// eq");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("D=M-D"); // Subtract the two (element below - element above) and store it
                    w("M=-1"); // Set the top of the stack to -1 (all 1s) for now, and we'll change it if the comparison comes out false
                    w("@eq" + comparisonLabelTag); // Prepare to jump ahead
                    w("D;JEQ"); // Jump ahead if the comparison comes out as true, else...
                    w("@SP"); // Go to the stack pointer
                    w("A=M-1"); // Go to whatever's at the top of the stack
                    w("M=0"); // Set the top of the stack to false (all 0s)
                    w("(eq" + comparisonLabelTag + ")"); // If the comparison comes out as true, we'll have jumped here to avoid setting the top of the stack to false
                    comparisonLabelTag++; // Lastly, incriment comparisonLabelTag so that the next run through will have a new tag
                }

                case "gt" -> {
                    w("// gt");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("D=M-D"); // Subtract the two (element below - element above) and store it
                    w("M=-1"); // Set the top of the stack to -1 (all 1s) for now, and we'll change it if the comparison comes out false
                    w("@gt" + comparisonLabelTag); // Prepare to jump ahead
                    w("D;JGT"); // Jump ahead if the comparison comes out as true, else...
                    w("@SP"); // Go to the stack pointer
                    w("A=M-1"); // Go to whatever's at the top of the stack
                    w("M=0"); // Set the top of the stack to false (all 0s)
                    w("(gt" + comparisonLabelTag + ")"); // If the comparison comes out as true, we'll have jumped here to avoid setting the top of the stack to false
                    comparisonLabelTag++; // Lastly, incriment comparisonLabelTag so that the next run through will have a new tag
                }

                case "lt" -> {
                    w("// lt");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("D=M-D"); // Subtract the two (element below - element above) and store it
                    w("M=-1"); // Set the top of the stack to -1 (all 1s) for now, and we'll change it if the comparison comes out false
                    w("@lt" + comparisonLabelTag); // Prepare to jump ahead
                    w("D;JLT"); // Jump ahead if the comparison comes out as true, else...
                    w("@SP"); // Go to the stack pointer
                    w("A=M-1"); // Go to whatever's at the top of the stack
                    w("M=0"); // Set the top of the stack to false (all 0s)
                    w("(lt" + comparisonLabelTag + ")"); // If the comparison comes out as true, we'll have jumped here to avoid setting the top of the stack to false
                    comparisonLabelTag++; // Lastly, incriment comparisonLabelTag so that the next run through will have a new tag
                }

                case "and" -> {
                    w("// and");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("M=D&M"); // And the two together
                }

                case "or" -> {
                    w("// or");
                    w("@SP"); // Go to the stack pointer
                    w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
                    w("D=M"); // Store whatever used to be at the top of the stack
                    w("A=A-1"); // Go down one (to what is now the top of the stack)
                    w("M=D|M"); // Or the two together
                }

                case "not" -> {
                    w("// not");
                    w("@SP"); // Go to the stack pointer
                    w("A=M-1"); // Go to whatever's at the top of the stack
                    w("M=!M"); // Make it not itself
                }

                default -> w("Error: Unknown arithmetic");
            }

        } catch (Exception e) {
            System.out.println("Exception: CodeWriter.writeArithmetic()");
        }

        // This function should present a series of checks to determine which
        // kind of command we are dealing with. Based on the command, the proper
        // series of Hack assembly commands must be output to the file.
    }


    // This function should present a series of checks to determine which
    // command we are dealing with and the segment. The proper
    // series of Hack assembly commands must be output to the file.	

    /* As the assembly code will be manipulating the D register frequently, the 
    coders should consider creating pushD and popD private methods outputting
    the proper assembly. This will save a lot of code repetition earlier. */

    public void writePushPop(String command, String segment, int index) {
        
		try {

            w("// " + command + " " + segment + " " + index);

            if (command.equals("push")) { // Push commands
                switch (segment) {
                    case "argument" -> push("ARG", index);

                    case "local" -> push("LCL", index);

                    case "this" ->  push("THIS", index);

                    case "that" -> push("THAT", index);

                    case "static" -> push("@" + staticName + "." + index);

                    case "constant" -> {
                        w("@" + index);
                        w("D=A");
                        w("@SP");
                        w("M=M+1");
                        w("A=M-1");
                        w("M=D");
                    }

                    case "pointer" -> {
                        switch (index) {
                            case 0 -> push("@THIS");

                            case 1 -> push("@THAT");

                            default -> w("Error: writePushPop(): push: pointer case switch");
                        }
                    }

                    case "temp" -> {
                        w("@R" + (index + 5));
                        w("D=M");
                        w("@SP");
                        w("M=M+1");
                        w("A=M-1");
                        w("M=D");
                    }

                    default -> w("Error: writePushPop(): push switch ");
                }

            } else { // Pop commands
                switch (segment) {
                    case "argument" -> pop("ARG", index);

                    case "local" -> pop("LCL", index);

                    case "this" -> pop("THIS", index);

                    case "that" -> pop("THAT", index);

                    case "static" -> pop("@" + staticName + "." + index);

                    case "pointer" -> {
                        switch (index) {

                            case 0 -> pop("@THIS");

                            case 1 -> pop("@THAT");

                            default -> w("Error: writePushPop(): pop: pointer case switch");
                        }
                    }

                    case "temp" -> pop("@R" + (index + 5));

                    default -> w("Error: writePushPop(): pop switch");
                }
            }
        } catch (Exception e) {
			System.out.println("Exception: CodeWriter.writePushPop()");
        }
    }

    /* The book indicates command as either C_PUSH or C_POP. This would suggest the
		use of a Java ENUM. To make sure the prep documents are compilable, String
		was used instead of an ENUM. The coders should feel free to replace the
		parameter type as fits their implementation. */

    // This push() pushes whatever's inside the spot (index) spaces above (@segment)
    public void push(String segment, int index) {
        try {
            w("@" + index); // Go to the address of the index
            w("D=A"); // Store the address (index) in D
            w("@" + segment); // Go to the address of the segment
            w("A=D+M"); // Go to the address of the segment + the index
            w("D=M"); // D now stores the memory of the segment + index
            w("@SP"); // Go to the stack pointer
            w("M=M+1"); // Move the stack pointer up
            w("A=M-1"); // Go to where the stack pointer previously was (above the top of the stack)
            w("M=D"); // Make the new top of the stack whatever was in D
        } catch (Exception e) {
			System.out.println("Exception: CodeWriter.push(segment, index)");
        }
    }

    // This push() pushes whatever's inside (address)
    public void push(String address) {
        try {
            w(address); // Go to the address of the address
            w("D=M"); // D now stores the memory of the address
            w("@SP"); // Go to the stack pointer
            w("M=M+1"); // Move the stack pointer up
            w("A=M-1"); // Go to where the stack pointer previously was (above the top of the stack)
            w("M=D"); // Make the new top of the stack whatever was in D
        } catch (Exception e) {
			System.out.println("Exception: CodeWriter.push(address)");
        }
    }

    // This pop() pops whatever's at the top of the stack to the spot (index) spaces above (@segment)
    public void pop(String segment, int index) {
        try {
            w("@SP"); // Go to the stack pointer
            w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
            w("D=M"); // Store whatever used to be at the top of the stack
            w("@R13"); // Go to R13 (temp storage)
            w("M=D"); // Put whatever used to be at the top of the stack into R13
            w("@" + index); // Go to the address of the index
            w("D=A"); // Store the address (index) in D
            w("@" + segment); // Go to the address of the segment
            w("A=D+M"); // Go to the address of the segment + the index
            w("D=A"); // now D is the right address (index + segment)
            w("@R14"); // Go to R14 (temp storage)
            w("M=D"); // R14 has the (right address)
            w("@R13"); // Go back to R13 (storing the value from the top of the stack)
            w("D=M"); // D has whatever used to be at the top of the stack
            w("@R14"); // Go back to R14 (storing the right address)
            w("A=M"); // Go to the right address
            w("M=D"); // The right value is now in the memory of the right address
        } catch (Exception e) {
			System.out.println("Exception: CodeWriter.pop(segment, index)");
        }
    }

    // This pop() pops whatever's at the top of the stack to the spot (address)
    public void pop(String address) {
        try {
            w("@SP"); // Go to the stack pointer
            w("AM=M-1"); // Go to the top of the stack + move the stack pointer down in advance
            w("D=M"); // Store whatever used to be at the top of the stack
            w(address); // Go to the address of the address
            w("M=D"); // The value is now in the memory of (address)
        } catch (Exception e) {
			System.out.println("Exception: CodeWriter.pop(address)");
        }
    }

    
    // Set the value of @SP to 256
    private void writerInit() {
        w("// writerInit");
        w("@256");
        w("D=A");
        w("@SP");
        w("M=D");
    }

    public void writeLabel(String label) {
        w("// writeLabel " + label);
        w("(" + staticName + "$" + label + ")");
    }

    public void writeGoto(String label) {
        w("// writeGoto " + label);
        w("@" + staticName + "$" + label);
        w("0;JMP");
    }

    public void writeIf(String label) {
        w("// writeIf " + label);
        w("@SP");
        w("AM=M-1");
        w("D=M");
        w("@" + staticName + "$" + label);
        w("D;JNE");
    }

	public void writeCall(String functionName, int numArgs) {

        w("// call " + functionName + " " + numArgs);

        // First and foremost lets give our function a unique name
        functionLabelTag++;
        String returnLabel = functionName + "$ret." + functionLabelTag;

        // Next we gotta save the return address so we know where to put stuff once the function finishes
        // Since we're saving the address and not the memory inside of the address, we can't use our push function, so we have to do it manually
        w("@" + returnLabel); // Go to the address of the address
        w("D=A"); // D now stores the address (this is the line we had to change from push())
        w("@SP"); // Go to the stack pointer
        w("M=M+1"); // Move the stack pointer up
        w("A=M-1"); // Go to where the stack pointer previously was (above the top of the stack)
        w("M=D"); // Make the new top of the stack whatever was in D

        // And we gotta save wherever the segment pointers were so we can put them back once the function finishes
        push("@LCL"); // LCL
        push("@ARG"); // ARG
        push("@THIS"); // THIS
        push("@THAT"); // THAT

        // Since we have 5 things that are now on the stack, we have to store wherever the stack pointer ORIGINALLY was inside of ARG
        w("@SP"); // Go to the stack pointer
        w("D=M"); // D now stores the stack pointer
        w("@" + (numArgs + 5)); // Number of arguments + the 5 things we saved
        w("D=D-A"); // Now D stores where the stack pointer ORIGINALLY was before we threw all of the segments and arguments on it
        w("@ARG"); // Go to ARG
        w("M=D"); // ARG now stores the OG stack pointer

        // And the address of whatever the stack pointer is NOW goes inside LCL
        w("@SP"); // Go to the stack pointer
        w("D=M"); // D now stores the stack pointer
        w("@LCL"); // Go to LCL
        w("M=D"); // LCL now stores the NEW stack pointer

        // Last but not least, we have to jump to the start of the function using our GOTO function
        w("@" + functionName); // Go to our functions address
        w("0;JMP"); // And jump to that label

        // And make a return label so we know where to come back once the function finishes
        w("(" + returnLabel + ")");
	}
	
	public void writeReturn() {
		w("// return");

        
	}
	
	public void writeFunction(String functionName, int numLocals) {
		w("// function " + functionName + " " + numLocals);
	}

    // Close the printWriter
    public void close() {
        try {
            printWriter.close();
        } catch (IOException e) {
            System.out.print("IOException: codeWriter.close()");
        }
    }
}
