package frontend.visit;

public abstract class Symbol {
    private final int id;		// 当前单词对应的poi。
    private final SymbolTable table;     // 当前单词所在的符号表。
    private final String token; 	// 当前单词所对应的字符串。

    public int getId() {
        return id;
    }

    public SymbolTable getTable() {
        return table;
    }

    public String getToken() {
        return token;
    }

    public Symbol(int id, SymbolTable table, String token) {
        this.id = id;
        this.table = table;
        this.token = token;
    }
}
