import java.io.*;

public class CodeWriter
{
	File outFile;
	FileWriter printWriter;
	String filename;
	int qwer = 1;
	String staticname;
	
	/* The project for chapter 7 only deals with arithmetic command and push/pop.
		functions, call, return, label will be dealt with in chapter 8.	*/
	
	
	/* The constructor opens an output file/stream and gets ready to 
		write into it. The argument will identify the output file / stream.
		This can be done by sending in a stream */
		
	public CodeWriter(FileWriter FW)
	{
		printWriter = FW;
	}
	
	
	public void setFileName(String inFileName) 
	{
		filename = inFileName;			
		staticname = filename.replace("vm", "").substring((filename.lastIndexOf("/")+1), (filename.indexOf(".")+1));
	}
	
	public void w(String cmd){
		try{
		printWriter.write(cmd+"\n");
		}catch(Exception e){
		
		}
	}
		
	public void writeArithmetic(String command)
	{
		//System.out.println(command);
		try{
			switch(command){
				case "add": 
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("M=M+D");
								break;		
				case "sub": 
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("M=M-D");
								break;	
				case "neg": 
								w("@SP");
								w("A=M-1");
								w("M=-M");
								break;
				case "eq": 
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("D=M-D");
								w("M=-1");
								w("@eq" + qwer);
								w("D;JEQ");
								w("@SP");
								w("A=M-1");
								w("M=0");
								w("(eq" + qwer + ")");
								qwer++;
								break;
				case "gt":
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("D=M-D");
								w("M=-1");
								w("@gt" + qwer);
								w("D;JGT");
								w("@SP");
								w("A=M-1");
								w("M=0");
								w("(gt" + qwer + ")");
								qwer++;
								break;
				case "lt": 
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("D=M-D");
								w("M=-1");
								w("@lt" + qwer);
								w("D;JLT");
								w("@SP");
								w("A=M-1");
								w("M=0");
								w("(lt" + qwer + ")");
								qwer++;
								break;
				case "and": 
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("M=D&M");
								break;		
				case "or": 
								w("@SP");
								w("AM=M-1");
								w("D=M");
								w("A=A-1");
								w("M=D|M");
								break;
				case "not": 
								w("@SP");
								w("A=M-1");//go to top of stack
								w("M=!M");//make it not itself
								break;
							
				default:        
								w("?\n");
							}
			} catch (Exception e){
								// :)
			}
		
		// This function should present a series of checks to determine which
		// kind of command we are dealing with. Based on the command, the proper
		// series of Hack assembly commands must be output to the file.
	}
	
	/* The book indicates command as either C_PUSH or C_POP. This would suggest the
		use of a Java ENUM. To make sure the prep documents are compilable, String
		was used instead of an ENUM. The coders should feel free to replace the
		parameter type as fits their implementation. */
		
	//push whatevers on the index after the address of ___

	public void push(String cmd, int index){
		try{
			w("@"+index);
			w("D=A");//D is whatever the index is
			w("@"+cmd);
			w("A=D+M");//address is @segment + the index
			w("D=M");//D now stores the value of the right address
			w("@SP");
			w("M=M+1");//SP is now up one
			w("A=M-1");//we go to where SP previously was (which was on top of the stack)
			w("M=D");//and now the top of the stack is D
		}catch(Exception e){
		
		}
	}
	
	public void push(String address){
		try{
			w(address);//go straight to cmd
			w("D=M");//D now stores the value of the stuff
			w("@SP");
			w("M=M+1");//SP is now up one
			w("A=M-1");//we go to where SP previously was (which was on top of the stack)
			w("M=D");//and now the top of the stack is D
		}catch(Exception e){
		
		}
	}
	
	public void pop(String cmd, int index){
		try{
			w("@SP");
			w("AM=M-1");//go to the top of the stack minus one, and change SP to minus one
			w("D=M");//D is whatever was in there
			w("@R13");//R13 is temp storage
			w("M=D");//R13 has the top stack's contents
			w("@"+index);
			w("D=A");//now D is the index
			w("@"+cmd);
			w("A=M+D");
			w("D=A");//now D is the index + pointer
			w("@R14");
			w("M=D");//R14 has the true address
			w("@R13");
			w("D=M");//D is the true storage
			w("@R14");
			w("A=M");//now we at the true address
			w("M=D");//handoff
		}catch(Exception e){
		
		}
	}
	
	public void pop(String address){
		try{
			w("@SP");
			w("AM=M-1");//go to the top of the stack minus one, and change SP to minus one
			w("D=M");//D is whatever was in there
			w(address);
			w("M=D");//handoff
		}catch(Exception e){
		
		}
	}
	
	public void writePushPop(String command, String segment, int index)
	{				
		try{
		//w("//"+command+" "+segment+" "+index);
		if(command.equals("push")){
				switch(segment){
					case "argument": //WORKS
										push("ARG", index);
										break;
					case "local": //WORKS
										push("LCL", index);
										break;
					case "this": //WORKS
										push("THIS", index);
										break;
					case "that": //WORKS
										push("THAT", index);
										break;
					case "static":		
										push("@"+staticname+index);
										break;
					case "constant": //WORKS
										w("@"+index);
										w("D=A");
										w("@SP");
										w("M=M+1");
										w("A=M-1");
										w("M=D");
										break;
					case "pointer"://WORKS
									switch(index){
										case 0:
													push("@THIS");
													break;
										case 1:
													push("@THAT");
													break;
										default: 
													w("pointer error");
													break;
									};
					case "temp": //WORKS
										w("@R" + (index+5));
										w("D=M");
										w("@SP");
										w("M=M+1");
										w("A=M-1");
										w("M=D");
										break;
					default:
										w("error man :/");
				};
			}else{//POP
				switch(segment){
					case "argument": //WORKS
										pop("ARG", index);
										break;
					case "local": //WORKS
										pop("LCL", index);
										break;
					case "this": //WORKS
										pop("THIS", index);
										break;
					case "that": //WORKS
										pop("THAT", index);
										break;
					case "static"://probably fix this and everything else under here
										pop("@"+staticname+index);
										break;
					case "pointer"://WORKS
									switch(index){
										case 0:
													pop("@THIS");
													break;
										case 1:
													pop("@THAT");
													break;
										default: 
													w("pointer error");
													break;
										};
													
					case "temp": //WORKS
													pop("@R"+(index+5));
													break;
					default:
													w("error popping");
													break;
					};
				}
				
		}catch(Exception e){}
		// This function should present a series of checks to determine which
		// command we are dealing with and the segment. The proper
		// series of Hack assembly commands must be output to the file.	
		
	/* As the assembly code will be manipulating the D register frequently, the 
		coders should consider creating pushD and popD private methods outputting
		the proper assembly. This will save a lot of code repetition earlier. */
	}
	
    public void writerInit() {
        w("@256");
        w("D=A");
        w("@SP");
        w("M=D");
	}
		
	public void writeLabel(String label) {
		w("(" + label + ")");
	}
	
	public void writeGoto(String label) {
		w("@" + label);
		w("0:JMP");
	}
	
	public void writeIf(String label) {
		w("@SP");
		w("AM=M-1");
		w("D=M");
		w("@" + label);
		w("D;JNE");
	}
	
//	public void writeCall(String functionName, int numArgs) {
//		w("call " + functionName + " " + numArgs);
//	}
//	
//	public void writeReturn() {
//		w("return");
//	}
//	
//	public void writeFunction(String functionName, int numLocals) {
//		w("function " + functionName + " " + numLocals);
//	}

	public void close(){
		try {
			printWriter.close();
		} catch (Exception e){
		//:)
		}
	}
}

