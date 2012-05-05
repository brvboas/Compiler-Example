
import AST.*;
import java.io.*;

public class Main {
    
    public static void main( String []args ) {
        
        File file;
        FileReader stream;
        int numChRead;
        Program program;
        
        if ( args.length != 2 )  {
            System.out.println("Uso:\n   Main I/O");
            System.out.println("I é o arquivo a ser compilado");
            System.out.println("O é o arquivo onde o código gerado será salvo");
        }
        else {
           file = new File(args[0]);
           if ( ! file.exists() || ! file.canRead() ) {
             System.out.println("O arquivo " + args[0] + " não existe ou nao pôde ser lido");
             throw new RuntimeException();
           }
           try { 
             stream = new FileReader(file);  
            } catch ( FileNotFoundException e ) {
                System.out.println("Arquivo não existe mais");
                throw new RuntimeException();
            }
                // one more character for '\0' at the end that will be added by the
                // compiler
            char []input = new char[ (int ) file.length() + 1 ];
            
            try {
              numChRead = stream.read( input, 0, (int ) file.length() );
            } catch ( IOException e ) {
                System.out.println("Erro ao ler o arquivo " + args[0]);
                throw new RuntimeException();
            }
                
            if ( numChRead != file.length() ) {
                System.out.println("Erro de leitura");
                throw new RuntimeException();
            }
            try {
              stream.close();
            } catch ( IOException e ) {
                System.out.println("Erro na manipulacao do arquivo " + args[0]);
                throw new RuntimeException();
            }
                

            Compiler compiler = new Compiler();
            FileOutputStream  outputStream;
            try { 
               outputStream = new FileOutputStream(args[1]);
            } catch ( IOException e ) {
                System.out.println("Arquivo " + args[1] + " não pode ser aberto para escrita");
                throw new RuntimeException();
            }
            PrintWriter printWriter = new PrintWriter(outputStream);
            program = null;
              // the generated code goes to a file and so are the errors
            try {   
              program  = compiler.compile(input, printWriter );
            } catch ( RuntimeException e ) {
                System.out.println(e);
            }
            if ( program != null ) {
              PW pw = new PW();
              pw.set(printWriter);
              program.genC(pw);
              if ( printWriter.checkError() ) {
                  System.out.println("Há um erro na saída");
              }
            }
        }
    }
}
        