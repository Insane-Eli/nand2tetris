
import java.io.*;


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

    private JackTokenizer tokenizer;
    private BufferedWriter writer;
    private SymbolTable st;
    private VMWriter vmw;
    private String className;

    public void debug(String keyword) {
        System.out.println("\tkeyword: " + keyword);
    }

    /**
     * @param input Jack file The CompilationEngine will create a new
     * JackTokenizer object based on the inputFile and will create an output
     * stream to outputFile. *
     */
    public CompilationEngine(String inputFile, String outputFile) {
        try {
            tokenizer = new JackTokenizer(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));
            compileClass();
            writer.write("\n// ELI'S TRANSLATED FILE BTW");
            st = new SymbolTable();
            vmw = new VMWriter(outputFile);
            writer.close(); // TEMPORARY
        } catch (IOException e) {
            System.out.println("error compilation engine constructor: " + e);
        }
        /* Note: Section 10.3.3 does not mention the need for a close() method.
			This is either an oversight or it is expected that the class is 
			self-rectifying in this regard. As compileClass() must be called
			next after the constructor, closing outputFile could occur in the 
			final stages of compileClass(). */
    }

    private void w(String s) {
        try {
            writer.write(s);

            writer.write("\n");
        } catch (IOException e) {
            System.out.println("w() error: " + e);
        }
    }

    /**
     * compileClass() is the only public method. All other methods are called
     * using recursive descent parsing. *
     */
    public void compileClass() {
        // 'class'
        tokenizer.advance();
        
        // className
        tokenizer.advance();
        className = tokenizer.identifier(); // save class name for later

        // '{'
        tokenizer.advance();

        // classVarDec (static | field)
        tokenizer.advance();
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field"))) {
            compileClassVarDec();
            tokenizer.advance();
        }

        // subroutineDec (constructor | function | method)
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && (tokenizer.keyWord().equals("constructor") || tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method"))) {
            compileSubroutine();
            tokenizer.advance();
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
        st.Define(varName, type, kind); 
        tokenizer.advance();

        // more commas = more varNames
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
            tokenizer.advance();
            varName = tokenizer.identifier();
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
        // subroutine:			subroutineDec subroutineBody
        // 		subroutineDec:	('constructor' | 'function' | 'method')
        //						('void' | type) subroutineName '(' parameterList ')'
        //						subroutineBody
        //		subroutineBody:	'{' varDec* statements '}'

        // subroutineDec:
        st.startSubroutine();

        String subKind = tokenizer.keyWord(); // constructor, function, or method
        tokenizer.advance();

        tokenizer.advance(); // 'void' | 'type'

        String subName = tokenizer.keyWord(); // subroutineName
        tokenizer.advance();

        tokenizer.advance(); // '('
        compileParameterList(subKind.equals("method"));
        tokenizer.advance(); // ')'


        // subroutineBody:
        // since we need subKind in the implementation and it's relatively short, it's easier to just get rid of compileSubroutineBody() and throw the logic in here
        tokenizer.advance(); // '{'

        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("var")) { // varDec* statements
            compileVarDec();
        }


        // vm function declaration
        int numLocals = st.VarCount(SymbolTable.VarKind.VAR);
        vmw.writeFunction(className + "." + subName, numLocals);

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

        compileStatements();

        tokenizer.advance(); // '}'
    }



    private void compileParameterList(boolean isMethod) {

        // if method, add "this" as first arg
        if (isMethod) {
            st.Define("this", className, SymbolTable.VarKind.ARG);
        }

        // parameterList: ((type varName) (',' type varName)*)?
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD || tokenizer.tokenType() == JackTokenizer.Type.IDENTIFIER) {

            while (true) {

                String type; // type

                if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
                    type = tokenizer.keyWord(); // int, boolean, char, void
                } else {
                    type = tokenizer.identifier(); // class type
                }

                tokenizer.advance();

                // varName
                String name = tokenizer.identifier();
                st.Define(name, type, SymbolTable.VarKind.ARG);
                tokenizer.advance();

                // if more commas continue, otherwise break
                if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
                    tokenizer.advance();
                } else {
                    break;
                }
            }
        }
    }


    private void compileVarDec() {

        tokenizer.advance(); // 'var'

        String type; // type
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            type = tokenizer.keyWord(); // int, char, boolean
        } else {
            type = tokenizer.identifier(); // class type
        }
        tokenizer.advance();

        // varName
        String name = tokenizer.identifier();
        st.Define(name, type, SymbolTable.VarKind.VAR); // add local to symbol table
        tokenizer.advance();

        // ", varName"*
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL &&
            tokenizer.symbol() == ',') {
            tokenizer.advance(); // ','

            name = tokenizer.identifier();
            st.Define(name, type, SymbolTable.VarKind.VAR); // add the extra locals
            tokenizer.advance();
        }

        tokenizer.advance(); // ';'
    }


    private void compileStatements() {

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
    }

    private void compileDo() {
        
        tokenizer.advance(); // 'do'

        String name = tokenizer.identifier(); // subroutineName, className, or varName
        tokenizer.advance();

        int numArgs = 0;
        String subroutineName;

        if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '.') {
            tokenizer.advance(); // '.'

            String nextName = tokenizer.identifier();
            tokenizer.advance();

            // check for "name" in symbol table (i.e. method call on an object)
            if (st.KindOf(name) != SymbolTable.VarKind.NONE) {
                String type = st.TypeOf(name);
                int index = st.IndexOf(name);
                String segment;

                segment = switch(st.KindOf(name)) {
                    case VAR -> "local";
                    case ARG -> "argument";
                    case FIELD -> "this";
                    case STATIC -> "static";
                    default -> "bruh!";
                };

                // push object reference as arg0
                vmw.writePush(segment, index);
                numArgs++;
                subroutineName = type + "." + nextName; // method call on object type

            } else {

                // className.subroutineName (static call)
                subroutineName = name + "." + nextName;
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
        // letStatement:  'let' varName ('[' expression ']')? '=' expression ';'

        tokenizer.advance(); // let

        // varName
        String varName = tokenizer.identifier();
        SymbolTable.VarKind kind = st.KindOf(varName);
        String type = st.TypeOf(varName);
        int index = st.IndexOf(varName);


        // [expression]?
        if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == '[') {
            w("<symbol> [ </symbol>");
            tokenizer.advance();

            compileExpression();

            w("<symbol> ] </symbol>");
            tokenizer.advance();
        }

        // =
        w("<symbol> = </symbol>");
        tokenizer.advance();

        // expression
        compileExpression();

        // ;
        w("<symbol> ; </symbol>");
        tokenizer.advance();

        w("</letStatement>");
    }

    private void compileWhile() {
        // whileStatement:  'while' '('  expression ')' '{' statements '}'

        w("<whileStatement>");

        // while
        w("<keyword> while </keyword>");
        tokenizer.advance();

        // (
        w("<symbol> ( </symbol>");
        tokenizer.advance();

        // expression
        compileExpression();

        // )
        w("<symbol> ) </symbol>");
        tokenizer.advance();

        // {
        w("<symbol> { </symbol>");
        tokenizer.advance();

        // statements
        compileStatements();

        // }
        w("<symbol> } </symbol>");
        tokenizer.advance();

        w("</whileStatement>");
    }

    private void compileReturn() {
        // returnStatement:	'return' expression? ';'
        w("<returnStatement>");

        // return
        w("<keyword> return </keyword>");
        tokenizer.advance();

        // expression?
        if (tokenizer.tokenType() != JackTokenizer.Type.SYMBOL) { // if not ;
            compileExpression();
        }

        // ;
        w("<symbol> ; </symbol>");
        tokenizer.advance();

        w("</returnStatement>");

    }

    private void compileIf() {
        // ifStatement:  'if' '(' expression ')' '{' statements '}'
        //				('else' '{' statements '}')?

        w("<ifStatement>");

        // if
        w("<keyword> if </keyword>");
        tokenizer.advance();

        // (
        w("<symbol> ( </symbol>");
        tokenizer.advance();

        // expression
        compileExpression();

        // )
        w("<symbol> ) </symbol>");
        tokenizer.advance();

        // {
        w("<symbol> { </symbol>");
        tokenizer.advance();

        // statements
        compileStatements();

        // }
        w("<symbol> } </symbol>");
        tokenizer.advance();

        // else?
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("else")) {
            w("<keyword> else </keyword>");
            tokenizer.advance();

            w("<symbol> { </symbol>");
            tokenizer.advance();

            compileStatements();

            w("<symbol> } </symbol>");
            tokenizer.advance();
        }

        w("</ifStatement>");
    }

    private void compileExpression() {
        // expression:	term (op term)*
        w("<expression>");
        compileTerm();

        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && isValidSymbol(tokenizer.symbol())) {
            w("<symbol> " + comparisonSwitch(tokenizer.symbol()) + " </symbol>");
            tokenizer.advance();
            compileTerm();
        }

        w("</expression>");

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
    private void compileTerm() {
        // term:	integerConstant | stringConstant | keywordConstant |
        //			varName | varName '[' expression ']' | subroutineCall |
        //			'(' expression ')' | unaryOp term

        w("<term>");

        if (tokenizer.tokenType() == JackTokenizer.Type.INT_CONSTANT) {
            w("<integerConstant> " + tokenizer.intVal() + " </integerConstant>");
            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.STRING_CONSTANT) {
            w("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>");
            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            w("<keyword> " + tokenizer.keyWord() + " </keyword>");
            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL
                && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
            w("<symbol> " + tokenizer.symbol() + " </symbol>");
            tokenizer.advance();
            compileTerm();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL
                && tokenizer.symbol() == '(') {
            w("<symbol> ( </symbol>");
            tokenizer.advance();
            compileExpression();
            w("<symbol> ) </symbol>");
            tokenizer.advance();

        } else if (tokenizer.tokenType() == JackTokenizer.Type.IDENTIFIER) {
            w("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();

            if (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL) {
                switch (tokenizer.symbol()) {
                    case '[' -> {
                        w("<symbol> [ </symbol>");
                        tokenizer.advance();
                        compileExpression();
                        w("<symbol> ] </symbol>");
                        tokenizer.advance();
                    }
                    case '(' -> {
                        w("<symbol> ( </symbol>");
                        tokenizer.advance();
                        compileExpressionList();
                        w("<symbol> ) </symbol>");
                        tokenizer.advance();
                    }
                    case '.' -> {
                        w("<symbol> . </symbol>");
                        tokenizer.advance();
                        w("<identifier> " + tokenizer.identifier() + " </identifier>");
                        tokenizer.advance();
                        w("<symbol> ( </symbol>");
                        tokenizer.advance();
                        compileExpressionList();
                        w("<symbol> ) </symbol>");
                        tokenizer.advance();
                    }
                    default -> {
                    }
                }
            }
        }

        w("</term>");
    }

    private int compileExpressionList() {
        int count = 0;

        // check for empty expression list
        if (!(tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ')')) {
            compileExpression();
            count++;

            while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
                tokenizer.advance();
                compileExpression();
                count++;
            }
        }

        return count;
    }
}
