package AST;

public class Variable {
    
    public Variable( String name, Type type ) {
        this.name = name;
        this.type = type;
    }
    
    public Variable( String name ) {
        this.name = name;
    }
    
    public void setType( Type type ) {
        this.type = type;
    }
    
    public String getName() { return name; }
    
    public Type getType() {
        return type;
    }
    
    private String name;
    private Type type;
}