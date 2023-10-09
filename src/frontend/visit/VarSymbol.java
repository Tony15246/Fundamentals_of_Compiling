package frontend.visit;

public class VarSymbol extends Symbol {
    private final boolean isConst;

    public VarSymbol(int id, SymbolTable table, String token, boolean isConst) {
        super(id, table, token);
        this.isConst = isConst;
    }

    public boolean isConst() {
        return isConst;
    }
}
