package AST;

import java.io.*;

public class ProcedureCall extends Statement {
    
    public ProcedureCall( Procedure procedure, ExprList exprList ) {
        this.procedure = procedure;
        this.exprList = exprList;
    }
    
    public void genC( PW pw ) {
        pw.print( procedure.getName() + "(" );
        if ( exprList != null ) 
          exprList.genC(pw);
        pw.out.println(");");
    }
    
    Procedure procedure;
    ExprList exprList;
    
}