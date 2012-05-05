package AST;

import java.io.*;

public class FunctionCall extends Expr {
    
    public FunctionCall( Function function, ExprList exprList ) {
        this.function = function;
        this.exprList = exprList;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.out.print( function.getName() + "(" );
        if ( exprList != null ) 
          exprList.genC(pw);
        pw.out.print( ")");
    }
    
    public Type getType() {
        return function.getReturnType();
    }
    
    Function function;
    ExprList exprList;
    
}
