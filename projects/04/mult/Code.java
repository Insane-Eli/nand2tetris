import java.util.*;
public class Code{
	int dest(int dest){
		switch(dest){
		case "M": return 001;
		case "D": return 010;
		case "MD": return 011;
		case "A": return 100;
		case "AM": return 101;		
		case "AD": return 110;
		case "AMD": return 111;
		default: return 000;
		}
	}
	public int comp(int comp){
	return comp;
	//7bits
	}
	public int jump(int jump){
	return jump;
	//3bits
	}
}