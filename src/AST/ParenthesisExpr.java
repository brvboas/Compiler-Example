package AST;

public class ParenthesisExpr extends Expr {
    
    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }
    
    public void genC( PW pw, boolean putParenthesis  ) {
    	pw.out.print("(");
    	expr.genC(pw, false);
    	pw.out.print(")");
    }
    
    public Type getType() {
        return expr.getType();
    }
    
    private Expr expr;
}