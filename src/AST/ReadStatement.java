package AST;

public class ReadStatement extends Statement {
    public ReadStatement( Variable v ) {
        this.v = v;
    }
 
    public void genC( PW pw ) {
    	if(v.getType().getName().equals("char")){
    		pw.println("scanf(\"%c\",&"
    				+v.getName()+");");
    		return;
    	}
    	pw.println("scanf(\"%d\",&"
				+v.getName()+");");
	
    	
    }
    private Variable v;
}