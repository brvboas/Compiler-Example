package Lexer;

public enum Symbol {

      EOF("eof"),
      IDENT("Ident"),
      NUMBER("Number"),
      PLUS("+"),
      MINUS("-"),
      MULT("*"),
      DIV("/"),
      LT("<"),
      LE("<="),
      GT(">"),
      GE(">="),
      NEQ("!="),
      EQ("=="),
      ASSIGN("<-"),
      LEFTPAR("("),
      RIGHTPAR(")"),
      SEMICOLON(";"),
     // VAR("var"),
      BEGIN("inicio"),
      END("fim"),
      IF("se"),
      THEN("entao"),
      ELSE("senao"),
      ENDIF("fimse"),
      COMMA(","),
      READ("leia"),
      WRITE("escreva"),
      WHILE("enquanto"),
      DO("faca"),
      ENDWHILE("fimenquanto"),
      //COLON(":"),
      INTEGER("inteiro"),
      BOOLEAN("boleano"),
      CHAR("carac"),
      CHARACTER("character"), 
      TRUE("verdadeiro"),
      FALSE("falso"),
      OR   ("||"),
      AND  ("&&"),
      REMAINDER("%"),
      NOT("!"),
        // the following symbols are used only at error treatment
      CURLYLEFTBRACE("{"),
      CURLYRIGHTBRACE("}"),
      LEFTSQBRACKET("["),
      RIGHTSQBRACKET("]"),
      //
      PROCEDURE("procedimento"),
      ENDPROCEDURE("fimprocedimento"),
      FUNCTION("funcao"),
      ENDFUNCTION("fimfuncao"),
      RETURN("return");



      Symbol(String name) {
          this.name = name;
      }

    @Override
      public String toString() {
          return name;
      }

      private String name;

}