package AST;

public class Parameter extends Variable {
    public Parameter( String name, Type type ) {
        super(name, type);
    }
    
    public Parameter( String name ) {
        super(name);
    }

}