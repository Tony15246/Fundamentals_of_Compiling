package frontend.visit;

import frontend.Lexer;

public abstract class Symbol {
    private int id;		// 当前单词对应的poi。
    private final SymbolTable table;     // 当前单词所在的符号表。
    private final Lexer.Token token; 	// 当前单词所对应的字符串。

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SymbolTable getTable() {
        return table;
    }

    public Lexer.Token getToken() {
        return token;
    }

    public Symbol(SymbolTable table, Lexer.Token token) {
        this.table = table;
        this.token = token;
    }
}
