package AST;

public class CharExpr extends Expr {
    
    public CharExpr( char value ) {
        this.value = value; 
    }
    
    public void genC( PW pw , boolean putParenthesis ) {
    	pw.out.print("'"+value+"'");
    }
    
    public char getValue() {
        return value;
    }
    
    public Type getType() {
        return Type.charType;
    }
    
    private char value;
}