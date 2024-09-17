import java.lang.*;
import java.io.*;
public class Parser {
	private FileWriter fileWriter;
	private String line;
	private int linenumber;
	public Parser(String inFileName, String outFileName) {
		
		boolean checkedForLabels = false;
		try {
			linenumber = 0;
			//initializes file reader+ buffered reader + new file + file writer
			FileReader fileReader = new FileReader(inFileName);
			BufferedReader labelChecker = new BufferedReader(fileReader);
						
			while ((line = labelChecker.readLine()) != null) {
				CheckForLabels(linenumber);
			}
			
			labelChecker.close();
				
			FileReader fileReader2 = new FileReader(inFileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader2);
			
			File outFile = new File(outFileName);
			fileWriter = new FileWriter(outFile);
			
			while ((line = bufferedReader.readLine()) != null) {
				advance();
			}
			bufferedReader.close();
			fileWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isInt(String a){
		try{
		Integer.parseInt(a);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	private void CheckForLabels(int x){
		if (line.startsWith("(") && line.endsWith(")")){
			SymbolTable.addEntry(line.substring(1, line.indexOf(")")), x);		
		} else {
			linenumber++;
		}
	}

	private void advance(){
		try{
			if(!(line.startsWith("//")||line.isBlank())){
				if(line.startsWith("@")){
					fileWriter.write(symbol());//@
					fileWriter.write(System.lineSeparator()); 
				} else if (line.startsWith("(") && line.endsWith(")")){
					// '\_(:/)_/'
				} else {
				
					fileWriter.write("111");
					
					
				//DEST = COMP ; JUMP
					
					if(line.contains("=") && line.contains(";")){//COMP AND JUMP
						fileWriter.write(Code.comp(line.substring(line.indexOf("=")+1,line.indexOf(";"))));
						fileWriter.write(Code.dest(line.substring(0,line.indexOf("="))));
						fileWriter.write(Code.jump(line.substring(line.indexOf(";")+1)));
					}else if(line.contains("=")){//JUST COMP
						fileWriter.write(Code.comp(line.substring(line.indexOf("=")+1)));
						fileWriter.write(Code.dest(line.substring(0,line.indexOf("="))));
						fileWriter.write("000");
					}else if(line.contains(";")){//JUST JUMP
						fileWriter.write(Code.comp(line.substring(0,line.indexOf(";"))));
						fileWriter.write("000");
						fileWriter.write(Code.jump(line.substring(line.indexOf(";")+1)));
					}else{//NEITHER
						fileWriter.write("0000000");
						fileWriter.write(Code.dest(line));
						fileWriter.write("000");
					}
					fileWriter.write(System.lineSeparator()); 
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

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

