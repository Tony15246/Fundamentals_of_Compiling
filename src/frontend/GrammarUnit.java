package frontend;

import java.util.ArrayList;

public class GrammarUnit implements Node {

    private ArrayList<Node> children;
    private Node parent;
    private GrammarUnitType type;
    private Boolean correct;
    private ArrayList<Lexer.Token> tokens;

    @Override
    public void addChild(Node child) {
        this.children.add(child);
        child.setParent(this);
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public ArrayList<Node> getChildren() {
        return this.children;
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    @Override
    public Boolean isCorrect() {
        return this.correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public GrammarUnitType getType() {
        return this.type;
    }

    public ArrayList<Lexer.Token> getTokens() {
        return tokens;
    }

    public void addToken(Lexer.Token token) {
        this.tokens.add(token);
    }

    @Override
    public String toString() {
        return '<' + this.type.name() + '>';
    }

    public GrammarUnit(GrammarUnitType type) {
        this.children = new ArrayList<>();
        this.parent = null;
        this.type = type;
        this.correct = true;
        this.tokens = new ArrayList<>();
    }

    public enum GrammarUnitType {
        CompUnit,
        ConstDecl,
        VarDecl,
        ConstDef,
        ConstInitVal,
        VarDef,
        InitVal,
        FuncDef,
        MainFuncDef,
        FuncType,
        FuncFParams,
        FuncFParam,
        Block,
        Stmt,
        ForStmt,
        Exp,
        Cond,
        LVal,
        PrimaryExp,
        Number,
        UnaryExp,
        UnaryOp,
        FuncRParams,
        MulExp,
        AddExp,
        RelExp,
        EqExp,
        LAndExp,
        LOrExp,
        ConstExp,
        BlockItem,
        BType,
        Decl,
    }
}
