package frontend.visit;

public class TwoDimensionArraySymbol extends Symbol {
    private final boolean isConst;
    private final int dim1;	// 一维数组的大小
    private final int dim2;	// 二维数组的大小

    public TwoDimensionArraySymbol(int id, SymbolTable table, String token, boolean isConst, int dim1, int dim2) {
        super(id, table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
        this.dim2 = dim2;
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
}
