import java.util.*;

public class SymbolTable {

    public enum VarKind {
        STATIC, 
        FIELD, 
        ARG, 
        VAR, 
        NONE
    }

    public Hashtable<String, VarData> classVars;
    public Hashtable<String, VarData> subroutineVars;

    public class VarData{
        public String type;
        public VarKind kind;
        public int index;
        public VarData(String type, VarKind kind, int index){
            this.type = type;
            this.kind = kind;
            this.index = index;
        }
    }

    public int staticCount;
    public int fieldCount;
    public int argCount;
    public int varCount;

    public SymbolTable(){ // Creates a new empty symbol table.

        System.out.println("DONE!");

        classVars = new Hashtable<>();
        subroutineVars = new Hashtable<>();

        staticCount = 0;
        fieldCount = 0;
        argCount = 0;
        varCount = 0;
    }

    public void startSubroutine(){ // Starts a new subroutine scope (i.e., resets the subroutine's symbol table)
        subroutineVars = new Hashtable<>();
        argCount = 0;
        varCount = 0;

    }

    // Defines a new identifier of a given name, type, and kind and issigns it a running index
    // STATIC and FIELD identifiers have a class scope, while ARG and VAR identifiers have a subroutine scope.
    public void Define(String name, String type, VarKind kind){

        VarData d;
        switch(kind){
            case STATIC -> {
                d = new VarData(type, kind, staticCount++);
                classVars.put(name, d);
            }
            case FIELD -> {
                d = new VarData(type, kind, fieldCount++);
                classVars.put(name, d);
            }
            case ARG -> {
                d = new VarData(type, kind, argCount++);
                subroutineVars.put(name, d);
            }
            case VAR -> {
                d = new VarData(type, kind, varCount++);
                subroutineVars.put(name, d);
            }
            default -> System.out.println("somthing messed up :|");
        }
    }

    public int VarCount(VarKind kind){ // Returns the number of variables of the given kind already defined in the current scope.
        return switch(kind){
            case STATIC -> staticCount;
            case FIELD -> fieldCount;
            case ARG -> argCount;
            case VAR -> varCount;
            default -> -1;
        };
    }

    public VarKind KindOf(String name){ // Returns the kind of the named identifier in the current scope. If the identifier is unknown in the current scope, returns NONE.
        if (subroutineVars.containsKey(name)) {
            return subroutineVars.get(name).kind;
        } else if (classVars.containsKey(name)){
            return classVars.get(name).kind;
        } else {
            System.out.println("\nBad Kind");
            errorReport(name);
            return VarKind.NONE;
        }
    }

    public String TypeOf(String name){ // Returns the type of the named identifier in the current scope
        if (subroutineVars.containsKey(name)) {
            return subroutineVars.get(name).type;
        } else if (classVars.containsKey(name)){
            return classVars.get(name).type;
        } else  {
            System.out.println("\nBad Type");
            errorReport(name);
            return "";
        }
    }

    public int IndexOf(String name){ // Returns the index assigned to the named identifier.
         if (subroutineVars.containsKey(name)) {
            return subroutineVars.get(name).index;
        } else if (classVars.containsKey(name)){
            return classVars.get(name).index;
        } else {
            System.out.println("\nBad Index");
            errorReport(name); // lets just turn this off for now
            return -1;
        }
    }

    private void errorReport(String name){
        System.out.println(
        "Error: " + name + "\n" +
        "staticCount: " + staticCount + "\n" +
        "fieldCount: " + fieldCount + "\n" +
        "argCount: " + argCount + "\n" +
        "varCount: " + varCount);
    }
}