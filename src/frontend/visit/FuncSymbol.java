package frontend.visit;

import java.util.ArrayList;

public class FuncSymbol extends Symbol{
    private final int retype;	// 返回值类型 0 -> void, 1 -> int
    private ArrayList<Symbol> paramList;	// 参数列表

    public FuncSymbol(int id, SymbolTable table, String token, int retype) {
        super(id, table, token);
        this.retype = retype;
        this.paramList = new ArrayList<>();
    }

    public int getRetype() {
        return retype;
    }

    public void addParam(Symbol param) {
        paramList.add(param);
    }

    public ArrayList<Symbol> getParamList() {
        return paramList;
    }
}
