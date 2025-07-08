import java.io.*;

/** CompilationEngine will serve as the focus for most of the compilers functionality.
	Based on the grammar delineated in section 10.3.3, each method will output XML
	and make appropriate calls to other methods. This recursive process means that 
	the caller only needs to call compileClass for each file and all other compilations 
	will occur as needed. 
	
	Note: In chapter 10, the program outputs XML. This includes markup for symbols,
			keywords, and identifiers. Making compile methods for these token types
			would be prudent.
			
		  The book's camel-case suggestions are inconsistent in section 10.3.3 with
			about half capitalizing the first letter and half leaving it lower-case.
			We will use strict camel-case.  **/

class CompilationEngine {

	private JackTokenizer tokenizer;
	private BufferedWriter writer;

	

	/** The CompilationEngine will create a new JackTokenizer object based on the
		inputFile and will create an output stream to outputFile. **/
	public CompilationEngine (String inputFile, String outputFile){
	    try {
			tokenizer = new JackTokenizer(new FileReader(inputFile));
			writer = new BufferedWriter(new FileWriter(outputFile));
			compileClass();
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

	private void w(String s){
		try{
			writer.write(s + "\n");
		} catch (Exception e){
			System.out.println("w() error: " + e);
		}
	}

	/** compileClass() is the only public method. All other methods are called
		using recursive descent parsing. **/
	public void compileClass() {
		// class:	'class' className '{' classVarDec* subroutine* '}'

		w("<class>");

	    // 'class'
		tokenizer.advance();
		w("<keyword> class </keyword>");;

		// className
		tokenizer.advance();
		w("<identifier> " + tokenizer.identifier() + " </identifier>");
			

		// '{'
		tokenizer.advance();
		w("<symbol> { </symbol>");

		tokenizer.advance();
		while (true) {

			String keyword = tokenizer.keyWord();

			if (keyword.equals("static") || keyword.equals("field")) {
				compileClassVarDec();
			} else if (keyword.equals("constructor") || keyword.equals("function") || keyword.equals("method")) {
				compileSubroutine();
			} else if (keyword.equals("}")) {
				w("<symbol> } </symbol>");
				break;
			} else {
				System.out.print("compile class loop error");
				break;
			}
		}

		w("</class>");

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
		// classVarDec:	('static' | 'field') type varName (',' vaName)* ';'
		w("<classVarDec>");

		w("<keyword> " + tokenizer.keyWord() + " </keyword>"); // either static or field
    	tokenizer.advance();

		if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) { // could be a primitive data type or an object
			w("<keyword> " + tokenizer.keyWord() + " </keyword>");
		} else {
			w("<identifier> " + tokenizer.identifier() + " </identifier>");
		}
		tokenizer.advance();

		w("<identifier> " + tokenizer.identifier() + " </identifier>"); // regardless of previous decision, the variable's name comes next
    	tokenizer.advance();

		while (tokenizer.symbol() == ',') { // handle multiple variables
			w("<symbol> , </symbol>");
			tokenizer.advance();

			w("<identifier> " + tokenizer.identifier() + " </identifier>");
			tokenizer.advance();
    	}

		w("<symbol> ; </symbol>");

    	tokenizer.advance();

    	w("</classVarDec>");

	}
	
	/** Section 10.3.3 suggests making compileSubroutine. Figure 10.5 does not explicitly
		define subroutine but we can infer it as being a subroutine declaration 
		(subroutineDec) followed by a subroutine body (subroutineBody), both of which
		are defined. **/
	private void compileSubroutine() {
		// subroutine:			subroutineDec subroutineBody
		// 		subroutineDec:	('constructor' | 'function' | 'method')
		//						('void' | type) subroutineName '(' parameterList ')'
		//						subroutineBody
		//		subroutineBody:	'{' varDec* statements '}'

		w("<subroutineDec>");

    	w("<keyword> " + tokenizer.keyWord() + " </keyword>"); // constructor, function, or method
    	tokenizer.advance();

		if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) { // keyword or
       		w("<keyword> " + tokenizer.keyWord() + " </keyword>");
		} else {
			w("<identifier> " + tokenizer.identifier() + " </identifier>");
		}
		tokenizer.advance();

		w("<identifier> " + tokenizer.identifier() + " </identifier>"); // subroutineName
    	tokenizer.advance();

		w("<symbol> ( </symbol>"); // '('
		tokenizer.advance();

		compileParameterList(); // parameters

		w("<symbol> ) </symbol>"); // ')'
		tokenizer.advance();

		compileSubroutineBody(); // body

		w("</subroutineDec>");
	}

	private void compileSubroutineBody() {
		w("<subroutineBody>");

		w("<symbol> { </symbol>");
		tokenizer.advance();

		while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD && tokenizer.keyWord().equals("var")) {
			compileVarDec();
		}

		compileStatements();

		w("<symbol> } </symbol>");
		tokenizer.advance();

		w("</subroutineBody>");
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

			// first varName
			w("<identifier> " + tokenizer.identifier() + " </identifier>");
			tokenizer.advance();

			// additional ", type name"'s
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
	
	private void compileVarDec() {
		// varDec:	'var' type varName (',' type varName)*)?

		w("<keyword> var </keyword>");
    	tokenizer.advance();


		if (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) { // could be a primitive data type or an object
			w("<keyword> " + tokenizer.keyWord() + " </keyword>");
		} else {
			w("<identifier> " + tokenizer.identifier() + " </identifier>");
		}

		tokenizer.advance();

		w("<identifier> " + tokenizer.identifier() + " </identifier>"); // regardless of previous decision, the variable's name comes next
    	tokenizer.advance();

		while (tokenizer.symbol() == ',') { // handle multiple variables
			w("<symbol> , </symbol>");
			tokenizer.advance();

			w("<identifier> " + tokenizer.identifier() + " </identifier>");
			tokenizer.advance();
    	}

		w("<symbol> ; </symbol>");
		tokenizer.advance();

	}
	
	private void compileStatements() {
		// statements:	statement*
		w("<statements>");

		while (tokenizer.tokenType() == JackTokenizer.Type.KEYWORD) {
			String keyword = tokenizer.keyWord();
			switch (keyword) {
				case "let":
					compileLet();
					break;
				case "if":
					compileIf();
					break;
				case "while":
					compileWhile();
					break;
				case "do":
					compileDo();
					break;
				case "return":
					compileReturn();
					break;
				default:
					return;
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
		if(tokenizer.tokenType() != JackTokenizer.Type.SYMBOL){ // if not ;
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
	
	// ! chapter 11 stuff?!


	private void compileExpression() {
		// expression:	term (op term)*
		
	}
	
	/** Near the end of section 10.1.3, it is mentioned that the Jack grammer is
		"almost" LL(0). The exception being that lookahead is required for the
		parsing of expressions. Specifically, a subroutineCall starts with an identifier
		which makes it impossible to differentiate from varName without
	 	more context either in terms of a pre-populated symbol table or a lookahead.
	 	Subroutine call identifiers are always followed by an '('. Looking ahead
	 	one token resolves the problem. **/
	private void compileTerm() {
		// term:	integerConstant | stringConstant | keywordConstant |
		//			varName | varName '[' expression ']' | subroutineCall |
		//			'(' expression ')' | unaryOp term
		
	}
	
	private void compileExpressionList() {
		// expressionList:	( expression (',' expression)* )?
		
	}
}