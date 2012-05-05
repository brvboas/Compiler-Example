package Lexer;

import java.util.*;
import AST.*;
import AuxComp.*;

public class Lexer {
    
    public Lexer( char []input, CompilerError error ) {
        this.input = input;
          // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
          // number of the current line
        lineNumber = 1;
        tokenPos = 0;
        lastTokenPos = 0;
        beforeLastTokenPos = 0;
        this.error = error;
    }


    public void skipBraces() {
          // skip any of the symbols [ ] { } ( )
        if ( token == Symbol.CURLYLEFTBRACE || token == Symbol.CURLYRIGHTBRACE ||
             token == Symbol.LEFTSQBRACKET  || token == Symbol.RIGHTSQBRACKET )
             nextToken();
        if ( token == Symbol.EOF )
          error.signal("Inesperado EOF");
    }

    public void skipPunctuation() {
          // skip any punctuation symbols
        while ( token != Symbol.EOF &&
                ( token == Symbol.COMMA ||
                  token == Symbol.SEMICOLON) )
           nextToken();
        if ( token == Symbol.EOF )
          error.signal("Inesperado EOF");
    }

    public void skipTo( Symbol []arraySymbol )  {
        // skip till one of the characters of arraySymbol appears in the input
        while ( token != Symbol.EOF ) {
            int i = 0;
            while ( i < arraySymbol.length )
                if ( token == arraySymbol[i] )
                  return;
                else
                  i++;
            nextToken();
        }
        if ( token == Symbol.EOF )
          error.signal("Inesperado EOF");
    }

    public void skipToNextStatement() {

        while ( token != Symbol.EOF &&
                token != Symbol.ELSE  && token != Symbol.ENDIF &&
                token != Symbol.END && token != Symbol.SEMICOLON )
           nextToken();
        if ( token == Symbol.SEMICOLON )
          nextToken();
    }


      // contains the keywords
    static private Hashtable<String, Symbol> keywordsTable;
    
     // this code will be executed only once for each program execution
     static {
        keywordsTable = new Hashtable<String, Symbol>();
        keywordsTable.put( "inicio", Symbol.BEGIN );
        keywordsTable.put( "fim", Symbol.END );
        keywordsTable.put( "se", Symbol.IF );
        keywordsTable.put( "entao", Symbol.THEN );
        keywordsTable.put( "senao", Symbol.ELSE );
        keywordsTable.put( "fimse", Symbol.ENDIF );
        keywordsTable.put( "leia", Symbol.READ );
        keywordsTable.put( "escreva", Symbol.WRITE );
        keywordsTable.put( "enquanto", Symbol.WHILE );
        keywordsTable.put( "faca", Symbol.DO );
        keywordsTable.put( "fimenquanto", Symbol.ENDWHILE );
        keywordsTable.put( "inteiro", Symbol.INTEGER );
        keywordsTable.put( "boleano", Symbol.BOOLEAN );
        keywordsTable.put( "carac", Symbol.CHAR );
        keywordsTable.put( "verdadeiro", Symbol.TRUE );
        keywordsTable.put( "falso", Symbol.FALSE );
        keywordsTable.put( "&&", Symbol.AND );
        keywordsTable.put( "||", Symbol.OR );
        keywordsTable.put( "not", Symbol.NOT );
        keywordsTable.put( "procedimento", Symbol.PROCEDURE );
        keywordsTable.put( "funcao", Symbol.FUNCTION );
        keywordsTable.put( "retorna", Symbol.RETURN );
     }
     
     
    
    
    public void nextToken() {
        char ch;        
        while (  (ch = input[tokenPos]) == ' ' || ch == '\r' ||
                 ch == '\t' || ch == '\n')  {
            // count the number of lines
          if ( ch == '\n')
            lineNumber++;
          tokenPos++;
          }
        if ( ch == '\0') 
          token = Symbol.EOF;
        else
          if ( input[tokenPos] == '-' && input[tokenPos + 1] == '-' ) {
                // comment found
               while ( input[tokenPos] != '\0'&& input[tokenPos] != '\n' )
                 tokenPos++;
               nextToken();
               }
          else {
            if ( Character.isLetter( ch ) ) {
                // get an identifier or keyword
                StringBuffer ident = new StringBuffer();
                while ( Character.isLetter( input[tokenPos] ) ) {
                    ident.append(input[tokenPos]);
                    tokenPos++;
                }

                stringValue = ident.toString();
                  // if identStr is in the list of keywords, it is a keyword !
                Symbol value = keywordsTable.get(stringValue);

                if ( value == null ) {
                  token = Symbol.IDENT;
                }
                else 
                  token = value;
                if ( Character.isDigit(input[tokenPos]) )
                  error.signal("Palavra seguida de um número");
            }
            else if ( Character.isDigit( ch ) ) {
                // get a number
                StringBuffer number = new StringBuffer();
                while ( Character.isDigit( input[tokenPos] ) ) {
                    number.append(input[tokenPos]);
                    tokenPos++;
                }
                token = Symbol.NUMBER;
                try {
                   numberValue = Integer.valueOf(number.toString()).intValue();
                } catch ( NumberFormatException e ) {
                   error.signal("Número maior que o limite");
                }
                if ( numberValue >= MaxValueInteger )
                   error.signal("Número maior que o limite");
                
            } else {
                tokenPos++;
                switch ( ch ) {
                    case '+' :
                      token = Symbol.PLUS;
                      break;
                    case '-' :
                      token = Symbol.MINUS;
                      break;
                    case '*' :
                      token = Symbol.MULT;
                      break;
                    case '/' :
                      token = Symbol.DIV;
                      break;
                    case '%' :
                      token = Symbol.REMAINDER;
                      break;
                    case '&' :
                        if ( input[tokenPos] == '&'){
                            tokenPos++;
                            token = Symbol.AND;
                            break;
                          }
                        else
                      error.signal("Caractere inválido: '" + ch + "'");
                    case '|' :
                        if ( input[tokenPos] == '|'){
                            tokenPos++;
                            token = Symbol.OR;
                            break;
                          }
                        else
                      error.signal("Caractere inválido: '" + ch + "'");
                    case '<' :
                      if ( input[tokenPos] == '=' ) {
                        tokenPos++;
                        token = Symbol.LE;
                      }
                      else
                          if ( input[tokenPos] == '-'){
                            tokenPos++;
                            token = Symbol.ASSIGN;
                          }
                          else
                            token = Symbol.LT;
                      break;
                    case '>' :
                      if ( input[tokenPos] == '=' ) {
                        tokenPos++;
                        token = Symbol.GE;
                      }
                      else
                        token = Symbol.GT;
                      break;
                    case '=' :
                      if ( input[tokenPos] == '=' ) {
                        tokenPos++;
                        token = Symbol.EQ;
                      }
                      else 
                        token = Symbol.ASSIGN;
                      break;
                    case '!' :
                        if (input[tokenPos] == '='){
                            tokenPos++;
                            token = Symbol.NEQ;
                        }
                      break;
                    case '(' :
                      token = Symbol.LEFTPAR;
                      break;
                    case ')' :
                      token = Symbol.RIGHTPAR;
                      break;
                    case ',' :
                      token = Symbol.COMMA;
                      break;
                    case ';' :
                      token = Symbol.SEMICOLON;
                      break;
                    case '\'' :
                      token = Symbol.CHARACTER;
                      charValue = input[tokenPos];
                      tokenPos++;
                      if ( input[tokenPos] != '\'' )
                        error.signal("Caractere ilegal" + input[tokenPos-1] );
                      tokenPos++;
                      break;
                      // the next four symbols are not used by the language
                      // but are returned to help the error treatment
                    case '{' :
                      token = Symbol.CURLYLEFTBRACE;
                      break;
                    case '}' :
                      token = Symbol.CURLYRIGHTBRACE;
                      break;
                    case '[' :
                      token = Symbol.LEFTSQBRACKET;
                      break;
                    case ']' :
                      token = Symbol.RIGHTSQBRACKET;
                      break;
                    default :
                      error.signal("Caractere invalido: '" + ch + "'");
                }
            }
          }
        lastTokenPos = tokenPos - 1;      
    }

      // return the line number of the last token got with getToken()
    public int getLineNumber() {
        return lineNumber;
    }
        
    public String getCurrentLine() {
        return getLine(lastTokenPos);
    }

    public String getStringValue() {
       return stringValue;
    }
    
    public int getNumberValue() {
       return numberValue;
    }
    
    public char getCharValue() {
       return charValue;
    }

        public int getLineNumberBeforeLastToken() {
        return getLineNumber( beforeLastTokenPos );
    }


            public String getLineBeforeLastToken() {
        return getLine(beforeLastTokenPos);
    }

          private int getLineNumber( int index ) {
        // return the line number in which the character input[index] is
        int i, n, size;
        n = 1;
        i = 0;
        size = input.length;
        while ( i < size && i < index ) {
          if ( input[i] == '\n' )
            n++;
          i++;
        }
        return n;
    }


            private String getLine( int index ) {
        // get the line that contains input[index]. Assume input[index] is at a token, not
        // a white space or newline

        int i = index;
        if ( i == 0 )
          i = 1;
        else
          if ( i >= input.length )
            i = input.length;

        StringBuffer line = new StringBuffer();
          // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' )
          i--;
        if ( input[i] == '\n' )
          i++;
          // go to the end of the line putting it in variable line
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }
        return line.toString();
    }


          // current token
    public Symbol token;
    private String stringValue;
    private int numberValue;
    private char charValue;
    
    private int  tokenPos;
      //  input[lastTokenPos] is the last character of the last token
    private int lastTokenPos;
      // program given as input - source code
    private char []input;
    
    // number of current line. Starts with 1
    private int lineNumber;
      //  input[beforeLastTokenPos] is the last character of the token before the last
      // token found
    private int beforeLastTokenPos;
    
    private CompilerError error;
    private static final int MaxValueInteger = 32768;
}
