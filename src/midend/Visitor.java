package midend;

import frontend.GrammarUnit;
import frontend.Node;
import frontend.TerminalSymbol;
import midend.value.*;
import midend.value.user.*;

import java.util.ArrayList;

public class Visitor {
    private User rootUser;
    private User currentUser;
    private int tempCount = 0;
    private final Node rootNode;
    private SymbolTable rootTable;
    private SymbolTable currentTable;

    public Visitor(Node rootNode) {
        this.rootNode = rootNode;
    }

    public boolean isGlobal() {
        return currentTable == rootTable;
    }

    public void visit() {
        rootTable = new SymbolTable();
        currentTable = rootTable;
        rootUser = new User();
        currentUser = rootUser;
        ArrayList<Node> children = rootNode.getChildren();
        for (Node child : children) {
            GrammarUnit unit = (GrammarUnit) child;
            switch (unit.getType()) {
                case Decl:
                    Decl(child);
                    break;
                case FuncDef:
                    FuncDef(child);
                    break;
                case MainFuncDef:
                    MainFuncDef(child);
                    break;
            }
        }
    }

    private void Decl(Node decl) {
        ArrayList<Node> children = decl.getChildren();
        for (Node child : children) {
            GrammarUnit unit = (GrammarUnit) child;
            switch (unit.getType()) {
                case ConstDecl:
                    ConstDecl(child);
                    break;
                case VarDecl:
                    VarDecl(child);
                    break;
            }
        }
    }

    private void ConstDecl(Node constDecl) {
        ArrayList<Node> children = constDecl.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.ConstDef) {
                ConstDef(child);
            }
        }
    }


    private void ConstDef(Node constDef) {
        ArrayList<Node> children = constDef.getChildren();
        int dim = 0;
        TerminalSymbol ident;
        VarValue varValue;
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.ConstExp) {
                dim++;
            }
        }
        switch (dim) {
            case 0:
                if (isGlobal()) {
                    ident = (TerminalSymbol) children.get(0);
                    varValue = new VarValue(currentTable, ident.getToken(), true);
                    varValue.setValue(ConstInitVal(children.get(2)));
                    currentTable.addSymbol(varValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(varValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    ident = (TerminalSymbol) children.get(0);
                    TempValue pointer = new TempValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    varValue = new VarValue(currentTable, ident.getToken(), true, pointer);
                    Value srcValue = ConstInitVal(children.get(2));
                    varValue.setValue(srcValue);
                    currentTable.addSymbol(varValue);
                    StoreUser storeUser = new StoreUser(srcValue, pointer);
                    currentUser.addUser(storeUser);
                }
                break;
            case 1, 2:
                throw new RuntimeException("no support for array yet");
        }
    }

    private Value ConstInitVal(Node constInitVal) {
        ArrayList<Node> children = constInitVal.getChildren();
        if (children.size() == 1) {
            return calNode(children.get(0));
        } else {
            throw new RuntimeException("no support for array yet");
        }
    }

    private void VarDecl(Node varDecl) {
        ArrayList<Node> children = varDecl.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.VarDef) {
                VarDef(child);
            }
        }
    }

    private void VarDef(Node varDef) {
        ArrayList<Node> children = varDef.getChildren();
        int dim = 0;
        TerminalSymbol ident;
        VarValue varValue;
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.ConstExp) {
                dim++;
            }
        }
        switch (dim) {
            case 0:
                if (isGlobal()) {
                    ident = (TerminalSymbol) children.get(0);
                    varValue = new VarValue(currentTable, ident.getToken(), false);
                    if (children.size() == 3) {
                        varValue.setValue(InitVal(children.get(2)));
                    } else {
                        varValue.setValue(new IntConstValue(0));
                    }
                    currentTable.addSymbol(varValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(varValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    ident = (TerminalSymbol) children.get(0);
                    TempValue pointer = new TempValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    varValue = new VarValue(currentTable, ident.getToken(), false, pointer);
                    if (children.size() == 3) {
                        Value srcValue = InitVal(children.get(2));
                        varValue.setValue(srcValue);
                        StoreUser storeUser = new StoreUser(srcValue, pointer);
                        currentUser.addUser(storeUser);
                    }
                    currentTable.addSymbol(varValue);
                }
                break;
            case 1, 2:
                throw new RuntimeException("no support for array yet");
        }
    }

    private Value InitVal(Node initVal) {
        ArrayList<Node> children = initVal.getChildren();
        if (children.size() == 1) {
            if (isGlobal()) {
                return calNode(children.get(0));
            } else {
                return Exp(children.get(0));
            }
        } else {
            throw new RuntimeException("no support for array yet");
        }
    }

    private void FuncDef(Node funcDef) {
        ArrayList<Node> children = funcDef.getChildren();
        int retype = FuncType(children.get(0));
        TerminalSymbol ident = (TerminalSymbol) children.get(1);
        FuncValue funcValue = new FuncValue(currentTable, ident.getToken(), retype);
        currentTable.addSymbol(funcValue);
        SymbolTable temp = new SymbolTable();
        temp.setFuncSymbol(funcValue);
        currentTable.addNext(temp);
        currentTable = temp;
        FuncDefUser funcDefUser = new FuncDefUser(funcValue);
        currentUser.addUser(funcDefUser);
        currentUser = funcDefUser;
        int count = tempCount;
        tempCount = 0;
        if (children.get(3) instanceof GrammarUnit) {
            funcValue.addParam(FuncFParams(children.get(3)));
            tempCount++;
            Block(children.get(5));
        } else {
            tempCount++;
            Block(children.get(4));
        }
        if (retype == 0) {
            RetUser returnUser = new RetUser();
            currentUser.addUser(returnUser);
        }
        tempCount = count;
        currentUser = rootUser;
    }

    private void MainFuncDef(Node mainFuncDef) {
        ArrayList<Node> children = mainFuncDef.getChildren();
        TerminalSymbol maintk = (TerminalSymbol) children.get(1);
        FuncValue funcValue = new FuncValue(currentTable, maintk.getToken(), 1);
        currentTable.addSymbol(funcValue);
        SymbolTable temp = new SymbolTable();
        temp.setFuncSymbol(funcValue);
        currentTable.addNext(temp);
        currentTable = temp;
        FuncDefUser funcDefUser = new FuncDefUser(funcValue);
        currentUser.addUser(funcDefUser);
        currentUser = funcDefUser;
        int count = tempCount;
        tempCount = 0;
        Block(children.get(4));
        tempCount = count;
        currentUser = rootUser;
    }

    private int FuncType(Node funcType) {
        int retype;
        TerminalSymbol type = (TerminalSymbol) funcType.getChildren().get(0);
        retype = switch (type.getToken().value) {
            case "void" -> 0;
            case "int" -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + type.getToken().value);
        };
        return retype;
    }

    private ArrayList<Value> FuncFParams(Node funcFParams) {
        ArrayList<Value> params = new ArrayList<>();
        ArrayList<Node> children = funcFParams.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.FuncFParam) {
                params.add(FuncFParam(child));
            }
        }
        return params;
    }

    private Value FuncFParam(Node funcFParam) {
        ArrayList<Node> children = funcFParam.getChildren();
        TerminalSymbol ident = (TerminalSymbol) children.get(1);
        VarValue param;
        switch (children.size()) {
            case 2:
                TempValue paramValue = new TempValue(tempCount++);
                param = new VarValue(currentTable, ident.getToken(), false, null);
                param.setTempVar(paramValue);
                break;
            default:
                throw new RuntimeException("no support for array yet");
        }
        return param;
    }

    private void Block(Node block) {
        ArrayList<Node> children = block.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit) {
                BlockItem(child);
            }
        }
    }

    private void BlockItem(Node blockItem) {
        ArrayList<Node> children = blockItem.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case Decl:
                    Decl(children.get(0));
                    break;
                case Stmt:
                    Stmt(children.get(0));
                    break;
            }
        }
    }

    private void Stmt(Node stmt) {
        ArrayList<Node> children = stmt.getChildren();
        if (children.get(0) instanceof TerminalSymbol terminalSymbol) {
            switch (terminalSymbol.getType()) {
                case PRINTFTK:
                    String format = ((TerminalSymbol) children.get(2)).getToken().value.replace("\"", "");
                    ArrayList<Value> args = new ArrayList<>();
                    for (Node child : children) {
                        if (child instanceof GrammarUnit exp
                                && exp.getType() == GrammarUnit.GrammarUnitType.Exp) {
                            args.add(Exp(exp));
                        }
                    }
                    PrintfUser printfUser = new PrintfUser(format);
                    printfUser.addArgs(args);
                    currentUser.addUser(printfUser);
                    break;
                case RETURNTK:
                    if (children.size() == 3){
                        RetUser returnUser = new RetUser(Exp(children.get(1)));
                        currentUser.addUser(returnUser);
                    }
                    break;
                case IFTK, FORTK, BREAKTK, CONTINUETK:
                    throw new RuntimeException("no support for if, for, break, continue yet");
            }
        } else if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case LVal:
                    if (children.size() == 6) {
                        TempValue newValue = new TempValue(tempCount++);
                        GetintUser getintUser = new GetintUser(newValue);
                        currentUser.addUser(getintUser);
                        StoreUser storeUser = new StoreUser(newValue, LVal(children.get(0), true));
                        currentUser.addUser(storeUser);
                    } else if (children.size() == 4) {
                        StoreUser storeUser = new StoreUser(Exp(children.get(2)), LVal(children.get(0), true));
                        currentUser.addUser(storeUser);
                    }
                    break;
                case Exp:
                    Exp(children.get(0));
                    break;
                case Block:
                    Block(children.get(0));
                    break;
            }
        }
    }

    private Value Exp(Node exp) {
        ArrayList<Node> children = exp.getChildren();
//        return AddExp(children.get(0));
        return null;
    }

    private Value LVal(Node lVal, boolean isLVal) {
        ArrayList<Node> children = lVal.getChildren();
        TerminalSymbol ident = (TerminalSymbol) children.get(0);
        switch (children.size()) {
            case 1:
                VarValue varValue = (VarValue) currentTable.getSymbol(ident.getToken().value);
                if (isLVal) {
                    if (varValue.getPointer() == null) {
                        TempValue pointer = new TempValue(tempCount++);
                        AllocUser allocUser = new AllocUser(pointer);
                        currentUser.addUser(allocUser);
                        varValue.setPointer(pointer);
                        varValue.setTempVar(null);
                    }
                    return varValue.getPointer();
                } else {
                    if (varValue.getTempVar() == null) {
                        TempValue tempVar = new TempValue(tempCount++);
                        LoadUser loadUser = new LoadUser(varValue.getPointer(), tempVar);
                        currentUser.addUser(loadUser);
                        varValue.setTempVar(tempVar);
                    }
                    return varValue.getTempVar();
                }
            case 4, 7:
                throw new RuntimeException("no support for array yet");
        }
        throw new RuntimeException("unexpected form of LVal");
    }

    private Value PrimaryExp(Node primaryExp) {
        ArrayList<Node> children = primaryExp.getChildren();
        //todo
        return null;
    }

    private Value calNode(Node node) {
        if (node instanceof TerminalSymbol terminalSymbol) {
            switch (terminalSymbol.getType()) {
                case INTCON:
                    return new IntConstValue(Integer.parseInt(terminalSymbol.getToken().value));
                case IDENFR:
                    Value value = currentTable.getSymbol(terminalSymbol.getToken().value);
                    if (value instanceof VarValue varValue) {
                        if (varValue.getValue() != null) {
                            return varValue.getValue();
                        } else {
                            throw new RuntimeException("not a const when calNode idenfr");
                        }
                    } else {
                        throw new RuntimeException("not a var when calNode idenfr");
                    }
            }
        } else if (node instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case ConstExp, Exp, Number:
                    return calNode(unit.getChildren().get(0));
                case UnaryExp:
                    if (unit.getChildren().size() == 1) {
                        return calNode(unit.getChildren().get(0));
                    } else if (unit.getChildren().size() == 2) {
                        String op = ((TerminalSymbol) unit.getChildren().get(0)).getToken().value;
                        IntConstValue value = (IntConstValue) calNode(unit.getChildren().get(1));
                        assert value != null;
                        if (op.equals("-")) {
                            return new IntConstValue(-value.getValue());
                        } else if (op.equals("+")) {
                            return new IntConstValue(value.getValue());
                        } else {
                            throw new RuntimeException("unknown unary op");
                        }
                    } else {
                        throw new RuntimeException("no support for function call yet");
                    }
                case MulExp:
                    if (unit.getChildren().size() == 1) {
                        return calNode(unit.getChildren().get(0));
                    } else if (unit.getChildren().size() == 3) {
                        String op = ((TerminalSymbol) unit.getChildren().get(1)).getToken().value;
                        IntConstValue left = (IntConstValue) calNode(unit.getChildren().get(0));
                        IntConstValue right = (IntConstValue) calNode(unit.getChildren().get(2));
                        assert left != null;
                        assert right != null;
                        if (op.equals("*")) {
                            return new IntConstValue(left.getValue() * right.getValue());
                        } else if (op.equals("/")) {
                            return new IntConstValue(left.getValue() / right.getValue());
                        } else if (op.equals("%")) {
                            return new IntConstValue(left.getValue() % right.getValue());
                        } else {
                            throw new RuntimeException("unknown mul op");
                        }
                    }
                case AddExp:
                    if (unit.getChildren().size() == 1) {
                        return calNode(unit.getChildren().get(0));
                    } else if (unit.getChildren().size() == 3) {
                        String op = ((TerminalSymbol) unit.getChildren().get(1)).getToken().value;
                        IntConstValue left = (IntConstValue) calNode(unit.getChildren().get(0));
                        IntConstValue right = (IntConstValue) calNode(unit.getChildren().get(2));
                        assert left != null;
                        assert right != null;
                        if (op.equals("+")) {
                            return new IntConstValue(left.getValue() + right.getValue());
                        } else if (op.equals("-")) {
                            return new IntConstValue(left.getValue() - right.getValue());
                        } else {
                            throw new RuntimeException("unknown add op");
                        }
                    }
                case LVal:
                    if (unit.getChildren().size() == 1) {
                        return calNode(unit.getChildren().get(0));
                    } else {
                        throw new RuntimeException("no support for array yet");
                    }
                case PrimaryExp:
                    if (unit.getChildren().size() == 1) {
                        return calNode(unit.getChildren().get(0));
                    } else if (unit.getChildren().size() == 3) {
                        return calNode(unit.getChildren().get(1));
                    }
            }
        }
        throw new RuntimeException("unknown node type");
    }
}
