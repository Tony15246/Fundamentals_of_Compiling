package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class OneDimensionArrayValue extends SymbolValue{
    private final boolean isConst;
    private final Integer dim1;	// 一维数组的大小
    private ArrayList<Value> values;
    private Value pointer;

    public OneDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, Integer dim1, Value pointer) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.pointer = pointer;
        if (pointer != null) {
            pointer.setValue(this);
        }
        this.values = null;
        if (dim1 != null) {
            setType("[" + this.dim1 + " x i32]");
        } else {
            setType("i32");
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
            setType("i32");
        }
    }

    public boolean isConst() {
        return isConst;
    }

    public Integer getDim1() {
        return dim1;
    }

    public Value getPointer() {
        return pointer;
    }

    public void setPointer(Value pointer) {
        this.pointer = pointer;
        pointer.setValue(this);
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public String getValuesString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(dim1);
        sb.append(" x i32] ");
        if (values == null) {
            sb.append("zeroinitializer");
            return sb.toString();
        }
        sb.append("[");
        for (int i = 0; i < values.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            if (i < dim1) {
                sb.append("i32 ");
                sb.append(values.get(i).toString());
            } else {
                sb.append("i32 0");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
