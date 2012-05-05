package AST;

import java.io.*;

public class Function extends Subroutine {
    
    public Function( String name ) {
        this.name = name;
    }
    
    public Type getReturnType() {
        return returnType;
    }
    
    public void setReturnType( Type returnType ) {
        this.returnType = returnType;
    }
    
    public void genC( PW pw ) {
        
        pw.out.print(returnType.getCname() + " " + name + "(");
        if ( paramList != null ) 
          paramList.genC(pw);
        pw.out.println(") {");
        pw.add();
        if ( localVarList != null ) 
          localVarList.genC(pw);
        pw.out.println();
        compositeStatement.getStatementList().genC(pw);
        pw.sub();
        pw.out.println("}");
        
    }
    
    private Type returnType;
}
    