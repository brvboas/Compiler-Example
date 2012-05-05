package AST;

public class WriteStatement extends Statement {
    
    public WriteStatement( Expr expr ) {
        this.expr = expr;
    }
 
    public void genC( PW pw ) {
        if(expr.getType() == Type.charType){
        	pw.print("printf(\"%c\",");
			expr.genC(pw, false);
			pw.println(");");
        }
        else{
        	pw.print("printf(\"%d\",");
			expr.genC(pw, false);
			pw.out.println(");");        	
        }
    }
    
    private Expr expr;
}