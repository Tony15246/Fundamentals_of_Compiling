package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

import java.util.ArrayList;

public class FuncValue extends SymbolValue {
    private final int retype;	// 返回值类型 0 -> void, 1 -> int
    private ArrayList<Value> params;	// 参数列表

    public FuncValue(SymbolTable table, Lexer.Token token, int retype) {
        super(table, token);
        this.retype = retype;
        this.params = new ArrayList<>();
    }

    public int getRetype() {
        return retype;
    }

    public void addParam(SymbolValue param) {
        params.add(param);
    }

    public void addParam(ArrayList<Value> paramList) {
        this.params.addAll(paramList);
    }

    public ArrayList<Value> getParams() {
        return params;
    }

    public String getParamsString() {
        StringBuilder sb = new StringBuilder();
        for (Value param : params) {
            sb.append(param.toString()).append(", ");
        }
        if (!sb.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }
}
