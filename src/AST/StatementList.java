package AST;

import java.util.*;
public class StatementList {

    public StatementList(ArrayList<Statement> v) {
        this.v = v;
    }
    
    public void genC( PW pw ) {

    	for(Statement st : v){
    		st.genC(pw);
    	}
    	
    }
    
    private ArrayList<Statement> v;
}
    