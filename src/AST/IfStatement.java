package AST;

public class IfStatement extends Statement {
    
    public IfStatement( Expr expr, StatementList thenPart, StatementList elsePart ) {
        this.expr = expr;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }
 
    public void genC( PW pw ) {
        pw.print("if");
        expr.genC(pw, false);
        pw.out.println("{");
        pw.add();
        thenPart.genC(pw);
        pw.sub();
        pw.println("}");
        if(elsePart != null)
        {        	
        	pw.println("else{");
        	pw.add();
        	elsePart.genC(pw);
        	pw.sub();
        	pw.println("}");
        }
    }
    
    private Expr expr;
    private StatementList thenPart, elsePart;
}