package AST;

public class NumberExpr extends Expr {
    
    public NumberExpr( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    public void genC( PW pw, boolean putParenthesis ) {
    	pw.out.print(String.valueOf(value));
    }
    
    public Type getType() {
        return Type.integerType;
    }
    
    private int value;
}
