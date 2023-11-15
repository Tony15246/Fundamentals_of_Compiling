import frontend.GrammarParser;
import frontend.Lexer;
import midend.Visitor;
import midend.value.user.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Compiler {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            FileWriter fw = new FileWriter("llvm_ir.txt",false);
            FileWriter fw1 = new FileWriter("main.ll",false);
            Lexer lexer = new Lexer(br);
            GrammarParser grammarParser = new GrammarParser(lexer.getSymbol());
            Visitor visitor = new Visitor(grammarParser.getAstRoot());
            for (User user : visitor.getRootUser().getUsers()) {
                fw.write(user.toString() + "\n");
                fw1.write(user + "\n");
            }
            fw.close();
            fw1.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
