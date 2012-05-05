package AST;

import java.io.*;

public class ReturnStatement extends Statement {
    
    public ReturnStatement( Expr expr ) {
        this.expr = expr;
    }
    
    public void genC( PW pw ) {
        pw.print("return ");
        expr.genC(pw, false);
        pw.out.println(";");
    }
    
    private Expr expr;
    
}
    
        
        