import frontend.Lexer;
import frontend.GrammarParser;

import java.io.*;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            FileWriter fw = new FileWriter("output.txt",true);
            Lexer lexer = new Lexer(br);
            GrammarParser grammarParser = new GrammarParser(lexer.getSymbol());
            for (String s : grammarParser.getResult()) {
                System.out.println(s);
                fw.write(s + "\n");
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
