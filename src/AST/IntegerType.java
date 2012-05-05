package AST;

public class IntegerType extends Type {
    
    public IntegerType() {
        super("inteiro");
    }
    
   public String getCname() {
      return "int";
   }
   
}