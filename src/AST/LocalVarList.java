package AST;

import java.io.*;
import java.util.*;

public class LocalVarList {
    
    public LocalVarList() {
        v = new ArrayList<Variable>();
    }
    
    public void addElement( Variable variable ) {
        v.add(variable);
    }
    
    public void genC( PW pw ) {

        for( Variable variable : v )
             pw.println( variable.getType().getCname() + " " +
                variable.getName() + ";" );
    }
    
    ArrayList<Variable> v;
}
    