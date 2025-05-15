import frontend.Lexer;
import frontend.parse.Node;
import midend.Visitor;
import midend.value.user.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import frontend.error.Logger;
import frontend.parse.GrammarParser;
import frontend.visit.ErrorVisitor;

public class Compiler {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            Lexer lexer = new Lexer(br);
            GrammarParser grammarParser = new GrammarParser(lexer.getSymbol(), true);
            Node root = grammarParser.getAstRoot();
            ErrorVisitor errorVisitor = new ErrorVisitor(root);
            errorVisitor.visit();
            if (Logger.getLogger().hasError()) {
                FileWriter fw2 = new FileWriter("error.txt",false);
                Logger.getLogger().printErrors(fw2);
                fw2.close();
            } else {
                FileWriter fw = new FileWriter("llvm_ir.txt",false);
                FileWriter fw1 = new FileWriter("main.ll",false);
                Visitor visitor = new Visitor(root);
                for (User user : visitor.getRootUser().getUsers()) {
                    fw.write(user.toString() + "\n");
                    fw1.write(user + "\n");
                }
                fw.close();
                fw1.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
