package frontend.visit;

import frontend.Lexer;

import java.util.ArrayList;

public class OneDimensionArraySymbol extends Symbol {
    private final boolean isConst;
    private final int dim1;	// 一维数组的大小
    private ArrayList<Integer> values;

    public OneDimensionArraySymbol(SymbolTable table, Lexer.Token token, boolean isConst, int dim1) {
        super(table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
    }

    public OneDimensionArraySymbol(SymbolTable table, Lexer.Token token, boolean isConst, int dim1, ArrayList<Integer> values) {
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
