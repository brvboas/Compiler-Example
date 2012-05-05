/*
 * Compiler.java
 *
 * Trabalho de Compiladores - segunda fase
 *
 * @Authors
 * Bruno Villas Boas da Costa
 * &
 * Renato Jensen Filho
 *
 */

/*
 *  Gramática:

Program ::= ProcFunc { ProcFunc }
ProcFunc ::= Procedure | Function
Procedure ::= "procedimento" Ident '(' ParamList ')' [VarDecList] CompositeStatement "fimprocedimento;"
Function ::= "funcao" Type Ident '(' ParamList ')' [ VarDecList ] CompositeStatement "fimfuncao;"
ParamList ::= | ParamDec {';' ParamDec}
ParamDec ::= Type Ident
CompositeStatement ::= "inicio" StatementList "fim"
StatementList ::= | Statement ";" StatementList
Statement ::= AssignmentStatement | IfStatement | ReadStatement | WriteStatement | WhileStatement | ProcedureCall | ReturnStatement
AssignmentStatement ::= Variable "<-" OrExpr
IfStatement ::= "se" OrExpr "entao" StatementList ["senao" StatementList] "fimse;"
ReadStatement ::= "leia" '(' Variable ')'
WriteStatement ::= "escreva" '(' OrExpr ')'
WhileStatement ::= "enquanto" OrExpr "faca" StatementList  "fimenquanto;"
ProcedureCall ::=  Ident '(' ExprList ')'
ExprList ::= | OrExpr {';' OrExpr}
ReturnStatement ::= "retorna" OrExpr
VarDecList ::= VarDecList2 {VarDecList2}
VarDecList2 ::= Type Variable {',' Variable} ';'
Variable ::= Letter {Letter}
Type ::= "inteiro" | "carac" | "boleano"
OrExpr ::= AndExpr [ "||" AndExpr]
AndExpr ::= RelExpr [ "&&" RelExpr]
RelExpr ::= AddExpr [ RelOp AddExpr]
AddExpr ::= MultExpr {AddOp MultExpr}
MultExpr ::= SimprExpr {MultOp SimpleExpr}
SimpleExpr ::= Number | Character | '(' OrExpr ')' | "!" SimpleExpr | Variable | '(' OrExpr ')' | "verdadeiro" | "falso"
RelOp ::= '<' | '<=' | '>' | '>=' | '==' | '!='
AddOp ::= '+' | '-'
Number ::= ['+'|'-'] Digit {Digit}
Digit ::= '0' | '1' | ... | '9'
Letter ::= 'A' | 'B' | ... | 'Z' | 'a' | 'b' | ... | 'z'
 *
 */
import AST.*;
import AuxComp.*;
import java.util.*;
import Lexer.*;
import java.io.*;

public class Compiler {

    private Boolean hasReturn = false;
    // compile must receive an input with an character less than

    public Program compile(char[] input, PrintWriter outError) {

        symbolTable = new SymbolTable();
        error = new CompilerError(lexer, new PrintWriter(outError));
        lexer = new Lexer(input, error);
        error.setLexer(lexer);

        lexer.nextToken();
        return program();
    }

    /**
     * program(): função que cria 1 ou mais ProcFunc
     *
     */
    private Program program() {

        ArrayList<Subroutine> procfuncList = new ArrayList<Subroutine>();

        while (lexer.token == Symbol.PROCEDURE
                || lexer.token == Symbol.FUNCTION) {
            procfuncList.add(procFunc());
        }

        Program program = new Program(procfuncList);
        if (lexer.token != Symbol.EOF) {
            error.show("EOF esperado");
        }
        // semantics analysis
        // there must be a procedure called principal
        Subroutine mainProc;
        if ((mainProc = (Subroutine) symbolTable.getInGlobal("principal")) == null) {
            error.show("O código precisa ter um procedimento chamado 'principal'");
        }
        return program;
    }

    /*
     *procFunc(): funcao que verifica se o statement é um procedimento ou uma função
     * caso seja procedimento, irá chamar o método do procedimento, caso seja funcao
     * chamará o metodo da funcao.
     */
    private Subroutine procFunc() {
        // Procedure ::= "procedure" Ident '(' ParamList ')'
        //     [ LocalVarDec ] CompositeStatement
        // Function ::= "function" Ident '(' ParamList ')' ':' Type
        //     [ LocalVarDec ] CompositeStatement

        boolean isFunction;


        if (lexer.token == Symbol.PROCEDURE) {
            isFunction = false;
        } else if (lexer.token == Symbol.FUNCTION) {
            isFunction = true;
        } else {
            // should never occur
            error.show("Erro interno do compilador");
            return null;
        }

        lexer.nextToken();
        Type t = null;
        //tipo
        if (isFunction) {
            t = type();
        }

        if (lexer.token != Symbol.IDENT) {
            error.show("Identificador esperado");
        }
        String name = (String) lexer.getStringValue();
        // Symbol table now searches for an identifier in the scope order. First
        // the local variables and parameters and then the procedures and functions.
        // at this point, there should not be any local variables/parameters in the
        // symbol table.
        Subroutine s = (Subroutine) symbolTable.getInGlobal(name);
        // semantic analysis
        // identifier is in the symbol table
        if (s != null) {
            error.show("Subrotina " + name + " já foi declarada!");
        }
        lexer.nextToken();

        if (isFunction) {
            // currentFunction is used to store the function being compiled or null if it is a procedure
            s = currentFunction = new Function(name);
        } else {
            s = new Procedure(name);
            currentFunction = null;
        }

        // insert s in the symbol table
        symbolTable.putInGlobal(name, s);
        if (isFunction) {
            ((Function) s).setReturnType(t);
        }
        if (lexer.token != Symbol.LEFTPAR) {
            error.show("( esperado");
            lexer.skipBraces();
        } else {
            lexer.nextToken();
        }

        // semantic analysis
        // if the subroutine is "principal", it must be a parameterless procedure
        if (name.compareTo("principal") == 0 && (lexer.token != Symbol.RIGHTPAR
                || isFunction)) {
            error.show("principal tem que ser um procedimento sem parâmetros!");
        }

        s.setParamList(paramList());
        if (lexer.token != Symbol.RIGHTPAR) {
            error.show(") esperado");
            lexer.skipBraces();
        } else {
            lexer.nextToken();
        }

        if (lexer.token == Symbol.INTEGER || lexer.token == Symbol.CHAR || lexer.token == Symbol.BOOLEAN) {

            s.setLocalVarList(localVarDec());
        }
        s.setCompositeStatement(compositeStatement());

        //If the subroutine is a function, you need to check if there is a returnstatment declared inside
        if (isFunction && !hasReturn) {
            String str = ((Type) ((Function) s).getReturnType()).getCname() + " " + s.getName() + "(...)";
            error.show("A função necessita de um retorno", str);
        }
        symbolTable.removeLocalIdent();
        return s;
    }

    /**
     * localVarDec(): funcao que é chamada por um procedimento ou funcao para criar
     * a lista de variáveis locais dos mesmos.
     *
     */
    private LocalVarList localVarDec() {
        // LocalVarDec ::= "var" VarDecList
        // VarDecList ::= VarDecList2  { VarDecList2  }

        LocalVarList localVarList = new LocalVarList();

        varDecList2(localVarList);
        while (lexer.token == Symbol.INTEGER || lexer.token == Symbol.CHAR || lexer.token == Symbol.BOOLEAN) {
            varDecList2(localVarList);
        }
        return localVarList;
    }


    /*
    paramList(): funcao que cria 0 ou mais funcoes do tipo ParamDec
     */
    private ParamList paramList() {
        // ParamList ::= | ParamDec { ';' ParamDec }

        ParamList paramList = null;
        if (lexer.token != Symbol.INTEGER && lexer.token != Symbol.CHAR && lexer.token != Symbol.BOOLEAN) {
            if (lexer.token != Symbol.RIGHTPAR) {
                error.show("Tipo esperado");
            } else {
                return null;
            }
        }
        // get the type
        paramList = new ParamList();
        paramDec(paramList);
        while (lexer.token == Symbol.SEMICOLON) {
            lexer.nextToken();
            paramDec(paramList);
        }

        return paramList;
    }

    /*
    paramDec(ParamList): funcao que é utilizada para declaracao de variáveis
     */
    private void paramDec(ParamList paramList) {
        // ParamDec ::= Type  Ident


        Type typeVar = type();
        if (lexer.token != Symbol.IDENT) {
            error.show("Identificador esperado");
        }
        // name of the identifier
        String name = (String) lexer.getStringValue();
        lexer.nextToken();
        // semantic analysis
        // if the name is in the symbol table and the scope of the name is local,
        // the variable is been declared twice.
        if (symbolTable.getInLocal(name) != null) {
            error.show("O parametro " + name + " já foi declarado");
        }

        // variable does not have a type yet
        Parameter v = new Parameter(name);
        // inserts the variable in the symbol table. The name is the key and an
        // object of class Variable is the value. Hash tables store a pair (key, value)
        // retrieved by the key.
        symbolTable.putInLocal(name, v);

        // add type to the variable
        v.setType(typeVar);
        // add v to the list of parameter declarations
        paramList.addElement(v);


    }

    /*
     * compositeStatement(): funcao que eh chamada por procedimentos e funcoes para
     * iniciá-las e finalizá-las.
     */
    private CompositeStatement compositeStatement() {
        // CompositeStatement ::= "begin" StatementList "end"
        //  StatementList ::= | Statement ";" StatementList

        if (lexer.token != Symbol.BEGIN) {
            error.show("\"inicio\" esperado");
        }
        lexer.nextToken();
        StatementList sl = statementList();
        if (lexer.token != Symbol.END) {
            error.show("\"fim\" esperado");
        }
        lexer.nextToken();
        return new CompositeStatement(sl);
    }

    /*
     * statementList(): funcao que cria uma lista de statements que serao executados
     * dentro de um compositeStatement.
     */
    private StatementList statementList() {
        Symbol tk;
        Statement astatement;
        ArrayList<Statement> v = new ArrayList<Statement>();

        // statements always begin with an identifier, if, read or write, ...
        while ((tk = lexer.token) != Symbol.END
                && tk != Symbol.ELSE
                && tk != Symbol.ENDIF && tk != Symbol.ENDWHILE) {
            astatement = null;
            try {
                // statement() should return null in a serious error
                astatement = statement();
            } catch (StatementException e) {
                lexer.skipToNextStatement();
            }
            if (astatement != null) {
                v.add(astatement);
                if (lexer.token != Symbol.SEMICOLON) {
                    error.show("; esperado", true);
                    lexer.skipPunctuation();
                } else {
                    lexer.nextToken();
                }
            }
        }
        return new StatementList(v);
    }

    /*
     * statement(): funcao que é chamada por um statementList onde chama a funcao
     * correta para tratar o statement em questao
     */
    private Statement statement() throws StatementException {
        /*  Statement ::= AssignmentStatement | IfStatement | ReadStatement |
        WriteStatement | returnStatement
         */

        switch (lexer.token) {
            case IDENT:
                if (symbolTable.get(lexer.getStringValue()) instanceof Procedure) {
                    return procedureCall();
                } else {
                    return assignmentStatement();
                }
            case IF:
                return ifStatement();
            case READ:
                return readStatement();
            case WRITE:
                return writeStatement();
            case WHILE:
                return whileStatement();
            case BEGIN:
                return compositeStatement();
            case RETURN:
                hasReturn = true;
                return returnStatement();
            default:
                error.show("Statement esperado");
                throw new StatementException();
        }
    }

    /*
     * assignmentStatement(): funcao chamada por uma funcao statement() para expressoes
     * de atribuicao (do tipo 'a <- b')
     */
    private AssignmentStatement assignmentStatement() {


        // is the variable in the symbol table ? Variables are inserted in the
        // symbol table when they are declared. It the variable is not there, it has
        // not been declared.

        Variable v = checkVariable();
        // was it in the symbol table ?
        if (lexer.token != Symbol.ASSIGN) {
            error.show("<- esperado");
            lexer.skipToNextStatement();
            return null;
        }
        lexer.nextToken();
        Expr right = orExpr();
        // semantic analysis
        // check if expression has the same type as variable
        if (v.getType() != right.getType()) {
            error.show("Erro de tipo na atribuição");
        }

        return new AssignmentStatement(v, right);
    }


    /*
     * ifStatement(): funcao chamada por uma funcao statement() para expressoes
     * de condicao (do tipo 'se a entao b senao c')
     */
    private IfStatement ifStatement() {

        lexer.nextToken();
        Expr e = orExpr();
        // semantic analysis
        // check if expression has type boolean
        if (e.getType() != Type.booleanType) {
            error.show("Tipo booleano é esperado em uma expressão SE");
        }

        if (lexer.token != Symbol.THEN) {
            error.show("entao esperado");
        } else {
            lexer.nextToken();
        }
        StatementList thenPart = statementList();
        StatementList elsePart = null;
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            elsePart = statementList();
        }
        if (lexer.token != Symbol.ENDIF) {
            error.show("\"fimse\" esperado");
        }
        lexer.nextToken();
        return new IfStatement(e, thenPart, elsePart);
    }


    /*
     * whileStatement(): funcao chamada por uma funcao statement() para expressoes
     * de laço (do tipo 'enquanto a faca b')
     */
    private Statement whileStatement() throws StatementException {
        // semantic analysis
        // check if expression has type boolean


        lexer.nextToken();
        Expr e = orExpr();
        // semantic analysis
        // check if expression has type boolean
        if (e.getType() != Type.booleanType) {
            error.show("Tipo booleano é esperado em uma expressão \"enquanto\"");
        }

        if (lexer.token != Symbol.DO) {
            error.show(" \" faca\" esperado");
        } else {
            lexer.nextToken();
        }
        StatementList doPart = statementList();
        if (lexer.token != Symbol.ENDWHILE) {
            error.show("\"fim enquanto\" esperado");
        }
        lexer.nextToken();
        return new whileStatement(e, doPart);
    }

    /*
     * readStatement(): funcao chamada por uma funcao statement() para expressoes
     * de leitura (do tipo 'leia(a)')
     */
    private ReadStatement readStatement() {
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            error.show("( esperado");
            lexer.skipBraces();
        } else {
            lexer.nextToken();
        }
        if (lexer.token != Symbol.IDENT) {
            error.signal("Identificador esperado");
        }
        // semantic analysis
        // check if the variable was declared
        String name = lexer.getStringValue();
        Variable v = (Variable) symbolTable.get(name);
        if (v == null) {
            error.show("Variável " + name + " ainda nao foi declarada");

            symbolTable.putInLocal(name, new Variable(name, null));
        }
        // semantic analysis
        // check if variable has type char or integer
        if (v.getType() != Type.charType && v.getType() != Type.integerType) {
            error.show("A variável deve ter um tipo carac ou inteiro");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.RIGHTPAR) {
            error.show(") esperado");
            lexer.skipBraces();
            lexer.skipToNextStatement();
            return null;
        } else {
            lexer.nextToken();
            return new ReadStatement(v);
        }
    }


    /*
     * writeStatement(): funcao chamada por uma funcao statement() para expressoes
     * de escrita (do tipo 'escreva(b)')
     */
    private WriteStatement writeStatement() {
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            error.show("( esperado");
            lexer.skipBraces();
        } else {
            lexer.nextToken();
        }
        // expression may be of any type
        Expr e = orExpr();
        if (lexer.token != Symbol.RIGHTPAR) {
            error.show(") esperado");
            lexer.skipBraces();
            lexer.skipToNextStatement();
            return null;
        } else {
            lexer.nextToken();
            return new WriteStatement(e);
        }
    }

    /*
     * procedureCall(): funcao utilizada dentro de um compositeStatemente() para chamada
     * de funcoes ou procedimentos
     */
    private ProcedureCall procedureCall() {
        // we already know the identifier is a procedure. So we need not to check it
        // again.
        ExprList anExprList = null;

        String name = (String) lexer.getStringValue();
        lexer.nextToken();
        Procedure p = (Procedure) symbolTable.getInGlobal(name);
        if (lexer.token != Symbol.LEFTPAR) {
            error.show("( esperado");
            lexer.skipBraces();
        } else {
            lexer.nextToken();
        }

        if (lexer.token != Symbol.RIGHTPAR) {
            // The parameter list is used to check if the arguments to the
            // procedure have the correct types
            anExprList = exprList(p.getParamList());
            if (lexer.token != Symbol.RIGHTPAR) {
                error.show("erro na expressão");
            } else {
                lexer.nextToken();
            }
        } else {
            // semantic analysis
            // does the procedure has no parameter ?
            if (p.getParamList() != null && p.getParamList().getSize() != 0) {
                error.signal("Parametro esperado");
            }
            lexer.nextToken();
        }

        return new ProcedureCall(p, anExprList);
    }

    /*
     * exprList(ParamList): funcao que faz uma expressao de acordo com o operador.
     * ela chamará um OrExpr() que, recusrivamente chamará outras funcoes.
     */
    private ExprList exprList(ParamList paramList) {
        ExprList anExprList;
        boolean firstErrorMessage = true;

        if (lexer.token == Symbol.RIGHTPAR) {
            return null;
        } else {
            Parameter parameter;
            int sizeParamList = paramList.getSize();
            Iterator e = paramList.getParamList().iterator();
            anExprList = new ExprList();
            while (true) {
                parameter = (Parameter) e.next();
                // semantic analysis
                // does the procedure has one more parameter ?
                if (sizeParamList < 1 && firstErrorMessage) {
                    error.show("Número errado de parâmetros na chamada");
                    firstErrorMessage = false;
                }
                sizeParamList--;
                Expr anExpr = orExpr();
                if (parameter.getType() != anExpr.getType()) {
                    error.show("Erro de tipo na passagem de parâmetro");
                }
                anExprList.addElement(anExpr);
                if (lexer.token == Symbol.SEMICOLON) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
            // semantic analysis
            // the procedure may need more parameters
            if (sizeParamList > 0 && firstErrorMessage) {
                error.show("Número errado de parâmetros");
            }
            return anExprList;
        }
    }


    /*
     * returnStatement(): funcao chamada por uma funcao statement() para expressoes
     * de retorno de funcao (do tipo 'retorna a')
     */
    private ReturnStatement returnStatement() {

        lexer.nextToken();
        Expr e = orExpr();
        // semantic analysis
        // Are we inside a function ?
        if (currentFunction == null) {
            error.show("expressao de retorno dentro de um procedimento");
        } else if (currentFunction.getReturnType() != e.getType()) {
            error.show("O tipo do retorno é diferente do tipo da função");
        }
        return new ReturnStatement(e);
    }

    /*checkVariable(): funcao que checa se uma variável está declarada,
     * caso nao esteja, declara-a sem um tipo;
     *
     */
    private Variable checkVariable() {
        // tests if the current identifier is a declared variable. If not,
        // declares it with the type Type.undefinedType.
        // assume lexer.token == Symbol.IDENT

        Variable v = null;

        String name = (String) lexer.getStringValue();
        try {
            v = (Variable) symbolTable.getInLocal(name);
        } catch (Exception e) {
        }
        // semantic analysis
        // was the variable declared ?
        if (v == null) {
            error.show("Variável " + name + " não foi declarada");
            v = new Variable(name, null);
            symbolTable.putInLocal(name, v);
        }
        lexer.nextToken();
        return v;
    }

    /*
     * varDecList2(LocalVarList): funcao que faz a declaracao de um tipo para uma variável.
     */
    private void varDecList2(LocalVarList localVarList) {
        //  VarDecList2 ::= Type Ident { ',' Ident } ';'

        ArrayList<Variable> lastVarList = new ArrayList<Variable>();

        if (lexer.token != Symbol.INTEGER && lexer.token != Symbol.CHAR && lexer.token != Symbol.BOOLEAN) {
            error.show("Tipo esperado");
        }
        // get the type
        Type typeVar = type();
        //lexer.nextToken();

        while (true) {

            if (lexer.token != Symbol.IDENT) {
                error.show("Identificador esperado");
            }
            // name of the identifier
            String name = lexer.getStringValue();
            lexer.nextToken();

            // semantic analysis
            // if the name is in the symbol table, the variable is been declared twice.
            if (symbolTable.getInLocal(name) != null) {
                error.show("Variavel " + name + " já foi declarada");
            }

            // variable does not have a type yet
            Variable v = new Variable(name, typeVar);
            // inserts the variable in the symbol table. The name is the key and an
            // object of class Variable is the value. Hash tables store a pair (key, value)
            // retrieved by the key.
            symbolTable.putInLocal(name, v);
            // list of the last variables declared. They don't have types yet
            lastVarList.add(v);

            if (lexer.token == Symbol.COMMA) {
                lexer.nextToken();
            } else {
                break;
            }
        }
//;
        for (Variable v : lastVarList) {
            v.setType(typeVar);
            // add variable to the list of variable declarations
            localVarList.addElement(v);

        }
        if (lexer.token != Symbol.SEMICOLON) {
            error.show("; esperado");
            lexer.skipPunctuation();
        }
        lexer.nextToken();
    }

    /*
     * type(): funcao que atribuirá um tipo a uma variável
     */
    private Type type() {
        Type result;
        switch (lexer.token) {
            case INTEGER:
                result = Type.integerType;
                break;
            case BOOLEAN:
                result = Type.booleanType;
                break;
            case CHAR:
                result = Type.charType;
                break;
            default:
                error.show("Tipo esperado");
                result = null;
        }
        lexer.nextToken();
        return result;
    }

    /*todas as funcoes deste ponto até o fim do arquivo sao para verificacao do tipo de
    operador que será utilizado em uma expressao. Caso nao seja um operador, será
    um numero e este será atribuído a operacao.
     */
    private Expr orExpr() {
        /*
        OrExpr ::= AndExpr [ "||" AndExpr ]
         */

        Expr left, right;
        left = andExpr();
        if (lexer.token == Symbol.OR) {
            lexer.nextToken();
            right = andExpr();
            // semantic analysis
            if (left.getType() != Type.booleanType
                    || right.getType() != Type.booleanType) {
                error.show("Expressao do tipo booleano esperado");
            }
            left = new CompositeExpr(left, Symbol.OR, right);
        }
        return left;
    }

    private Expr andExpr() {
        /*
        AndExpr ::= RelExpr [ "&&" RelExpr ]
         */
        Expr left, right;
        left = relExpr();
        if (lexer.token == Symbol.AND) {
            lexer.nextToken();
            right = relExpr();
            // semantic analysis
            if (left.getType() != Type.booleanType
                    || right.getType() != Type.booleanType) {
                error.show("Expressao do tipo booleano esperado");
            }
            left = new CompositeExpr(left, Symbol.AND, right);
        }
        return left;
    }

    private Expr relExpr() {
        /*
        RelExpr ::= AddExpr [ RelOp AddExpr ]
         */
        Expr left, right;
        left = addExpr();
        Symbol op = lexer.token;
        if (op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE || op == Symbol.LT
                || op == Symbol.GE || op == Symbol.GT) {
            lexer.nextToken();
            right = addExpr();
            // semantic analysis
            if (left.getType() != right.getType()) {
                error.show("Erro de tipo na expressao");
            }
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr addExpr() {
        /*
        AddExpr ::= MultExpr { AddOp MultExpr }
        
         */
        Symbol op;
        Expr left, right;
        left = multExpr();
        while ((op = lexer.token) == Symbol.PLUS
                || op == Symbol.MINUS) {
            lexer.nextToken();
            right = multExpr();
            // semantic analysis
            if (left.getType() != Type.integerType
                    || right.getType() != Type.integerType) {
                error.show("Expressao do tipo inteiro esperado");
            }
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr multExpr() {
        /*
        MultExpr ::= SimpleExpr { MultOp SimpleExpr }
         */
        Expr left, right;
        left = simpleExpr();
        Symbol op;
        while ((op = lexer.token) == Symbol.MULT
                || op == Symbol.DIV || op == Symbol.REMAINDER) {
            lexer.nextToken();
            right = simpleExpr();
            // semantic analysis
            if (left.getType() != Type.integerType
                    || right.getType() != Type.integerType) {
                error.show("Expressao do tipo inteiro esperado");
            }
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr simpleExpr() {
        /*
        SimpleExpr ::= Number |  "verdadeiro" | "falso" | Character
        | '(' orExpr ')' | "!" SimpleExpr | Variable
         */

        Expr e;

        // note we test the lexer.getToken() to decide which production to use
        switch (lexer.token) {
            case NUMBER:
                return number();
            case TRUE:
                lexer.nextToken();
                return BooleanExpr.True;
            case FALSE:
                lexer.nextToken();
                return BooleanExpr.False;
            case CHARACTER:
//              get the token with getToken.
//              then get the value of it, with has the type Object
//              convert the object to type Character using a cast
//              call method charValue to get the character inside the object
                char ch = lexer.getCharValue();
                lexer.nextToken();
                return new CharExpr(ch);
            case LEFTPAR:
                lexer.nextToken();
                e = orExpr();
                if (lexer.token != Symbol.RIGHTPAR) {
                    error.show(") esperado");
                    lexer.skipBraces();
                } else {
                    lexer.nextToken();
                }
                return new ParenthesisExpr(e);
            case NOT:
                lexer.nextToken();
                e = orExpr();
                // semantic analysis
                if (e.getType() != Type.booleanType) {
                    error.show("Expressao do tipo booleano esperado");
                }
                return new UnaryExpr(e, Symbol.NOT);
            case PLUS:
                lexer.nextToken();
                e = orExpr();
                // semantic analysis
                if (e.getType() != Type.integerType) {
                    error.show("Expressao do tipo inteiro esperado");
                }
                return new UnaryExpr(e, Symbol.PLUS);
            case MINUS:
                lexer.nextToken();
                e = orExpr();
                // semantic analysis
                if (e.getType() != Type.integerType) {
                    error.show("Expressao do tipo inteiro esperado");
                }
                return new UnaryExpr(e, Symbol.MINUS);
            default:
                // an identifier
                if (lexer.token != Symbol.IDENT) {
                    error.show("Identificador esperado");
                    lexer.nextToken();
                    return new VariableExpr(new Variable("nameless", null));
                } else {
                    // this part needs to be improved. If the compiler finds
                    // a call to a function that was not declared, it will sign an
                    // error  "Identifier was not declared" and signal errors because of the
                    // parentheses following the function name. This can be corrected.
                    String name = (String) lexer.getStringValue();
                    // is it a function ?
                    Object objIdent = symbolTable.get(name);
                    if (objIdent == null) {
                        error.show("Identificador não foi declarado");
                        lexer.nextToken();
                        if (lexer.token != Symbol.LEFTPAR) {
                            Variable newVariable = new Variable(name, null);
                            symbolTable.putInLocal(name, newVariable);
                            return new VariableExpr(newVariable);
                        } else {
                            Function falseFunction = new Function(name);
                            falseFunction.setReturnType(null);
                            falseFunction.setCompositeStatement(null);
                            symbolTable.putInGlobal(name, falseFunction);
                            objIdent = falseFunction;
                        }
                    }

                    if (objIdent instanceof Subroutine) {
                        if (objIdent instanceof Function) {
                            return functionCall();
                        } else {
                            error.show("Tentativa de chamar um procedimento em uma expressão");
                            procedureCall();
                            return new VariableExpr(new Variable("nameless", null));
                        }
                    } else {
                        // it is a variable
                        Variable v = (Variable) objIdent;
                        lexer.nextToken();
                        return new VariableExpr(v);
                    }
                }
        }

    }

    private FunctionCall functionCall() {
        // we already know the identifier is a function. So we
        // need not to check it again.
        ExprList anExprList = null;

        String name = (String) lexer.getStringValue();
        lexer.nextToken();
        Function p = (Function) symbolTable.getInGlobal(name);
        if (lexer.token != Symbol.LEFTPAR) {
            error.show("( esperado");
            lexer.skipBraces();
        } else {
            lexer.nextToken();
        }

        if (lexer.token != Symbol.RIGHTPAR) {
            // The parameter list is used to check if the arguments to the
            // procedure have the correct types
            anExprList = exprList(p.getParamList());
            if (lexer.token != Symbol.RIGHTPAR) {
                error.show("Erro na expressão");
            } else {
                lexer.nextToken();
            }
        } else {
            // semantic analysis
            // does the procedure has no parameter ?
            if (p.getParamList() != null && p.getParamList().getSize() != 0) {
                error.show("Parametro esperado");
            }
            lexer.nextToken();
        }

        return new FunctionCall(p, anExprList);
    }

    private NumberExpr number() {

        NumberExpr e = null;

        // the number value is stored in lexer.getToken().value as an object of Integer.
        // Method intValue returns that value as an value of type int.
        int value = lexer.getNumberValue();
        lexer.nextToken();
        return new NumberExpr(value);
    }
    private SymbolTable symbolTable;
    private Lexer lexer;
    private CompilerError error;
    private Function currentFunction;
}
