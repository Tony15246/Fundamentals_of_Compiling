import frontend.Lexer;
import frontend.parse.GrammarParser;

import java.io.*;

public class Compiler {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            FileWriter fw = new FileWriter("output.txt",false);
            Lexer lexer = new Lexer(br);
            GrammarParser grammarParser = new GrammarParser(lexer.getSymbol(), true);
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
