package AST;

public class BooleanExpr extends Expr {
    
    public BooleanExpr( boolean value ) {
        this.value = value;
    }
    
    public void genC( PW pw, boolean putParenthesis  ) {
    	if(value){
    		pw.out.print("1");
    		return;
    	}
    	pw.out.print("0");
    }
    
    public Type getType() {
        return Type.booleanType;
    }
    
    public static BooleanExpr True  = new BooleanExpr(true);
    public static BooleanExpr False = new BooleanExpr(false);
    
    private boolean value;
}
