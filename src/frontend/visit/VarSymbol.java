package frontend.visit;

import frontend.Lexer;

public class VarSymbol extends Symbol {
    private final boolean isConst;
    private int value;

    public VarSymbol(SymbolTable table, Lexer.Token token, boolean isConst) {
        super(table, token);
        this.isConst = isConst;
    }

    public VarSymbol(SymbolTable table, Lexer.Token token, boolean isConst, int value) {
        super(table, token);
        this.isConst = isConst;
        this.value = value;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getValue() {
        return value;
    }
}
