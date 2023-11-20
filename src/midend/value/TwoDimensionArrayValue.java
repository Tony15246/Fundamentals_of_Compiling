package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class TwoDimensionArrayValue extends SymbolValue {
    private final boolean isConst;
    private final Integer dim1;    // 一维数组的大小
    private final Integer dim2;    // 二维数组的大小
    private ArrayList<Value> values;
    private final Value pointer;

    public TwoDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, Integer dim1, Integer dim2, Value pointer) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.dim2 = dim2;
        this.pointer = pointer;
        ((TempPointerValue)pointer).setValue(this);
        this.values = null;
        if (dim1 != null) {
            setType("[" + this.dim1 + " x [" + this.dim2 + " x i32]]");
        } else {
            setType("[" + this.dim2 + " x i32]*");
        }
    }

    public TwoDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, Integer dim1, Integer dim2) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.dim2 = dim2;
        this.pointer = new GlobalVarPointerValue(token.value, this);
        this.values = null;
        if (dim1 != null) {
            setType("[" + this.dim1 + " x [" + this.dim2 + " x i32]]");
        } else {
            setType("[" + this.dim2 + " x i32]*");
        }
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDim1() {
        return dim1;
    }

    public int getDim2() {
        return dim2;
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
