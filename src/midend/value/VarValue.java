package midend.value;

import frontend.Lexer;
import midend.SymbolTable;

public class VarValue extends SymbolValue{
    private final boolean isConst;
    private TempValue tempVar;//存储变量在当前作用域值的局部变量，即load命令的结果
    private Value pointer;//存储为局部变量开辟的空间的指针，即alloc命令的结果，store命令的第二个参数
    private Value value;//存储变量的值，即store命令的第一个参数

    public VarValue(SymbolTable table, Lexer.Token token, boolean isConst, Value pointer) {
        super(table, token);
        this.isConst = isConst;
        this.tempVar = null;
        this.pointer = pointer;
        if (pointer != null) {
            pointer.setValue(this);
        }
        this.value = null;
        setType("i32");
    }

    public VarValue(SymbolTable table, Lexer.Token token, boolean isConst) {
        super(table, token);
        this.isConst = isConst;
        this.tempVar = null;
        this.pointer = new GlobalVarPointerValue(token.value, this);
        this.value = null;
        setType("i32");
    }

    public boolean isConst() {
        return isConst;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getPointer() {
        return pointer;
    }

    public void setPointer(Value pointer) {
        this.pointer = pointer;
        pointer.setValue(this);
    }

    public Value getTempVar() {
        return tempVar;
    }

    public void setTempVar(TempValue tempVar) {
        this.tempVar = tempVar;
        tempVar.setValue(this);
    }

    @Override
    public String toString() {
        return tempVar.toString();
    }
}
