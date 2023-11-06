package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

public class VarValue extends SymbolValue{
    private final boolean isConst;
    private int value;

    public VarValue(SymbolTable table, Lexer.Token token, boolean isConst) {
        super(table, token);
        this.isConst = isConst;
    }

    public VarValue(SymbolTable table, Lexer.Token token, boolean isConst, int value) {
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
