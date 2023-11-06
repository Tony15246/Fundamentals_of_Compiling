package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class TwoDimensionArrayValue extends SymbolValue{
    private final boolean isConst;
    private final int dim1;	// 一维数组的大小
    private final int dim2;	// 二维数组的大小
    private ArrayList<Integer> values;

    public TwoDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, int dim1, int dim2) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.dim2 = dim2;
    }

    public TwoDimensionArrayValue(SymbolTable table, Lexer.Token token, boolean isConst, int dim1, int dim2, ArrayList<Integer> values) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.dim2 = dim2;
        this.values = values;
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

    public ArrayList<Integer> getValues() {
        return values;
    }
}
