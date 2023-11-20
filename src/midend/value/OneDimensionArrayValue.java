package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class OneDimensionArrayValue extends SymbolValue{
    private final boolean isConst;
    private final Integer dim1;	// 一维数组的大小
    private ArrayList<Value> values;
    private final Value pointer;

    public OneDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, Integer dim1, Value pointer) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.pointer = pointer;
        ((TempPointerValue)pointer).setValue(this);
        this.values = null;
        if (dim1 != null) {
            setType("[" + this.dim1 + " x i32]");
        } else {
            setType("i32*");
        }
    }

    public OneDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, Integer dim1) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.values = null;
        this.pointer = new GlobalVarPointerValue(token.value, this);
        if (dim1 != null) {
            setType("[" + this.dim1 + " x i32]");
        } else {
            setType("i32*");
        }
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDim1() {
        return dim1;
    }

    public Value getPointer() {
        return pointer;
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

    public ArrayList<Value> getValues() {
        return values;
    }
}
