/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AST;

/**
 *
 * @author Bruno
 */
public class whileStatement extends Statement {
    public whileStatement( Expr expr, StatementList doPart) {
        this.expr = expr;
        this.doPart = doPart;
       //this.elsePart = elsePart;
    }

    public void genC( PW pw ) {

        pw.print("while ");
        expr.genC(pw, false);
        pw.out.println("{");
        pw.add();
        doPart.genC(pw);
        pw.sub();
        pw.println("}");

        
    }

    private Expr expr;
    private StatementList doPart;
}
