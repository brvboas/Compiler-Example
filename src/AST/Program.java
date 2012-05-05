package AST;

import java.util.*;

public class Program {
    

    public Program( ArrayList<Subroutine> procfuncList ) {
        this.procfuncList = procfuncList;
    }
    
    public void genC( PW pw ) {
        
        pw.out.println("#include <stdio.h>");
        pw.out.println();
        boolean temBooleano = false;
//        for(Variable v : arrayVariable){
//            if (temBooleano == false && v.getType().getCname().equals("bool")){
                pw.println("enum boolean {");
                pw.println("    true = 1, false = 0");
                pw.println("};");
                pw.println("typedef  enum boolean  bool;");
                pw.out.println();
                temBooleano = true;
//            }

//        }
        // generate code for the declaration of variables

           for( Subroutine s : procfuncList ) {
            s.genC(pw);
            pw.out.println("");
            pw.out.println("");
        }
       
    }                             

    private ArrayList<Subroutine> procfuncList;
}