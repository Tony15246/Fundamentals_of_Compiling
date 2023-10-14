package frontend.visit;

import frontend.Lexer;

import java.util.ArrayList;

public class FuncSymbol extends Symbol{
    private final int retype;	// 返回值类型 0 -> void, 1 -> int
    private ArrayList<Symbol> paramList;	// 参数列表

    public FuncSymbol(SymbolTable table, Lexer.Token token, int retype) {
        super(table, token);
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
