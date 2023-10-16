package frontend.visit;

import frontend.Lexer;

import java.util.ArrayList;

public class FuncSymbol extends Symbol{
    private final int retype;	// 返回值类型 0 -> void, 1 -> int (为2时表示是main函数)
    private ArrayList<Symbol> params;	// 参数列表

    public FuncSymbol(SymbolTable table, Lexer.Token token, int retype) {
        super(table, token);
        this.retype = retype;
        this.params = new ArrayList<>();
    }

    public int getRetype() {
        return retype;
    }

    public void addParam(Symbol param) {
        params.add(param);
    }

    public void addParam(ArrayList<Symbol> paramList) {
        this.params.addAll(paramList);
    }

    public ArrayList<Symbol> getParams() {
        return params;
    }
}
