package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

public class VarValue extends SymbolValue{
    private final boolean isConst;
    private Value value;
    private Value pointer;

    public VarValue(SymbolTable table, Lexer.Token token, boolean isConst, Value pointer) {
        super(table, token);
        this.isConst = isConst;
        this.value = null;
        this.pointer = pointer;
    }

    public VarValue(SymbolTable table, Lexer.Token token, boolean isConst) {
        super(table, token);
        this.isConst = isConst;
        this.value = null;
        this.pointer = new GlobalVarPointerValue(token.value);
    }

    public boolean isConst() {
        return isConst;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getPointer() {
        return pointer;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
