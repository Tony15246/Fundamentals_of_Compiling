package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

public class SymbolValue extends Value{
    private final SymbolTable table;     // 当前单词所在的符号表。
    private final Lexer.Token token; 	// 当前单词所对应的字符串。
    private int varNumber = -1;            // 当前局部变量的标识符编号。

    public int getVarNumber() {
        return varNumber;
    }

    public void setVarNumber(int varNumber) {
        this.varNumber = varNumber;
    }

    public SymbolTable getTable() {
        return table;
    }

    public Lexer.Token getToken() {
        return token;
    }

    public SymbolValue(SymbolTable table, Lexer.Token token) {
        this.table = table;
        this.token = token;
    }
}
