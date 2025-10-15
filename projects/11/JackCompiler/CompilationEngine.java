
import java.io.*;
import java.util.*;


/**
 * @author tha one and only TOP SHOTTA Eli "Insane Eli" Currah (fan-made name)
 * CompilationEngine will serve as the focus for most of the compilers
 * functionality. Based on the grammar delineated in section 10.3.3, each method
 * will output XML and make appropriate calls to other methods. This recursive
 * process means that the caller only needs to call compileClass for each file
 * and all other compilations will occur as needed.  *
 * Note: In chapter 10, the program outputs XML. This includes markup for
 * symbols, keywords, and identifiers. Making compile methods for these token
 * types would be prudent.
 *
 * The book's camel-case suggestions are inconsistent in section 10.3.3 with
 * about half capitalizing the first letter and half leaving it lower-case. We
 * will use strict camel-case.  *
 */
public class CompilationEngine {

    private static int tabs;
    private static BitSet hasMoreBranches = new BitSet();

    private static void print(String s){
        
        for(int i = 0; i < tabs-1; i++){
            System.out.print(hasMoreBranches.get(i) ? "│  " : "░░░");
        }
        
        // System.out.print("checking out index: " + (hasMoreBranches.size()-1) + " which reads: " + hasMoreBranches.get(hasMoreBranches.size()-1));

        if(tabs != 0) System.out.print(hasMoreBranches.get(tabs-1) ?  "├─ " : "└─ ");

        System.out.print(s);
    }

    private JackTokenizer tokenizer;
    private SymbolTable st;
    private VMWriter vmw;
    private String className;

    /**
     * @param input Jack file The CompilationEngine will create a new
     * JackTokenizer object based on the inputFile and will create an output
     * stream to outputFile. *
     */
    public CompilationEngine(String inputFile, String outputFile) {
        System.out.println("DONE!");

        try {
            System.out.print("Creating new Tokenizer... ");
            tokenizer = new JackTokenizer(new FileReader(inputFile));

            System.out.print("Creating new SymbolTable... ");
            st = new SymbolTable();

            System.out.print("Creating new VMWriter... ");
            vmw = new VMWriter(outputFile);
            System.out.println();
            
            compileClass(); // stupid davin
            
            vmw.close();            

        } catch (IOException e) {
            System.out.println("error compilation engine constructor: " + e);
        }
        /* Note: Section 10.3.3 does not mention the need for a close() method.
			This is either an oversight or it is expected that the class is 
			self-rectifying in this regard. As compileClass() must be called
			next after the constructor, closing outputFile could occur in the 
			final stages of compileClass(). */
    }

    /**
     * compileClass() is the only public method. All other methods are called
     * using recursive descent parsing. *
     */
    public void compileClass() {

        tabs = 0;
        hasMoreBranches.clear();

        print("Compiling class: ");

        // 'class'
        tokenizer.advance();
        
        // className
        tokenizer.advance();
        className = tokenizer.identifier(); // save class name for later

        System.out.println(className);

        // '{'
        tokenizer.advance();

        // classVarDec (static | field)
        tokenizer.advance();

        hasMoreBranches.set(tabs, true);
        
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field"))) {
            tabs++;
            compileClassVarDec();
            tabs--;
        }

        hasMoreBranches.set(tabs, false);

        // subroutineDec (constructor | function | method)
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && (tokenizer.keyWord().equals("constructor") || tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method"))) {
            tabs++;
            compileSubroutine();
            tabs--;
        }
        
        // '}'
        tokenizer.advance();
        
        /* The general procedure for any "compile" method is to handle each terminal
        in order according to the Jack grammar. When a non-terminal is encountered,
        a method is to be called to handle that.
        
        ie: In this method, we should encounter 3 terminals (the keyword "class",
        an identifier and the symbol '{'). We will next see a keyword related
        to classVarDec ("static" or "field") or a keyword related to subroutines
        ("constructor", "function" or "method"). We loop calling compileClassVarDec()
        until we have subroutine keywords and loop calling compileSubroutine().
        We look for the closing '}', close the output file and return.  */
    }


    private void compileClassVarDec() {
        // ('static' | 'field') type varName (',' varName)* ';'

        print("Compiling class variable: ");

        // kind = static or field
        SymbolTable.VarKind kind;
        if (tokenizer.keyWord().equals("static")) {
            kind = SymbolTable.VarKind.STATIC;
        } else {
            kind = SymbolTable.VarKind.FIELD;
        }
        tokenizer.advance();

        // type = int, char, boolean, or className
        String type;
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            type = tokenizer.keyWord();
        } else {
            type = tokenizer.identifier(); // class name
        }
        tokenizer.advance();

        // first varName
        String varName = tokenizer.identifier();
        System.out.println(varName);
        st.Define(varName, type, kind); 
        tokenizer.advance();

        // more commas = more varNames
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
            tokenizer.advance();
            varName = tokenizer.identifier();
            print("Compiling class variable: ");
            System.out.println(varName);
            st.Define(varName, type, kind);
            tokenizer.advance();
        }

        // ';'
        if (tokenizer.symbol() == ';') {
            tokenizer.advance();
        }
    }



    /**
     * Section 10.3.3 suggests making compileSubroutine. Figure 10.5 does not
     * explicitly define subroutine but we can infer it as being a subroutine
     * declaration (subroutineDec) followed by a subroutine body
     * (subroutineBody), both of which are defined. *
     */
    private void compileSubroutine() {

        print("Compiling subroutine: ");

        // subroutine:			subroutineDec subroutineBody
        // 		subroutineDec:	('constructor' | 'function' | 'method')
        //						('void' | type) subroutineName '(' parameterList ')'
        //						subroutineBody
        //		subroutineBody:	'{' varDec* statements '}'

        // subroutineDec:
        st.startSubroutine();

        String subKind = tokenizer.keyWord(); // constructor, function, or method
        System.out.print(subKind + " ");
        tokenizer.advance();

        tokenizer.advance(); // 'void' | 'type'

        String subName = tokenizer.identifier(); // subroutineName
        System.out.println(subName + " ");
        tokenizer.advance();

        tokenizer.advance(); // '('
        tabs++; 
        compileParameterList(subKind.equals("method"));
        tabs--; 
        tokenizer.advance(); // ')'


        // subroutineBody:
        // since we need subKind in the implementation and it's relatively short, it's easier to just get rid of compileSubroutineBody() and throw the logic in here
        tokenizer.advance(); // '{'

        tabs++; 
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("var")) { // varDec* statements
            compileVarDec();
        }
        tabs--; 


        // vm function declaration
        int numLocals = st.VarCount(SymbolTable.VarKind.VAR);
        vmw.writeFunction(className, subName, numLocals);

        // constructor/method setup
        if (subKind.equals("constructor")) {
            int numFields = st.VarCount(SymbolTable.VarKind.FIELD);
            vmw.writePush("constant", numFields);
            vmw.writeCall("Memory.alloc", 1);
            vmw.writePop("pointer", 0); // this = allocated memory

        } else if (subKind.equals("method")) {
            vmw.writePush("argument", 0);
            vmw.writePop("pointer", 0); // this = argument 0
        }

        tabs++; 
        compileStatements();
        tabs--;

        tokenizer.advance(); // '}'
    }



    private void compileParameterList(boolean isMethod) {

        print("Compiling Parameter list: ");

        // if method, add "this" as first arg
        if (isMethod) {
            st.Define("this", className, SymbolTable.VarKind.ARG);
        }

        System.out.print("(");

        // parameterList: ((type varName) (',' type varName)*)?
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD || tokenizer.tokenType() == JackTokenizer.Type.IDENTIFIER) {
            
            while (true) {

                String type; // type


                if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
                    type = tokenizer.keyWord(); // int, boolean, char, void
                } else {
                    type = tokenizer.identifier(); // class type
                }

                System.out.print(type);

                tokenizer.advance();

                // varName
                String name = tokenizer.identifier();
                st.Define(name, type, SymbolTable.VarKind.ARG);
                tokenizer.advance();

                System.out.print(name);

                // if more commas continue, otherwise break
                if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
                    System.out.print(", ");
                    tokenizer.advance();
                } else {
                    break;
                }
            }

        }

        System.out.println(")");

    }


    private void compileVarDec() {

        print("Compiling subroutine var: ");

        tokenizer.advance(); // 'var'

        String type; // type
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            type = tokenizer.keyWord(); // int, char, boolean
        } else {
            type = tokenizer.identifier(); // class type
        }

        System.out.print(type + " ");

        tokenizer.advance();

        // varName
        String name = tokenizer.identifier();
        System.out.print(name);
        st.Define(name, type, SymbolTable.VarKind.VAR); // add local to symbol table
        tokenizer.advance();

        // ", varName"*
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
            tokenizer.advance(); // ','

            name = tokenizer.identifier();
            st.Define(name, type, SymbolTable.VarKind.VAR); // add the extra locals
            System.out.print(", " + name);
            tokenizer.advance();
        }

        System.out.println();

        tokenizer.advance(); // ';'
    }


    private void compileStatements() {

        print("Compiling statement:\n");

        tabs++; 

        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            String keyword = tokenizer.keyWord();
            switch (keyword) {
                case "let" -> compileLet();
                case "if" -> compileIf();
                case "while" -> compileWhile();
                case "do" -> compileDo();
                case "return" -> compileReturn();
                default -> {
                    return;
                }
            }
        }

        tabs--; 

    }

    private void compileDo() {

        print("Compiling do: do \n");
        
        tokenizer.advance(); // 'do'

        String name = tokenizer.identifier(); // subroutineName, className, or varName
        tokenizer.advance();

        int numArgs = 0;
        String subroutineName;

        if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '.') {
            tokenizer.advance(); // '.'

            String name2 = tokenizer.identifier();
            tokenizer.advance();

            // check for name in symbol table
            if (st.KindOf(name) != SymbolTable.VarKind.NONE) {

                String type = st.TypeOf(name);
                String segment = kindToSegment(st.KindOf(name));


                // push object reference as arg0
                vmw.writePush(segment, st.IndexOf(name));
                numArgs = 1;
                subroutineName = type + "." + name2; // method call on object type

            } else {

                // className.subroutineName (static call)
                subroutineName = name + "." + name2;
            }
        } else {
            // subroutine in the current class
            // push "this" as arg0
            vmw.writePush("pointer", 0);
            numArgs++;
            subroutineName = className + "." + name;
        }


        tokenizer.advance(); // '('

        numArgs += compileExpressionList();

        tokenizer.advance(); // ')'

        vmw.writeCall(subroutineName, numArgs);
        
        vmw.writePop("temp", 0); // discard return value

        // ';'
        tokenizer.advance();
    }


    private void compileLet() {

        print("Compiling let: let ");

        // letStatement:  'let' varName ('[' expression ']')? '=' expression ';'
        
        boolean isArray = false;

        tokenizer.advance(); // 'let'

        // varName
        String varName = tokenizer.identifier();
        tokenizer.advance();

        System.out.print(varName + " = \n");

        // [expression]?
        if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '[') {
            
            isArray = true;
    
            tokenizer.advance(); // '['
    
            // push base address of varName
            SymbolTable.VarKind kind = st.KindOf(varName);

            int index = st.IndexOf(varName);
            String segment = kindToSegment(st.KindOf(varName));


            vmw.writePush(segment, index);
    
            // compile index expression
            tabs++; 
            compileExpression();
            tabs--; 
    
            tokenizer.advance(); // ']'
    
            // add base + index
            vmw.writeArithmetic("add");
        }

        // =
        tokenizer.advance();

        // expression
        tabs++; 
        compileExpression();
        tabs--; 

        // ;
        tokenizer.advance();
        
        if (isArray) {
            // For arr[i] = expr
            vmw.writePop("temp", 0);     // save expr value
            vmw.writePop("pointer", 1);  // set THAT = arr+i
            vmw.writePush("temp", 0);    // reload expr value
            vmw.writePop("that", 0);     // store into arr[i]
        } else {

            String segment = kindToSegment(st.KindOf(varName));

            vmw.writePop(segment, st.IndexOf(varName));
        }
    }

    private int whileCounter = 0;

    private void compileWhile() {

        print("Compiling while: while \n");

        // whileStatement:  'while' '('  expression ')' '{' statements '}'
        int thisWhile = whileCounter++;
    
        tokenizer.advance(); // 'while'
    
        tokenizer.advance(); // '('
        vmw.writeLabel("WHILE_EXP" + thisWhile);
    
        // expression
        tabs++; 
        compileExpression();
        tabs--; 
    
        tokenizer.advance(); // ')'
    
        // not condition
        vmw.writeArithmetic("not");
    
        // if-goto end
        vmw.writeIf("WHILE_END" + thisWhile);
    
        tokenizer.advance(); // '{'
    
        // statements
        tabs++; 
        compileStatements();
        tabs--; 
    
        tokenizer.advance(); // '}'
    
        // jump back to start
        vmw.writeGoto("WHILE_EXP" + thisWhile);
    
        // end label
        vmw.writeLabel("WHILE_END" + thisWhile);

    }


    private void compileReturn() {

        print("Compiling return: return \n");

            
        // returnStatement:	'return' expression? ';'

        tokenizer.advance(); // 'return'

        // if next token isn't ';' there's an expression
        if (!(tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ';')) {
            tabs++; 
            compileExpression();
            tabs--; 
        } else {
            // void return = 0
            vmw.writePush("constant", 0);
        }

        tokenizer.advance(); // ';'

        vmw.writeReturn();
    }

    private int ifCounter = 0;

    private void compileIf() {

        print("Compiling if: if\n");


        // ifStatement:  'if' '(' expression ')' '{' statements '}'
        //				('else' '{' statements '}')?

        int thisIf = ifCounter++;
    
        tokenizer.advance(); // 'if'

        tokenizer.advance(); // '('
        
        tabs++; 
        compileExpression(); // expression
        tabs--; 
    
        tokenizer.advance(); // ')'
    
        // condition is on stack, not it
        vmw.writeArithmetic("not");
        vmw.writeIf("IF_FALSE" + thisIf);
    
        tokenizer.advance(); // '{'
        
        tabs++; 
        compileStatements();
        tabs--; 

        tokenizer.advance(); // '}'
    
        vmw.writeGoto("IF_END" + thisIf);
        vmw.writeLabel("IF_FALSE" + thisIf);
    
        // else?
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("else")) {
            tokenizer.advance(); // 'else'
            tokenizer.advance(); // '{'
            tabs++; 
            compileStatements();
            tabs--; 
            tokenizer.advance(); // '}'
        }
    
        vmw.writeLabel("IF_END" + thisIf);
    }
    


    private void compileExpression() {

        print("Compiling expression: \n");


        // expression:	term (op term)*

        tabs++; 
        compileTerm(); // term
        tabs--; 
    
        // while the current token is an operator
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && isValidSymbol(tokenizer.symbol())) {

            char operator = tokenizer.symbol();
            tokenizer.advance(); // operator
            
            tabs++; 
            compileTerm();
            tabs--; 
    
            switch (operator) {
                case '+' -> vmw.writeArithmetic("add");
                case '-' -> vmw.writeArithmetic("sub");
                case '*' -> vmw.writeCall("Math.multiply", 2);
                case '/' -> vmw.writeCall("Math.divide", 2);
                case '&' -> vmw.writeArithmetic("and");
                case '|' -> vmw.writeArithmetic("or");
                case '<' -> vmw.writeArithmetic("lt");
                case '>' -> vmw.writeArithmetic("gt");
                case '=' -> vmw.writeArithmetic("eq");
            }
        }
    }

    public String comparisonSwitch(char token) {
        return switch (token) {
            case '&' -> "&amp;";
            case '<' -> "&lt;";
            case '>' -> "&gt;";
            default -> "" + token;
        };
    }

    private boolean isValidSymbol(char c) {
        return "+-*/&|<>=".indexOf(c) != -1;
    }

    /**
     * Near the end of section 10.1.3, it is mentioned that the Jack grammer is
     * "almost" LL(0). The exception being that lookahead is required for the
     * parsing of expressions. Specifically, a subroutineCall starts with an
     * identifier which makes it impossible to differentiate from varName
     * without more context either in terms of a pre-populated symbol table or a
     * lookahead. Subroutine call identifiers are always followed by an '('.
     * Looking ahead one token resolves the problem. *
     */
    public void compileTerm() { // public ???
        // term:	integerConstant | stringConstant | keywordConstant |
        //			varName | varName '[' expression ']' | subroutineCall |
        //			'(' expression ')' | unaryOp term

        print("Compiling term: ");

        if (tokenizer.tokenType() == JackTokenizer.Type.INT_CONSTANT) { // integerConstant | DONE
            vmw.writePush("constant", tokenizer.intVal());
            System.out.println(tokenizer.intVal());
            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.STRING_CONSTANT) { // stringConstant | DONE

            vmw.writePush("constant", tokenizer.stringVal().length());
            vmw.writeCall("String.new", 1);

            System.out.println("\"" + tokenizer.stringVal() + "\"");

            for(char c : tokenizer.stringVal().toCharArray()){
                vmw.writePush("constant", (int) c);
                vmw.writeCall("String.appendChar", 2);
            }

            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) { // keywordConstant | DONE

            switch(tokenizer.keyWord()){
                case "true" -> vmw.writePush("constant", -1);
                case "false", "null" -> vmw.writePush("constant", 0);
                case "this" -> vmw.writePush("pointer", 0);
            }

            System.out.println(tokenizer.keyWord());
            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '-') { // unaryOp negative | DONE
            System.out.println();
            tokenizer.advance(); // -
            tabs++; 
            compileTerm();
            tabs--; 
            vmw.writeArithmetic("neg");


        } else if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '~') { // unaryOp not | DONE
            System.out.println();
            tokenizer.advance(); // ~
            tabs++; 
            compileTerm();
            tabs--; 
            vmw.writeArithmetic("not");

        } 
        
        else if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '(') { // (expression) | DONE
            System.out.println();

            tokenizer.advance(); // (
            tabs++; 
            compileExpression(); // expression
            tabs--; 
            tokenizer.advance(); // )

        } else if (tokenizer.tokenType() == JackTokenizer.Type.IDENTIFIER) { // varName
            
            String varName = tokenizer.identifier();

            System.out.print(varName);
            
            tokenizer.advance();
            
            if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL) {
                switch (tokenizer.symbol()) {
                    case '[' -> { // varName[x]
                        System.out.println("[]");


                        SymbolTable.VarKind kind = st.KindOf(varName);
                        int index = st.IndexOf(varName);
                        String segment = kindToSegment(kind);

                        tokenizer.advance(); // '['
                        tabs++; 
                        compileExpression(); // expression
                        tabs--; 
                        tokenizer.advance(); // ']'
                        
                        vmw.writePush(segment, index);
                        vmw.writeArithmetic("add");
                        vmw.writePop("pointer", 1);
                        vmw.writePush("that", 0);
                    }

                    case '(' -> { // varName() 
                        System.out.println("()");

                        tokenizer.advance(); // '('
                        vmw.writePush("pointer", 0);
                        tabs++; 
                        int numArgs = 1 + compileExpressionList();
                        tabs--; 
                        tokenizer.advance(); // ')'
                        vmw.writeCall(className + "." + varName, numArgs);
                    }

                    case '.' -> {

                        tokenizer.advance(); // '.'
                        String methodName = tokenizer.identifier();
                        tokenizer.advance(); // methodName

                        System.out.println("." + methodName + "()");
                        tokenizer.advance(); // '('
                    
                        // varName = variable | class
                        SymbolTable.VarKind kind = st.KindOf(varName);
                        int index = st.IndexOf(varName);
                        int numArgs = 0;
                    
                        if (kind != SymbolTable.VarKind.NONE) {
                            // instance method call
                            String segment = kindToSegment(kind);
                            vmw.writePush(segment, index);
                            numArgs = 1;
                            varName = st.TypeOf(varName);
                        }
                        
                        tabs++; 
                        numArgs += compileExpressionList();
                        tabs--; 
                    
                        tokenizer.advance(); // ')'

                        vmw.writeCall(varName + "." + methodName, numArgs);
                    }
                    
                    
                    default -> { // just varName
                        System.out.println();


                        SymbolTable.VarKind kind = st.KindOf(varName);
                        int index = st.IndexOf(varName);
                        String segment = kindToSegment(kind);

                        vmw.writePush(segment, index);
                    }
                }
            } else {
                System.out.println();

                SymbolTable.VarKind kind = st.KindOf(varName);
                int index = st.IndexOf(varName);
                String segment = kindToSegment(kind);
                vmw.writePush(segment, index);
            }
        }

    }

    private int compileExpressionList() {

        print("Compiling expression list: \n");

        int count = 0;

        // check for empty expression list
        if (!(tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ')')) {
            tabs++; 
            compileExpression();
            tabs--; 
            count++;

            while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
                tokenizer.advance();
                tabs++; 
                compileExpression();
                tabs--; 
                count++;
            }
        }

        return count;
    }

    private String kindToSegment(SymbolTable.VarKind kind) {
        return switch (kind) {
            case VAR -> "local";
            case ARG -> "argument";
            case FIELD -> "this";
            case STATIC -> "static";
            default -> throw new RuntimeException("Invalid var kind: " + kind);
        };
    }

}
