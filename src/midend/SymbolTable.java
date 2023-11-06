package midend;

import midend.value.FuncValue;
import midend.value.SymbolValue;
import midend.value.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private SymbolTable pre;	// 父符号表
    private ArrayList<SymbolTable> next = new ArrayList<>();	// 子符号表
    private HashMap<String, SymbolValue> symbolMap = new HashMap<>();	// 符号表中的符号

    public SymbolTable getPre() {
        return pre;
    }
    private FuncValue funcSymbol;

    public void setPre(SymbolTable pre) {
        this.pre = pre;
    }

    public ArrayList<SymbolTable> getNext() {
        return next;
    }

    public HashMap<String, SymbolValue> getSymbolMap() {
        return symbolMap;
    }

    public void addNext(SymbolTable next) {
        this.next.add(next);
        next.setPre(this);
    }

    public void addSymbol(SymbolValue symbol) {
        this.symbolMap.put(symbol.getToken().value, symbol);
    }

    public Value getSymbol(String token) {
        SymbolTable temp = this;
        do {
            if (temp.symbolMap.containsKey(token)) {
                return temp.symbolMap.get(token);
            }
            temp = temp.getPre();
        } while (temp != null);

        return null;
    }

    public Value getSymbolInCurrentTable(String token) {
        if (this.symbolMap.containsKey(token)) {
            return this.symbolMap.get(token);
        }
        return null;
    }

    public FuncValue getFuncSymbol() {
        return funcSymbol;
    }

    public void setFuncSymbol(FuncValue funcSymbol) {
        this.funcSymbol = funcSymbol;
    }
}
