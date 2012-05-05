package AST;

public class BooleanType extends Type {
    
   public BooleanType() { super("boleano"); }
   
   public String getCname() {
      return "bool";
   }
}
