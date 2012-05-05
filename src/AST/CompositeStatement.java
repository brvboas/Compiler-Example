package AST;

import java.util.*;
import java.io.*;

public class CompositeStatement extends Statement {
    
    public CompositeStatement( StatementList statementList ) {
        this.statementList = statementList;
    }
    
    public void genC( PW pw ) {
        pw.println("{");
        if ( statementList != null ) {
           pw.add();
           statementList.genC(pw);
           pw.sub();
        }
        pw.println("}");
    }
    
    public StatementList getStatementList() { return statementList; }
    
    private StatementList statementList;
}
