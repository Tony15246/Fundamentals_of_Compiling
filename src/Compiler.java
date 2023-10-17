import frontend.Lexer;
import frontend.error.Logger;
import frontend.parse.GrammarParser;
import frontend.visit.Visitor;

import java.io.*;

public class Compiler {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            FileWriter fw = new FileWriter("error.txt",false);
            Lexer lexer = new Lexer(br);
            GrammarParser grammarParser = new GrammarParser(lexer.getSymbol(), true);
            Visitor visitor = new Visitor(grammarParser.getAstRoot());
            visitor.visit();
            Logger.getLogger().printErrors(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
