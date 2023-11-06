package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class OneDimensionArrayValue extends SymbolValue{
    private final boolean isConst;
    private final int dim1;	// 一维数组的大小
    private ArrayList<Integer> values;

    public OneDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, int dim1) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
    }

    public OneDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, int dim1, ArrayList<Integer> values) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.values = values;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDim1() {
        return dim1;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }
}
