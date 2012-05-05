package AuxComp;


import java.util.*;

public class SymbolTable {
    
    public SymbolTable() {
        globalTable = new Hashtable();
        localTable  = new Hashtable();
    }
    
    public Object putInGlobal( String key, Object value ) {
       return globalTable.put(key, value);
    }

    public Object putInLocal( String key, Object value ) {
       return localTable.put(key, value);
    }
    
    public Object getInLocal( Object key ) {
       return localTable.get(key);
    }
    
    public Object getInGlobal( Object key ) {
       return globalTable.get(key);
    }
    
    public Object get( String key ) {
        // returns the object corresponding to the key. 
        Object result;
        if ( (result = localTable.get(key)) != null ) {
              // found local identifier
            return result;
        }
        else {
              // global identifier, if it is in globalTable
            return globalTable.get(key);
        }
    }

    public void removeLocalIdent() {
           // remove all local identifiers from the table
         localTable.clear();
    }
      
        
    private Hashtable globalTable, localTable;
}
            