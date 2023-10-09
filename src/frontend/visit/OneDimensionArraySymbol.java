package frontend.visit;

public class OneDimensionArraySymbol extends Symbol {
    private final boolean isConst;
    private final int dim1;	// 一维数组的大小

    public OneDimensionArraySymbol(int id, SymbolTable table, String token, boolean isConst, int dim1) {
        super(id, table, token);
        this.isConst = isConst;
        this.dim1 = dim1;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDim1() {
        return dim1;
    }
}
