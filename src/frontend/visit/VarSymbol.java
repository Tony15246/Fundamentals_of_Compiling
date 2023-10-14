package frontend.visit;

import frontend.Lexer;

public class VarSymbol extends Symbol {
    private final boolean isConst;

    public VarSymbol(SymbolTable table, Lexer.Token token, boolean isConst) {
        super(table, token);
        this.isConst = isConst;
    }

    public boolean isConst() {
        return isConst;
    }
}
