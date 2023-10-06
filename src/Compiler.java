import frontend.Lexer;

import java.io.*;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            FileWriter fw = new FileWriter("output.txt",false);
            Lexer lexer = new Lexer(br);
            ArrayList<Lexer.Token> tokens = lexer.getSymbol();
            for (Lexer.Token token : tokens) {
                fw.write(token.toString() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
