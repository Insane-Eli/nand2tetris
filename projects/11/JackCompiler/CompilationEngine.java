
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
            st = new SymbolTable();
            vmw = new VMWriter(outputFile);
            writer.write("\n// ELI'S TRANSLATED FILE BTW");
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
        // class:	'class' className '{' classVarDec* subroutine* '}'

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
<<<<<<< Updated upstream

        SymbolTable.VarKind kind = st.strToVarKind(tokenizer.keyWord()); // "static" or "field"
        tokenizer.advance();
    
        // type (keyword or class name)
=======
        // ('static' | 'field') type varName (',' varName)* ';'

        // ('static' | 'field')
        SymbolTable.VarKind kind;
        if (tokenizer.keyWord().equals("static")) {
            kind = SymbolTable.VarKind.STATIC;
        } else {
            kind = SymbolTable.VarKind.FIELD;
        }
        tokenizer.advance();

        // type (int, char, boolean, or className)
>>>>>>> Stashed changes
        String type;
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            type = tokenizer.keyWord();
        } else {
<<<<<<< Updated upstream
            type = tokenizer.identifier();
        }
        tokenizer.advance();
    
        // first var name
        String name = tokenizer.identifier();
        st.Define(name, type, kind);
        tokenizer.advance();
    
        // handle multiple variables
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
            tokenizer.advance(); // skip comma
            name = tokenizer.identifier();
            st.Define(name, type, kind);
            tokenizer.advance();
        }
    
        // skip semicolon
=======
            type = tokenizer.identifier(); // class name
        }
        tokenizer.advance();

        // varName
        String varName = tokenizer.identifier();
        st.Define(varName, type, kind);
        tokenizer.advance();

        // (',' varName)*
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
            tokenizer.advance();
            varName = tokenizer.identifier();
            st.Define(varName, type, kind);
            tokenizer.advance();
        }

        // ';'
>>>>>>> Stashed changes
        if (tokenizer.symbol() == ';') {
            tokenizer.advance();
        }
    }
<<<<<<< Updated upstream
    
=======


>>>>>>> Stashed changes

    /**
     * Section 10.3.3 suggests making compileSubroutine. Figure 10.5 does not
     * explicitly define subroutine but we can infer it as being a subroutine
     * declaration (subroutineDec) followed by a subroutine body
     * (subroutineBody), both of which are defined. *
     */
    private void compileSubroutine() {
<<<<<<< Updated upstream
        // No advance here! Read current token
        String subroutineType = tokenizer.keyWord(); // constructor, function, or method
        tokenizer.advance();
    
        String returnType;
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            returnType = tokenizer.keyWord();
            tokenizer.advance();
        } else {
            returnType = tokenizer.identifier();
            tokenizer.advance();
=======

        // subroutine:			subroutineDec subroutineBody
        // 		subroutineDec:	('constructor' | 'function' | 'method')
        //						('void' | type) subroutineName '(' parameterList ')'
        //						subroutineBody
        //		subroutineBody:	'{' varDec* statements '}'


        st.startSubroutine();  // reset symbol table

        // ('constructor' | 'function' | 'method')
        String subroutineType = tokenizer.keyWord();
        tokenizer.advance();

        // ('void' | type)
        String returnType;
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            returnType = tokenizer.keyWord();
        } else {
            returnType = tokenizer.identifier();  // class name
>>>>>>> Stashed changes
        }
    
        String subroutineName = tokenizer.identifier();
        tokenizer.advance();
<<<<<<< Updated upstream
    
        symbolTable.startSubroutine();
        if (subroutineType.equals("method")) {
            symbolTable.define("this", className, "argument");
        }
    
        tokenizer.advance(); // skip '('
        compileParameterList();
        tokenizer.advance(); // skip ')'
    
        compileSubroutineBody(subroutineName, subroutineType);
    }
    
    private void compileSubroutineBody(String subroutineName, String subroutineType) {
        tokenizer.advance(); // skip '{'
    
        int nLocals = 0;
=======

        // subroutineName
        String subroutineName = tokenizer.identifier();
        tokenizer.advance();

        // '(' parameterList ')'
        tokenizer.advance(); // '('
        compileParameterList();
        tokenizer.advance(); // ')'

        // subroutineBody
        tokenizer.advance(); // '{'

        // count local variables first
        int nLocals = 0;
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("var")) {
            nLocals += compileVarDec();  // define entries in st and return count
        }

        // function declaration
        vmw.writeFunction(className + "." + subroutineName, nLocals);

        // constructor/method setup
        if (subroutineType.equals("constructor")) {
            int nFields = st.VarCount(SymbolTable.VarKind.FIELD); // total fields in class
            vmw.writePush("constant", nFields);
            vmw.writeCall("Memory.alloc", 1);
            vmw.writePop("pointer", 0); // THIS = allocated object
        } else if (subroutineType.equals("method")) {
            vmw.writePush("argument", 0);
            vmw.writePop("pointer", 0); // THIS = argument 0
        }

        compileStatements(); // (let, if, while, do, return)

        tokenizer.advance(); // '}'
    }


    private void compileSubroutineBody() {
        w("<subroutineBody>");

        w("<symbol> { </symbol>");
        tokenizer.advance();

>>>>>>> Stashed changes
        while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("var")) {
            nLocals += compileVarDec(); // make compileVarDec return count of vars declared
        }
    
        vmw.writeFunction(className + "." + subroutineName, nLocals);
    
        if (subroutineType.equals("constructor")) {
            int fieldCount = symbolTable.varCount("field");
            vmw.writePush("constant", fieldCount);
            vmw.writeCall("Memory.alloc", 1);
            vmw.writePop("pointer", 0);
        } else if (subroutineType.equals("method")) {
            vmw.writePush("argument", 0);
            vmw.writePop("pointer", 0);
        }
    
        compileStatements();
    
        tokenizer.advance(); // skip '}'
    }
    

    private void compileParameterList() {
        // parameterList:	((type varName) (',' type varName)*)?
        w("<parameterList>");

        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD || tokenizer.tokenType() == JackTokenizer.Type.IDENTIFIER) {

            // first type
            if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
                w("<keyword> " + tokenizer.keyWord() + " </keyword>");
            } else {
                w("<identifier> " + tokenizer.identifier() + " </identifier>");
            }
            tokenizer.advance();

            // first variable
            w("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();

            // additional variables ", (type) (name)"s
            while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
                w("<symbol> , </symbol>");
                tokenizer.advance();

                if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
                    w("<keyword> " + tokenizer.keyWord() + " </keyword>");
                } else {
                    w("<identifier> " + tokenizer.identifier() + " </identifier>");
                }
                tokenizer.advance();

                w("<identifier> " + tokenizer.identifier() + " </identifier>");
                tokenizer.advance();
            }
        }

        w("</parameterList>");
    }

        private int compileVarDec() {
        // Grammar: 'var' type varName (',' varName)* ';'
        int count = 0;

        tokenizer.advance(); // skip 'var' keyword

        // get type
        String type;
        if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
            type = tokenizer.keyWord(); // int, char, boolean
        } else {
            type = tokenizer.identifier(); // class name
        }
        tokenizer.advance();

        // first varName
        String varName = tokenizer.identifier();
        st.Define(varName, type, SymbolTable.VarKind.VAR);  // define as local variable
        count++;
        tokenizer.advance();

        // handle additional vars separated by commas
        while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
            tokenizer.advance(); // skip ','
            varName = tokenizer.identifier();
            st.Define(varName, type, SymbolTable.VarKind.VAR);
            count++;
            tokenizer.advance();
        }

        // skip terminating ';'
        if (tokenizer.symbol() == ';') {
            tokenizer.advance();
        }

        return count; // number of local variables added
    }


    private void compileStatements() {
        // statements:	statement*
        w("<statements>");

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

        w("</statements>");

    }

    private void compileDo() {
        // doStatement:	'do' subroutineCall ';'
        w("<doStatement>");

        // do
        w("<keyword> do </keyword>");
        tokenizer.advance();

        // write identifier (subroutineName, className or varName)
        w("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();

        if (tokenizer.symbol() == '.') {
            w("<symbol> . </symbol>");
            tokenizer.advance();

            // subroutineName
            w("<identifier> " + tokenizer.identifier() + " </identifier>");
            tokenizer.advance();
        }

        w("<symbol> ( </symbol>");
        tokenizer.advance();

        compileExpressionList();

        w("<symbol> ) </symbol>");
        tokenizer.advance();

        // ;
        w("<symbol> ; </symbol>");
        tokenizer.advance();

        w("</doStatement>");
    }

    private void compileLet() {
        // letStatement:  'let' varName ('[' expression ']')? '=' expression ';'

        w("<letStatement>");

        // let
        w("<keyword> let </keyword>");
        tokenizer.advance();

        // varName
        w("<identifier> " + tokenizer.identifier() + " </identifier>");
        tokenizer.advance();

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

    private void compileExpressionList() {
        // expressionList:	( expression (',' expression)* )?
        w("<expressionList>");

        if (!(tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ')')) {
            compileExpression();

            while (tokenizer.tokenType() == JackTokenizer.Type.SYMBOL && tokenizer.symbol() == ',') {
                w("<symbol> , </symbol>");
                tokenizer.advance();
                compileExpression();
            }
        }

        w("</expressionList>");
    }
}
