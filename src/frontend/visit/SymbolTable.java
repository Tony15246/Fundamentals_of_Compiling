package frontend.visit;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private SymbolTable pre;	// 父符号表
    private ArrayList<SymbolTable> next = new ArrayList<>();	// 子符号表
    private HashMap<String, Symbol> symbolMap = new HashMap<>();	// 符号表中的符号

    public SymbolTable getPre() {
        return pre;
    }

    public void setPre(SymbolTable pre) {
        this.pre = pre;
    }

    public ArrayList<SymbolTable> getNext() {
        return next;
    }

    public HashMap<String, Symbol> getSymbolMap() {
        return symbolMap;
    }

    public void addNext(SymbolTable next) {
        this.next.add(next);
        next.setPre(this);
    }

    public void addSymbol(Symbol symbol) {
        this.symbolMap.put(symbol.getToken(), symbol);
    }

    public Symbol getSymbol(String token) {
        SymbolTable temp = this;
        do {
            if (temp.symbolMap.containsKey(token)) {
                return temp.symbolMap.get(token);
            }
            temp = temp.getPre();
        } while (temp != null);

        return null;
    }
}
