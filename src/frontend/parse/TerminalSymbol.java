package frontend.parse;

import frontend.Lexer;

import java.util.ArrayList;

public class TerminalSymbol implements Node {
    private Node parent;
    private Lexer.Token token;
    private Boolean correct;

    @Override
    public void addChild(Node child) {
        System.err.println("IN LeafNode: Try to add child to a leaf node!");
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public ArrayList<Node> getChildren() {
        System.err.println("IN LeafNode: Try to get child from a leaf node!");
        return null;
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    public Lexer.Token getToken() {
        return this.token;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    @Override
    public Boolean isCorrect() {
        return correct;
    }

    public Lexer.Token.TokenType getType() {
        return this.token.type;
    }

    @Override
    public String toString() {
        return token.toString();
    }

    public TerminalSymbol(Lexer.Token token) {
        this.token = token;
        this.correct = true;
    }
}
