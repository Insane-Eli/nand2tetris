import java.lang.*;
import java.io.*;
public class Parser {
	private FileWriter fileWriter; // were gonna be writing to the file we just made
	private String line; // whatever we see in the initial file goes in here
	private int linenumber; // and we gotta keep track of what line we're on

	public Parser(String inFileName, String outFileName) { // we just got the file names from the assembler, now let's do something with them
		
		boolean checkedForLabels = false;
		try {

			linenumber = 0; // at the start of a file, the first line is 0

			FileReader fileReader = new FileReader(inFileName); // lets make something to read the og file
			BufferedReader labelChecker = new BufferedReader(fileReader); // and then let's check if the og file has any labels
						
			//i'm guessing labels are kind of bs and it was better to just get those jawns out the way
			while ((line = labelChecker.readLine()) != null) { // while theres lines in the og file left to check for labels
				CheckForLabels(linenumber); // check them for labels
				// before we even write anything to the new file, we should just note all the labels and config them so that when we do we know what to 
			}
			
			labelChecker.close(); // ok now we checked everything so let's just close the checker :D
			fileReader.close(); // i'm just guessing we should do this too but could break stuff so maybe delete this too
			
			FileReader fileReader2 = new FileReader(inFileName); // anyways since we just read the file one time for the labels, and ig we can't re use the first file reader, now we gotta make a whole nother one for all the non-label shart 
			BufferedReader bufferedReader = new BufferedReader(fileReader2); // and the buffered reader this time will just check for regular code instead of the label bs
			
			File outFile = new File(outFileName); // now let's actually make the new file we're gonna write too
			fileWriter = new FileWriter(outFile); // and something that can write to it
			
			while ((line = bufferedReader.readLine()) != null) { // while there's lines left to translate
				advance(); // translate them 
			}

			// now we're done so just close everything
			bufferedReader.close();
			fileReader2.close(); // also just added this maybe delete later if it breaks shart
			fileWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//why isn't this a built-in function :(
	private boolean isInt(String a){
		try{
		Integer.parseInt(a);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	// if theres a label there, add it to the symbol table where we keep the info on our labels
	private void CheckForLabels(int x){
		if (line.startsWith("(") && line.endsWith(")")){
			SymbolTable.addEntry(line.substring(1, line.indexOf(")")), x);		
		} else {
			linenumber++;
		}
	}

	//now that we're writing to our file, let's figure out what type of command the line is
	private void advance(){
		try{
			if(!(line.startsWith("//")||line.isBlank())){ // ignore all comments

				/*Side Note:
				 * an address switch moves around the memory
				 * a jump moves around the program
				 */

				if(line.startsWith("@")){ // @ means switch address
					fileWriter.write(symbol()); // we keep all the primitive symbols in the symbol table as well, so if it's an address switch, just go there
					fileWriter.write(System.lineSeparator()); //hit a line seperator after putting in each command
				} else if (line.startsWith("(") && line.endsWith(")")){ // () means label
					// '\_(:/)_/' (i guess we do nothing in this case??)
				} else {
				
					fileWriter.write("111"); // if it's not either of those then it's a comp
					
					
				//DEST = COMP ; JUMP
					
					if(line.contains("=") && line.contains(";")){ // COMP AND JUMP
						fileWriter.write(Code.comp(line.substring(line.indexOf("=")+1,line.indexOf(";"))));
						fileWriter.write(Code.dest(line.substring(0,line.indexOf("="))));
						fileWriter.write(Code.jump(line.substring(line.indexOf(";")+1)));
					}else if(line.contains("=")){ //JUST COMP
						fileWriter.write(Code.comp(line.substring(line.indexOf("=")+1)));
						fileWriter.write(Code.dest(line.substring(0,line.indexOf("="))));
						fileWriter.write("000");
					}else if(line.contains(";")){ //JUST JUMP
						fileWriter.write(Code.comp(line.substring(0,line.indexOf(";"))));
						fileWriter.write("000");
						fileWriter.write(Code.jump(line.substring(line.indexOf(";")+1)));
					}else{ //NEITHER
						fileWriter.write("0000000");
						fileWriter.write(Code.dest(line));
						fileWriter.write("000");
					}

					//i'm not gonna explain what's up there but just know it calls the right functions and does it's job
					fileWriter.write(System.lineSeparator()); // hit a line seperator after putting in each command
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

/*SPECIAL NOTE:
 * if you @ a random word, in the symbol talbe, it will just assign that word to a new memory spot, kind of like a variable
 * so you can say @balls, and call it later on and it will grab the same address
 * 
 * if theres a (LABEL) and you say @balls, it will go to the spot in the code that says (BALLS) and start from there
 * 
 * that being said, i have no idea what i did to do that but its down here idk?
 * 
 * 
 * 
 * JUMPS will move between code
 * 
 * @s will just put shart in the address
 * 
 * Labels are just there to reference the line that we need to jump to
 * so to get to (BALLS)
 * we need to
 * @BALLS
 * 0:JMP;
 */

	private String symbol(){
		if(isInt(line.substring(1))){ 
			return "0" + String.format("%15s", Integer.toBinaryString(Integer.parseInt(line.substring(1)))).replace(' ', '0');
		} else if (SymbolTable.symbolMap.containsKey(line.substring(1))){
			line = "@" + String.valueOf(SymbolTable.symbolMap.get(line.substring(1)));
			return symbol(); 
		} else {
			SymbolTable.addEntry(line.substring(1));
			return symbol();
		}
	}
}

