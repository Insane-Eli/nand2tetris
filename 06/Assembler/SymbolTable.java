import java.util.Hashtable;

public class SymbolTable {
 	static public int nextFreeSpot = 16;
 public static Hashtable<String, Integer> symbolMap;
 	static {
 	symbolMap = new Hashtable<>();
 // Puts all the primitive symbols
	symbolMap.put("R0", 0); symbolMap.put("SP", 0);
	symbolMap.put("R1", 1); symbolMap.put("LCL", 1);
	symbolMap.put("R2", 2); symbolMap.put("ARG", 2);
	symbolMap.put("R3", 3); symbolMap.put("THIS", 3);
	symbolMap.put("R4", 4); symbolMap.put("THAT", 4);
	symbolMap.put("R5", 5);
	symbolMap.put("R6", 6);
	symbolMap.put("R7", 7);
	symbolMap.put("R8", 8);
	symbolMap.put("R9", 9);
	symbolMap.put("R10", 10);
	symbolMap.put("R11", 11);
	symbolMap.put("R12", 12);
	symbolMap.put("R13", 13);
	symbolMap.put("R14", 14);
	symbolMap.put("R15", 15);
	symbolMap.put("SCREEN", 16384);
	symbolMap.put("KBD", 24576);
	}
	
	public static void addEntry(String label){
		symbolMap.put(label, nextFreeSpot);
		nextFreeSpot++;
	}
	
	public static void addEntry(String label, int linenumber){
		symbolMap.put(label, linenumber);
	}
}
