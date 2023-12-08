package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class TwoDimensionArrayValue extends SymbolValue {
    private final boolean isConst;
    private final Integer dim1;    // 一维数组的大小
    private final Integer dim2;    // 二维数组的大小
    private ArrayList<Value> values;
    private Value pointer;

    public TwoDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, Integer dim1, Integer dim2, Value pointer) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.dim2 = dim2;
        this.pointer = pointer;
        if (pointer != null) {
            pointer.setValue(this);
        }
        this.values = null;
        if (dim1 != null) {
            setType("[" + this.dim1 + " x [" + this.dim2 + " x i32]]");
        } else {
            setType("[" + this.dim2 + " x i32]");
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
            setType("[" + this.dim2 + " x i32]");
        }
    }

    public boolean isConst() {
        return isConst;
    }

    public Integer getDim1() {
        return dim1;
    }

    public Integer getDim2() {
        return dim2;
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
        sb.append(" x [");
        sb.append(dim2);
        sb.append(" x i32]] ");
        if (values == null) {
            sb.append("zeroinitializer");
            return sb.toString();
        }
        sb.append("[");
        for (int i = 0; i < dim1; i++) {
            sb.append("[");
            sb.append(dim2);
            sb.append(" x i32] ");
            if (values.size() / dim1 + 1 < i) {
                sb.append("zeroinitializer");
                continue;
            }
            sb.append("[");
            for (int j = 0; j < dim2; j++) {
                if (j != 0) {
                    sb.append(", ");
                }
                if (i * dim2 + j < values.size()) {
                    sb.append("i32 ");
                    sb.append(values.get(i * dim2 + j).toString());
                } else {
                    sb.append("i32 0");
                }
            }
            sb.append("]");
            if (i != dim1 - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
