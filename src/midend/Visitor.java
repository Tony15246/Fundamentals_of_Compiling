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

    public User getRootUser() {
        visit();
        return rootUser;
    }

    public void visit() {
        rootTable = new SymbolTable();
        currentTable = rootTable;
        rootUser = new User();
        rootUser.addUser(new User("declare i32 @getint()"));
        rootUser.addUser(new User("declare void @putint(i32)"));
        rootUser.addUser(new User("declare void @putch(i32)"));
        rootUser.addUser(new User("declare void @putstr(i8*)"));
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
        TerminalSymbol ident = (TerminalSymbol) children.get(0);
        int dim1;
        int dim2;
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
                    varValue = new VarValue(currentTable, ident.getToken(), true);
                    varValue.setValue(ConstInitVal(children.get(2)).get(0));
                    currentTable.addSymbol(varValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(varValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    TempValue pointer = new TempPointerValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    varValue = new VarValue(currentTable, ident.getToken(), true, pointer);
                    Value srcValue = ConstInitVal(children.get(2)).get(0);
                    varValue.setValue(srcValue);
                    currentTable.addSymbol(varValue);
                    StoreUser storeUser = new StoreUser(srcValue, pointer);
                    currentUser.addUser(storeUser);
                }
                break;
            case 1:
                dim1 = calNode(children.get(2)).getNumber();
                if (isGlobal()) {
                    OneDimensionArrayValue oneDimensionArrayValue = new OneDimensionArrayValue(currentTable, ident.getToken(), true, dim1);
                    oneDimensionArrayValue.setValues(ConstInitVal(children.get(5)));
                    currentTable.addSymbol(oneDimensionArrayValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(oneDimensionArrayValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    TempValue pointer = new TempPointerValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    OneDimensionArrayValue oneDimensionArrayValue = new OneDimensionArrayValue(currentTable, ident.getToken(), true, dim1, pointer);
                    currentTable.addSymbol(oneDimensionArrayValue);
                    ArrayList<Value> srcValues = ConstInitVal(children.get(5));
                    oneDimensionArrayValue.setValues(srcValues);
                    for (int i = 0; i < dim1; i++) {
                        TempPointerValue elementPointer = new TempPointerValue(tempCount++);
                        elementPointer.setType("i32*");
                        ArrayList<Value> index = new ArrayList<>();
                        index.add(new IntConstValue(0));
                        index.add(new IntConstValue(i));
                        GetelementptrUser getelementptrUser = new GetelementptrUser(elementPointer, pointer, index);
                        currentUser.addUser(getelementptrUser);
                        StoreUser storeUser = new StoreUser(srcValues.get(i), elementPointer);
                        currentUser.addUser(storeUser);
                    }
                }
                break;
            case 2:
                dim1 = calNode(children.get(2)).getNumber();
                dim2 = calNode(children.get(5)).getNumber();
                if (isGlobal()) {
                    TwoDimensionArrayValue twoDimensionArrayValue = new TwoDimensionArrayValue(currentTable, ident.getToken(), true, dim1, dim2);
                    twoDimensionArrayValue.setValues(ConstInitVal(children.get(8)));
                    currentTable.addSymbol(twoDimensionArrayValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(twoDimensionArrayValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    TempValue pointer = new TempPointerValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    TwoDimensionArrayValue twoDimensionArrayValue = new TwoDimensionArrayValue(currentTable, ident.getToken(), true, dim1, dim2, pointer);
                    currentTable.addSymbol(twoDimensionArrayValue);
                    ArrayList<Value> srcValues = ConstInitVal(children.get(8));
                    twoDimensionArrayValue.setValues(srcValues);
                    for (int i = 0; i < dim1; i++) {
                        for (int j = 0; j < dim2; j++) {
                            TempPointerValue elementPointer = new TempPointerValue(tempCount++);
                            elementPointer.setType("i32*");
                            ArrayList<Value> index = new ArrayList<>();
                            index.add(new IntConstValue(0));
                            index.add(new IntConstValue(i));
                            index.add(new IntConstValue(j));
                            GetelementptrUser getelementptrUser = new GetelementptrUser(elementPointer, pointer, index);
                            currentUser.addUser(getelementptrUser);
                            StoreUser storeUser = new StoreUser(srcValues.get(i * dim2 + j), elementPointer);
                            currentUser.addUser(storeUser);
                        }
                    }
                }
                break;
        }
    }

    private ArrayList<Value> ConstInitVal(Node constInitVal) {
        ArrayList<Value> initVals = new ArrayList<>();
        ArrayList<Node> children = constInitVal.getChildren();
        if (children.size() == 1) {
            initVals.add(calNode(children.get(0)));
        } else {
            for (Node child : children) {
                if (child instanceof GrammarUnit unit
                        && unit.getType() == GrammarUnit.GrammarUnitType.ConstInitVal) {
                    initVals.addAll(ConstInitVal(child));
                }
            }
        }
        return initVals;
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
        TerminalSymbol ident = (TerminalSymbol) children.get(0);
        int dim1;
        int dim2;
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
                    varValue = new VarValue(currentTable, ident.getToken(), false);
                    if (children.size() == 3) {
                        varValue.setValue(InitVal(children.get(2)).get(0));
                    } else {
                        varValue.setValue(new IntConstValue(0));
                    }
                    currentTable.addSymbol(varValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(varValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    TempValue pointer = new TempPointerValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    varValue = new VarValue(currentTable, ident.getToken(), false, pointer);
                    if (children.size() == 3) {
                        Value srcValue = InitVal(children.get(2)).get(0);
                        varValue.setValue(srcValue);
                        StoreUser storeUser = new StoreUser(srcValue, pointer);
                        currentUser.addUser(storeUser);
                    }
                    currentTable.addSymbol(varValue);
                }
                break;
            case 1:
                dim1 = calNode(children.get(2)).getNumber();
                if (isGlobal()) {
                    OneDimensionArrayValue oneDimensionArrayValue = new OneDimensionArrayValue(currentTable, ident.getToken(), false, dim1);
                    if (children.size() == 6) {
                        oneDimensionArrayValue.setValues(InitVal(children.get(5)));
                    }
                    currentTable.addSymbol(oneDimensionArrayValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(oneDimensionArrayValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    TempValue pointer = new TempPointerValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    OneDimensionArrayValue oneDimensionArrayValue = new OneDimensionArrayValue(currentTable, ident.getToken(), false, dim1, pointer);
                    currentTable.addSymbol(oneDimensionArrayValue);
                    if (children.size() == 6) {
                        ArrayList<Value> srcValues = InitVal(children.get(5));
                        oneDimensionArrayValue.setValues(srcValues);
                        for (int i = 0; i < dim1; i++) {
                            TempPointerValue elementPointer = new TempPointerValue(tempCount++);
                            elementPointer.setType("i32*");
                            ArrayList<Value> index = new ArrayList<>();
                            index.add(new IntConstValue(0));
                            index.add(new IntConstValue(i));
                            GetelementptrUser getelementptrUser = new GetelementptrUser(elementPointer, pointer, index);
                            currentUser.addUser(getelementptrUser);
                            StoreUser storeUser = new StoreUser(srcValues.get(i), elementPointer);
                            currentUser.addUser(storeUser);
                        }
                    }
                }
                break;
            case 2:
                dim1 = calNode(children.get(2)).getNumber();
                dim2 = calNode(children.get(5)).getNumber();
                if (isGlobal()) {
                    TwoDimensionArrayValue twoDimensionArrayValue = new TwoDimensionArrayValue(currentTable, ident.getToken(), false, dim1, dim2);
                    if (children.size() == 9) {
                        twoDimensionArrayValue.setValues(InitVal(children.get(8)));
                    }
                    currentTable.addSymbol(twoDimensionArrayValue);
                    GlobalVarDefUser globalVarDefUser = new GlobalVarDefUser(twoDimensionArrayValue);
                    currentUser.addUser(globalVarDefUser);
                } else {
                    TempValue pointer = new TempPointerValue(tempCount++);
                    AllocUser allocUser = new AllocUser(pointer);
                    currentUser.addUser(allocUser);
                    TwoDimensionArrayValue twoDimensionArrayValue = new TwoDimensionArrayValue(currentTable, ident.getToken(), false, dim1, dim2, pointer);
                    currentTable.addSymbol(twoDimensionArrayValue);
                    if (children.size() == 9) {
                        ArrayList<Value> srcValues = InitVal(children.get(8));
                        twoDimensionArrayValue.setValues(srcValues);
                        for (int i = 0; i < dim1; i++) {
                            for (int j = 0; j < dim2; j++) {
                                TempPointerValue elementPointer = new TempPointerValue(tempCount++);
                                elementPointer.setType("i32*");
                                ArrayList<Value> index = new ArrayList<>();
                                index.add(new IntConstValue(0));
                                index.add(new IntConstValue(i));
                                index.add(new IntConstValue(j));
                                GetelementptrUser getelementptrUser = new GetelementptrUser(elementPointer, pointer, index);
                                currentUser.addUser(getelementptrUser);
                                StoreUser storeUser = new StoreUser(srcValues.get(i * dim2 + j), elementPointer);
                                currentUser.addUser(storeUser);
                            }
                        }
                    }
                }
                break;
        }
    }

    private ArrayList<Value> InitVal(Node initVal) {
        ArrayList<Value> initVals = new ArrayList<>();
        ArrayList<Node> children = initVal.getChildren();
        if (children.size() == 1) {
            if (isGlobal()) {
                initVals.add(calNode(children.get(0)));
            } else {
                initVals.add(Exp(children.get(0)));
            }
        } else {
            for (Node child : children) {
                if (child instanceof GrammarUnit unit
                        && unit.getType() == GrammarUnit.GrammarUnitType.InitVal) {
                    initVals.addAll(InitVal(child));
                }
            }
        }
        return initVals;
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
        currentTable = currentTable.getPre();
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
        tempCount = 1;
        Block(children.get(4));
        tempCount = count;
        currentUser = rootUser;
        currentTable = currentTable.getPre();
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
        ArrayList<Value> paramsTempVar = new ArrayList<>();
        ArrayList<Node> children = funcFParams.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.FuncFParam) {
                Value param = FuncFParam(child);
                if (param instanceof VarValue varValue) {
                    params.add(varValue);
                    paramsTempVar.add(varValue.getTempVar());
                    currentTable.addSymbol(varValue);
                } else if (param instanceof OneDimensionArrayValue oneDimensionArrayValue) {
                    paramsTempVar.add(oneDimensionArrayValue.getPointer());
                    currentTable.addSymbol(oneDimensionArrayValue);
                } else if (param instanceof TwoDimensionArrayValue twoDimensionArrayValue) {
                    paramsTempVar.add(twoDimensionArrayValue.getPointer());
                    currentTable.addSymbol(twoDimensionArrayValue);
                }
            }
        }
        tempCount++;
        for (Value i : params) {
            if (i instanceof VarValue param) {
                TempValue pointer = new TempPointerValue(tempCount++);
                param.setPointer(pointer);
                AllocUser allocUser = new AllocUser(pointer);
                currentUser.addUser(allocUser);
                StoreUser storeUser = new StoreUser(param.getTempVar(), pointer);
                currentUser.addUser(storeUser);
            }
        }
        return paramsTempVar;
    }

    private Value FuncFParam(Node funcFParam) {
        ArrayList<Node> children = funcFParam.getChildren();
        TerminalSymbol ident = (TerminalSymbol) children.get(1);
        TempValue pointer;
        switch (children.size()) {
            case 2:
                TempValue paramValue = new TempValue(tempCount++);
                VarValue varValue = new VarValue(currentTable, ident.getToken(), false, null);
                varValue.setTempVar(paramValue);
                return varValue;
            case 4:
                pointer = new TempPointerValue(tempCount++);
                return new OneDimensionArrayValue(currentTable, ident.getToken(), false, null, pointer);
            case 7:
                int dim2 = calNode(children.get(5)).getNumber();
                pointer = new TempPointerValue(tempCount++);
                return new TwoDimensionArrayValue(currentTable, ident.getToken(), false, null, dim2, pointer);
            default:
                throw new RuntimeException("unexpected form of FuncFParam");
        }
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
                    if (children.size() == 3) {
                        RetUser returnUser = new RetUser(Exp(children.get(1)));
                        currentUser.addUser(returnUser);
                    } else if (children.size() == 2) {
                        RetUser returnUser = new RetUser();
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
                        newValue.setType("i32");
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
                    SymbolTable temp = new SymbolTable();
                    currentTable.addNext(temp);
                    currentTable = temp;
                    Block(children.get(0));
                    currentTable = currentTable.getPre();
                    break;
            }
        }
    }

    private Value Exp(Node exp) {
        ArrayList<Node> children = exp.getChildren();
        return AddExp(children.get(0));
    }

    private Value LVal(Node lVal, boolean isLVal) {
        ArrayList<Node> children = lVal.getChildren();
        TerminalSymbol ident = (TerminalSymbol) children.get(0);
        Value value = currentTable.getSymbol(ident.getToken().value);
        if (value instanceof VarValue varValue) {
            if (children.size() == 1) {
                if (isLVal) {
                    return varValue.getPointer();
                } else {
                    TempValue tempVar = new TempValue(tempCount++);
                    varValue.setTempVar(tempVar);
                    LoadUser loadUser = new LoadUser(varValue.getPointer(), tempVar);
                    currentUser.addUser(loadUser);
                    return varValue.getTempVar();
                }
            } else {
                throw new RuntimeException("can not add index for var");
            }
        } else if (value instanceof OneDimensionArrayValue oneDimensionArrayValue) {
            if (children.size() == 1) {
                if (isLVal) {
                    throw new RuntimeException("can not assign a array");
                } else {
                    ArrayList<Value> index = new ArrayList<>();
                    if (oneDimensionArrayValue.getDim1() == null) {
                        return oneDimensionArrayValue.getPointer();
                    }
                    index.add(new IntConstValue(0));
                    index.add(new IntConstValue(0));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("i32*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, oneDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    return pointer;
                }
            } else if (children.size() == 4) {
                if (isLVal) {
                    ArrayList<Value> index = new ArrayList<>();
                    if (oneDimensionArrayValue.getDim1() != null) {
                        index.add(new IntConstValue(0));
                    }
                    index.add(Exp(children.get(2)));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("i32*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, oneDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    return pointer;
                } else {
                    ArrayList<Value> index = new ArrayList<>();
                    if (oneDimensionArrayValue.getDim1() != null) {
                        index.add(new IntConstValue(0));
                    }
                    index.add(Exp(children.get(2)));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("i32*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, oneDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    TempValue tempVar = new TempValue(tempCount++);
                    tempVar.setType("i32");
                    LoadUser loadUser = new LoadUser(pointer, tempVar);
                    currentUser.addUser(loadUser);
                    return tempVar;
                }
            } else {
                throw new RuntimeException("can not add two index for one dimension array");
            }
        } else if (value instanceof TwoDimensionArrayValue twoDimensionArrayValue) {
            if (children.size() == 1) {
                if (isLVal) {
                    throw new RuntimeException("can not assign a array");
                } else {
                    ArrayList<Value> index = new ArrayList<>();
                    if (twoDimensionArrayValue.getDim1() == null) {
                        return twoDimensionArrayValue.getPointer();
                    }
                    index.add(new IntConstValue(0));
                    index.add(new IntConstValue(0));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("[" + twoDimensionArrayValue.getDim2() + " x i32]*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, twoDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    return pointer;
                }
            } else if (children.size() == 4) {
                if (isLVal) {
                    throw new RuntimeException("can not assign a array");
                } else {
                    ArrayList<Value> index = new ArrayList<>();
                    if (twoDimensionArrayValue.getDim1() != null) {
                        index.add(new IntConstValue(0));
                    }
                    index.add(Exp(children.get(2)));
                    index.add(new IntConstValue(0));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("i32*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, twoDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    return pointer;
                }
            } else if (children.size() == 7) {
                if (isLVal) {
                    ArrayList<Value> index = new ArrayList<>();
                    if (twoDimensionArrayValue.getDim1() != null) {
                        index.add(new IntConstValue(0));
                    }
                    index.add(Exp(children.get(2)));
                    index.add(Exp(children.get(5)));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("i32*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, twoDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    return pointer;
                } else {
                    ArrayList<Value> index = new ArrayList<>();
                    if (twoDimensionArrayValue.getDim1() != null) {
                        index.add(new IntConstValue(0));
                    }
                    index.add(Exp(children.get(2)));
                    index.add(Exp(children.get(5)));
                    TempPointerValue pointer = new TempPointerValue(tempCount++);
                    pointer.setType("i32*");
                    GetelementptrUser getelementptrUser = new GetelementptrUser(pointer, twoDimensionArrayValue.getPointer(), index);
                    currentUser.addUser(getelementptrUser);
                    TempValue tempVar = new TempValue(tempCount++);
                    tempVar.setType("i32");
                    LoadUser loadUser = new LoadUser(pointer, tempVar);
                    currentUser.addUser(loadUser);
                    return tempVar;
                }
            } else {
                throw new RuntimeException("not two index for two dimension array");
            }
        } else {
            throw new RuntimeException("not a var when LVal");
        }
    }

    private Value PrimaryExp(Node primaryExp) {
        ArrayList<Node> children = primaryExp.getChildren();
        if (children.get(0) instanceof TerminalSymbol) {
            return Exp(children.get(1));
        } else if (children.get(0) instanceof GrammarUnit unit) {
            if (unit.getType() == GrammarUnit.GrammarUnitType.LVal) {
                return LVal(children.get(0), false);
            } else if (unit.getType() == GrammarUnit.GrammarUnitType.Number) {
                return Number(children.get(0));
            }
        }
        throw new RuntimeException("unexpected form of PrimaryExp");
    }

    private Value Number(Node number) {
        ArrayList<Node> children = number.getChildren();
        return new IntConstValue(Integer.parseInt(((TerminalSymbol) children.get(0)).getToken().value));
    }

    private Value UnaryExp(Node unaryExp) {
        ArrayList<Node> children = unaryExp.getChildren();
        if (children.size() == 1) {
            return PrimaryExp(children.get(0));
        } else if (children.size() == 2) {
            String op = UnaryOp(children.get(0));
            Value rightValue = UnaryExp(children.get(1));
            if (op.equals("-")) {
                TempValue outputValue = new TempValue(tempCount++);
                outputValue.setType("i32");
                SubUser subUser = new SubUser(new IntConstValue(0), rightValue, outputValue);
                currentUser.addUser(subUser);
                return outputValue;
            }
            return rightValue;
        } else if (children.size() == 3) {
            TerminalSymbol ident = (TerminalSymbol) children.get(0);
            FuncValue funcValue = (FuncValue) currentTable.getSymbol(ident.getToken().value);
            if (funcValue.getRetype() == 0) {
                FuncCallUser funcCallUser = new FuncCallUser(funcValue, new ArrayList<>());
                currentUser.addUser(funcCallUser);
                return null;
            } else {
                TempValue outputValue = new TempValue(tempCount++);
                FuncCallUser funcCallUser = new FuncCallUser(funcValue, new ArrayList<>(), outputValue);
                outputValue.setValue(funcValue);
                currentUser.addUser(funcCallUser);
                return outputValue;
            }
        } else if (children.size() == 4) {
            TerminalSymbol ident = (TerminalSymbol) children.get(0);
            FuncValue funcValue = (FuncValue) currentTable.getSymbol(ident.getToken().value);
            ArrayList<Value> params = FuncRParams(children.get(2));
            if (funcValue.getRetype() == 0) {
                FuncCallUser funcCallUser = new FuncCallUser(funcValue, params);
                currentUser.addUser(funcCallUser);
                return null;
            } else {
                TempValue outputValue = new TempValue(tempCount++);
                FuncCallUser funcCallUser = new FuncCallUser(funcValue, params, outputValue);
                outputValue.setValue(funcValue);
                currentUser.addUser(funcCallUser);
                return outputValue;
            }
        }
        throw new RuntimeException("unexpected form of UnaryExp");
    }

    private String UnaryOp(Node unaryOp) {
        ArrayList<Node> children = unaryOp.getChildren();
        return ((TerminalSymbol) children.get(0)).getToken().value;
    }

    private ArrayList<Value> FuncRParams(Node funcRParams) {
        ArrayList<Node> children = funcRParams.getChildren();
        ArrayList<Value> params = new ArrayList<>();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.Exp) {
                params.add(Exp(child));
            }
        }
        return params;
    }

    private Value MulExp(Node mulExp) {
        ArrayList<Node> children = mulExp.getChildren();
        if (children.size() == 1) {
            return UnaryExp(children.get(0));
        } else if (children.size() == 3) {
            String op = ((TerminalSymbol) children.get(1)).getToken().value;
            Value leftValue = MulExp(children.get(0));
            Value rightValue = UnaryExp(children.get(2));
            TempValue outputValue = new TempValue(tempCount++);
            outputValue.setType("i32");
            switch (op) {
                case "*" -> {
                    MulUser mulUser = new MulUser(leftValue, rightValue, outputValue);
                    currentUser.addUser(mulUser);
                }
                case "/" -> {
                    SdivUser divUser = new SdivUser(leftValue, rightValue, outputValue);
                    currentUser.addUser(divUser);
                }
                case "%" -> {
                    SremUser modUser = new SremUser(leftValue, rightValue, outputValue);
                    currentUser.addUser(modUser);
                }
            }
            return outputValue;
        }
        throw new RuntimeException("unexpected form of MulExp");
    }

    private Value AddExp(Node addExp) {
        ArrayList<Node> children = addExp.getChildren();
        if (children.size() == 1) {
            return MulExp(children.get(0));
        } else if (children.size() == 3) {
            String op = ((TerminalSymbol) children.get(1)).getToken().value;
            Value leftValue = AddExp(children.get(0));
            Value rightValue = MulExp(children.get(2));
            TempValue outputValue = new TempValue(tempCount++);
            outputValue.setType("i32");
            switch (op) {
                case "+" -> {
                    AddUser addUser = new AddUser(leftValue, rightValue, outputValue);
                    currentUser.addUser(addUser);
                }
                case "-" -> {
                    SubUser subUser = new SubUser(leftValue, rightValue, outputValue);
                    currentUser.addUser(subUser);
                }
            }
            return outputValue;
        }
        throw new RuntimeException("unexpected form of AddExp");
    }

    private IntConstValue calNode(Node node) {
        if (node instanceof TerminalSymbol terminalSymbol) {
            switch (terminalSymbol.getType()) {
                case INTCON:
                    return new IntConstValue(Integer.parseInt(terminalSymbol.getToken().value));
                case IDENFR:
                    Value value = currentTable.getSymbol(terminalSymbol.getToken().value);
                    if (value instanceof VarValue varValue) {
                        if (varValue.getValue() != null) {
                            return (IntConstValue) varValue.getValue();
                        } else {
                            return new IntConstValue(0);
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
                        IntConstValue value = calNode(unit.getChildren().get(1));
                        assert value != null;
                        if (op.equals("-")) {
                            return new IntConstValue(-value.getNumber());
                        } else if (op.equals("+")) {
                            return new IntConstValue(value.getNumber());
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
                        IntConstValue left = calNode(unit.getChildren().get(0));
                        IntConstValue right = calNode(unit.getChildren().get(2));
                        assert left != null;
                        assert right != null;
                        if (op.equals("*")) {
                            return new IntConstValue(left.getNumber() * right.getNumber());
                        } else if (op.equals("/")) {
                            return new IntConstValue(left.getNumber() / right.getNumber());
                        } else if (op.equals("%")) {
                            return new IntConstValue(left.getNumber() % right.getNumber());
                        } else {
                            throw new RuntimeException("unknown mul op");
                        }
                    }
                case AddExp:
                    if (unit.getChildren().size() == 1) {
                        return calNode(unit.getChildren().get(0));
                    } else if (unit.getChildren().size() == 3) {
                        String op = ((TerminalSymbol) unit.getChildren().get(1)).getToken().value;
                        IntConstValue left = calNode(unit.getChildren().get(0));
                        IntConstValue right = calNode(unit.getChildren().get(2));
                        assert left != null;
                        assert right != null;
                        if (op.equals("+")) {
                            return new IntConstValue(left.getNumber() + right.getNumber());
                        } else if (op.equals("-")) {
                            return new IntConstValue(left.getNumber() - right.getNumber());
                        } else {
                            throw new RuntimeException("unknown add op");
                        }
                    }
                case LVal:
                    ArrayList<Node> children = unit.getChildren();
                    TerminalSymbol ident = (TerminalSymbol) children.get(0);
                    int dim1;
                    int dim2;
                    switch (children.size()) {
                        case 1:
                            return calNode(ident);
                        case 4:
                            dim1 = calNode(children.get(2)).getNumber();
                            Value value = currentTable.getSymbol(ident.getToken().value);
                            if (value instanceof OneDimensionArrayValue oneDimensionArrayValue) {
                                ArrayList<Value> values = oneDimensionArrayValue.getValues();
                                if (dim1 < values.size()) {
                                    return (IntConstValue) values.get(dim1);
                                } else {
                                    return new IntConstValue(0);
                                }
                            } else {
                                throw new RuntimeException("not a one dimension array");
                            }
                        case 7:
                            dim1 = calNode(children.get(2)).getNumber();
                            dim2 = calNode(children.get(5)).getNumber();
                            Value value2 = currentTable.getSymbol(ident.getToken().value);
                            if (value2 instanceof TwoDimensionArrayValue twoDimensionArrayValue) {
                                ArrayList<Value> values = twoDimensionArrayValue.getValues();
                                if (dim1 * twoDimensionArrayValue.getDim2() + dim2 < values.size()) {
                                    return (IntConstValue) values.get(dim1 * twoDimensionArrayValue.getDim2() + dim2);
                                } else {
                                    return new IntConstValue(0);
                                }
                            } else {
                                throw new RuntimeException("not a two dimension array");
                            }
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
