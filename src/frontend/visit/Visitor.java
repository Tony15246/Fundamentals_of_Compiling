package frontend.visit;

import frontend.Lexer;
import frontend.error.Error;
import frontend.error.Logger;
import frontend.parse.GrammarUnit;
import frontend.parse.Node;
import frontend.parse.TerminalSymbol;

import java.util.ArrayList;

public class Visitor {
    private final Node root;
    private SymbolTable rootTable;
    private SymbolTable currentTable;

    public Visitor(Node root) {
        this.root = root;
    }

    public SymbolTable getRootTable() {
        return rootTable;
    }

    public void visit() {
        rootTable = new SymbolTable();
        currentTable = rootTable;
        ArrayList<Node> children = root.getChildren();
        for (Node child : children) {
            GrammarUnit unit = (GrammarUnit) child;
            switch (unit.getType()) {
                case Decl:
                    checkDecl(child);
                    break;
                case FuncDef:
                    checkFuncDef(child);
                    break;
                case MainFuncDef:
                    checkMainFuncDef(child);
                    break;
            }
        }
    }

    private void checkDecl(Node decl) {
        ArrayList<Node> children = decl.getChildren();
        for (Node child : children) {
            GrammarUnit unit = (GrammarUnit) child;
            switch (unit.getType()) {
                case ConstDecl:
                    checkConstDecl(child);
                    break;
                case VarDecl:
                    checkVarDecl(child);
                    break;
            }
        }
    }

    private void checkConstDecl(Node constDecl) {
        ArrayList<Node> children = constDecl.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.ConstDef) {
                checkConstDef(child);
            }
        }
    }

    private void checkConstDef(Node constDef) {
        int dim = 0;
        TerminalSymbol ident;
        Symbol repeat;
        ArrayList<Integer> values;
        int dim1;
        int dim2;
        ArrayList<Node> children = constDef.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit) {
                if (unit.getType() == GrammarUnit.GrammarUnitType.ConstExp) {
                    dim++;
                }
            }
        }
        switch (dim) {
            case 0:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                values = checkConstInitVal(children.get(2));
                currentTable.addSymbol(new VarSymbol(currentTable, ident.getToken(), true, values.get(0)));
                break;
            case 1:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                GrammarUnit constExp = (GrammarUnit) children.get(2);
                dim1 = checkConstExp(constExp);
                values = checkConstInitVal(children.get(5));
                currentTable.addSymbol(new OneDimensionArraySymbol(currentTable, ident.getToken(), true, dim1, values));
                break;
            case 2:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                GrammarUnit constExp1 = (GrammarUnit) children.get(2);
                GrammarUnit constExp2 = (GrammarUnit) children.get(5);
                dim1 = checkConstExp(constExp1);
                dim2 = checkConstExp(constExp2);
                values = checkConstInitVal(children.get(8));
                currentTable.addSymbol(new TwoDimensionArraySymbol(currentTable, ident.getToken(), true, dim1, dim2, values));
                break;
        }
    }

    private ArrayList<Integer> checkConstInitVal(Node constInitVal) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Node> children = constInitVal.getChildren();
        if (children.get(0) instanceof GrammarUnit) {
            values.add(checkConstExp(children.get(0)));
        } else {
            for (Node child : children) {
                if (child instanceof GrammarUnit unit
                        && unit.getType() == GrammarUnit.GrammarUnitType.ConstInitVal) {
                    values.addAll(checkConstInitVal(child));
                }
            }
        }
        return values;
    }

    private void checkVarDecl(Node varDecl) {
        ArrayList<Node> children = varDecl.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.VarDef) {
                checkVarDef(child);
            }
        }
    }

    private void checkVarDef(Node varDef) {
        int dim = 0;
        TerminalSymbol ident;
        Symbol repeat;
        int dim1;
        int dim2;
        ArrayList<Node> children = varDef.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit) {
                if (unit.getType() == GrammarUnit.GrammarUnitType.ConstExp) {
                    dim++;
                }
            }
        }
        switch (dim) {
            case 0:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                currentTable.addSymbol(new VarSymbol(currentTable, ident.getToken(), false));
                if (children.size() == 3) {
                    checkInitVal(children.get(2));
                }
                break;
            case 1:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                GrammarUnit constExp = (GrammarUnit) children.get(2);
                dim1 = checkConstExp(constExp);
                currentTable.addSymbol(new OneDimensionArraySymbol(currentTable, ident.getToken(), false, dim1));
                if (children.size() == 6) {
                    checkInitVal(children.get(5));
                }
                break;
            case 2:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                GrammarUnit constExp1 = (GrammarUnit) children.get(2);
                GrammarUnit constExp2 = (GrammarUnit) children.get(5);
                dim1 = checkConstExp(constExp1);
                dim2 = checkConstExp(constExp2);
                currentTable.addSymbol(new TwoDimensionArraySymbol(currentTable, ident.getToken(), false, dim1, dim2));
                if (children.size() == 9) {
                    checkInitVal(children.get(8));
                }
                break;
        }
    }

    private void checkInitVal(Node initVal) {
        ArrayList<Node> children = initVal.getChildren();
        if (children.get(0) instanceof TerminalSymbol) {
            for (Node child : children) {
                if (child instanceof GrammarUnit unit
                        && unit.getType() == GrammarUnit.GrammarUnitType.InitVal) {
                    checkInitVal(unit);
                }
            }
        } else {
            checkExp(children.get(0));
        }
    }

    private void checkFuncDef(Node funcDef) {
        ArrayList<Node> children = funcDef.getChildren();
        int retype = checkFuncType(children.get(0));
        TerminalSymbol ident = (TerminalSymbol) children.get(1);
        Symbol repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
        if (repeat != null) {
            Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
            return;
        }
        FuncSymbol funcSymbol = new FuncSymbol(currentTable, ident.getToken(), retype);
        currentTable.addSymbol(funcSymbol);
        SymbolTable temp = new SymbolTable();
        temp.setFuncSymbol(funcSymbol);
        currentTable.addNext(temp);
        currentTable = temp;
        if (children.get(3) instanceof GrammarUnit) {
            currentTable.getFuncSymbol().addParam(checkFuncFParams(children.get(3)));
            checkBlock(children.get(5));
        } else {
            checkBlock(children.get(4));
        }
        currentTable = currentTable.getPre();
    }

    private void checkMainFuncDef(Node mainFuncDef) {
        ArrayList<Node> children = mainFuncDef.getChildren();
        SymbolTable main = new SymbolTable();
        main.setFuncSymbol(new FuncSymbol(main, null, 2));
        currentTable.addNext(main);
        currentTable = main;
        checkBlock(children.get(4));
        currentTable = currentTable.getPre();
    }

    private int checkFuncType(Node funcType) {
        int retype;
        TerminalSymbol type = (TerminalSymbol) funcType.getChildren().get(0);
        retype = switch (type.getToken().value) {
            case "void" -> 0;
            case "int" -> 1;
            default -> -1;
        };
        return retype;
    }

    private ArrayList<Symbol> checkFuncFParams(Node funcFParams) {
        ArrayList<Symbol> params = new ArrayList<>();
        ArrayList<Node> children = funcFParams.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.FuncFParam) {
                params.add(checkFuncFParam(child));
            }
        }
        return params;
    }

    private Symbol checkFuncFParam(Node funcFParam) {
        Symbol param;
        TerminalSymbol ident;
        Symbol repeat;
        ArrayList<Node> children = funcFParam.getChildren();
        switch (children.size()) {
            case 2:
                ident = (TerminalSymbol) children.get(1);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return null;
                }
                param = new VarSymbol(currentTable, ident.getToken(), false);
                currentTable.addSymbol(param);
                break;
            case 4:
                ident = (TerminalSymbol) children.get(1);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return null;
                }
                param = new OneDimensionArraySymbol(currentTable, ident.getToken(), false, -1);
                currentTable.addSymbol(param);
                break;
            case 7:
                ident = (TerminalSymbol) children.get(1);
                repeat = currentTable.getSymbolInCurrentTable(ident.getToken().value);
                if (repeat != null) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return null;
                }
                GrammarUnit constExp = (GrammarUnit) children.get(5);
                int dim2 = checkConstExp(constExp);
                param = new TwoDimensionArraySymbol(currentTable, ident.getToken(), false, -1, dim2);
                currentTable.addSymbol(param);
                break;
            default:
                return null;
        }
        return param;
    }

    private void checkBlock(Node block) {
        ArrayList<Node> children = block.getChildren();
        int count = 0;
        for (Node child : children) {
            if (child instanceof GrammarUnit) {
                checkBlockItem(child);
                count++;
            }
        }
        TerminalSymbol rbrace = (TerminalSymbol) children.get(children.size()-1);
        FuncSymbol funcSymbol = currentTable.getFuncSymbol();
        if (funcSymbol != null) {
            if (funcSymbol.getRetype() != 0 && count == 0) {
                Logger.getLogger().addError(new Error(rbrace.getToken().lineNum, "g"));
            } else if (funcSymbol.getRetype() == 0 && count > 0) {
                GrammarUnit lastBlockItem = (GrammarUnit) children.get(children.size()-2);
                TerminalSymbol returnSymbol = null;
                GrammarUnit lastStmt = (GrammarUnit) lastBlockItem.getChildren().get(0);
                for (Node lastStmtChild : lastStmt.getChildren()) {
                    if (lastStmtChild instanceof TerminalSymbol terminalSymbol
                            && terminalSymbol.getType() == Lexer.Token.TokenType.RETURNTK) {
                        returnSymbol = terminalSymbol;
                    }
                }
                if (returnSymbol != null && returnSymbol != lastStmt.getChildren().get(lastStmt.getChildren().size()-2)) {
                    Logger.getLogger().addError(new Error(returnSymbol.getToken().lineNum, "f"));
                }
            } else if (funcSymbol.getRetype() != 0 && count > 0) {
                GrammarUnit lastBlockItem = (GrammarUnit) children.get(children.size()-2);
                boolean error = true;
                GrammarUnit lastStmt = (GrammarUnit) lastBlockItem.getChildren().get(0);
                for (Node lastStmtChild : lastStmt.getChildren()) {
                    if (lastStmtChild instanceof TerminalSymbol returnSymbol
                            && returnSymbol.getType() == Lexer.Token.TokenType.RETURNTK) {
                        error = false;
                    }
                }
                if (error) {
                    Logger.getLogger().addError(new Error(rbrace.getToken().lineNum, "g"));
                }
            }
        }
    }

    private void checkBlockItem(Node blockItem) {
        ArrayList<Node> children = blockItem.getChildren();
        if (children.get(0) instanceof GrammarUnit
                && ((GrammarUnit) children.get(0)).getType() == GrammarUnit.GrammarUnitType.Decl) {
            checkDecl(children.get(0));
        } else {
            checkStmt(children.get(0));
        }
    }

    private void checkStmt(Node stmt) {
        ArrayList<Node> children = stmt.getChildren();
        if (children.get(0) instanceof TerminalSymbol terminalSymbol) {
            switch (terminalSymbol.getType()) {
                case IFTK:
                    checkCond(children.get(2));
                    GrammarUnit stmt1 = (GrammarUnit) children.get(4);
                    if (stmt1.getChildren().size() == 1
                            && stmt1.getChildren().get(0) instanceof GrammarUnit block
                            && block.getType() == GrammarUnit.GrammarUnitType.Block) {
                        SymbolTable temp = new SymbolTable();
                        currentTable.addNext(temp);
                        currentTable = temp;
                        checkStmt(stmt1);
                        currentTable = currentTable.getPre();
                    } else {
                        checkStmt(stmt1);
                    }
                    if (children.size() == 7) {
                        GrammarUnit stmt2 = (GrammarUnit) children.get(6);
                        if (stmt2.getChildren().size() == 1
                                && stmt2.getChildren().get(0) instanceof GrammarUnit block
                                && block.getType() == GrammarUnit.GrammarUnitType.Block) {
                            SymbolTable temp = new SymbolTable();
                            currentTable.addNext(temp);
                            currentTable = temp;
                            checkStmt(stmt2);
                            currentTable = currentTable.getPre();
                        } else {
                            checkStmt(stmt2);
                        }
                    }
                    break;
                case FORTK:
                    int index = 2;
                    if (children.get(index) instanceof TerminalSymbol) {
                        index++;
                    } else {
                        checkForStmt(children.get(index));
                        index += 2;
                    }
                    if (children.get(index) instanceof TerminalSymbol) {
                        index++;
                    } else {
                        checkCond(children.get(index));
                        index += 2;
                    }
                    if (children.get(index) instanceof TerminalSymbol) {
                        index++;
                    } else {
                        checkForStmt(children.get(index));
                        index += 2;
                    }
                    GrammarUnit stmt3 = (GrammarUnit) children.get(index);
                    if (stmt3.getChildren().size() == 1
                            && stmt3.getChildren().get(0) instanceof GrammarUnit block
                            && block.getType() == GrammarUnit.GrammarUnitType.Block) {
                        SymbolTable temp = new SymbolTable();
                        currentTable.addNext(temp);
                        currentTable = temp;
                        checkStmt(stmt3);
                        currentTable = currentTable.getPre();
                    } else {
                        checkStmt(stmt3);
                    }
                    break;
                case RETURNTK:
                    if (children.size() == 3) {
                        checkExp(children.get(1));
                    }
                    break;
                case PRINTFTK:
                    for (Node child : children) {
                        if (child instanceof GrammarUnit exp
                                && exp.getType() == GrammarUnit.GrammarUnitType.Exp) {
                            checkExp(exp);
                        }
                    }
                    break;
            }
        } else if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case Block:
                    checkBlock(unit);
                    break;
                case Exp:
                    checkExp(unit);
                    break;
                case LVal:
                    checkLVal(unit);
                    if (children.get(2) instanceof GrammarUnit exp
                            && exp.getType() == GrammarUnit.GrammarUnitType.Exp) {
                        checkExp(exp);
                    }
                    break;
            }
        }
    }

    private void checkForStmt(Node forStmt) {
        ArrayList<Node> children = forStmt.getChildren();
        checkLVal(children.get(0));
        checkExp(children.get(2));
    }

    private int checkExp(Node exp) {
        return checkAddExp(exp.getChildren().get(0));
    }

    private void checkCond(Node cond) {
        checkLOrExp(cond.getChildren().get(0));
    }

    private int checkLVal(Node lVal) {
        int dim = -1;
        ArrayList<Node> children = lVal.getChildren();
        TerminalSymbol ident = (TerminalSymbol) children.get(0);
        Symbol symbol = currentTable.getSymbol(ident.getToken().value);
        if (symbol == null) {
            Logger.getLogger().addError(new Error(ident.getToken().lineNum, "c"));
            return -1;
        }
        switch (symbol.getClass().getSimpleName()) {
            case "VarSymbol":
                dim = 0;
                VarSymbol varSymbol = (VarSymbol) symbol;
                if (varSymbol.isConst()) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "h"));
                }
                break;
            case "OneDimensionArraySymbol":
                dim = 1;
                OneDimensionArraySymbol oneDimensionArraySymbol = (OneDimensionArraySymbol) symbol;
                if (oneDimensionArraySymbol.isConst()) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "h"));
                }
                break;
            case "TwoDimensionArraySymbol":
                dim = 2;
                TwoDimensionArraySymbol twoDimensionArraySymbol = (TwoDimensionArraySymbol) symbol;
                if (twoDimensionArraySymbol.isConst()) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "h"));
                }
                break;
        };
        for (Node child : children) {
            if (child instanceof GrammarUnit exp
                    && exp.getType() == GrammarUnit.GrammarUnitType.Exp) {
                dim--;
                checkExp(exp);
            }
        }
        return dim;
    }

    private int checkPrimaryExp(Node primaryExp) {
        int dim = 0;
        ArrayList<Node> children = primaryExp.getChildren();
        if (children.get(0) instanceof TerminalSymbol) {
            dim = checkExp(children.get(1));
        } else if (children.get(0) instanceof GrammarUnit lVal
                && lVal.getType() == GrammarUnit.GrammarUnitType.LVal) {
            dim = checkLVal(lVal);
        }
        return dim;
    }

    private int checkUnaryExp(Node unaryExp) {
        int dim = -1;
        ArrayList<Node> children = unaryExp.getChildren();
        if (children.get(0) instanceof TerminalSymbol ident) {
            FuncSymbol funcSymbol = (FuncSymbol) currentTable.getSymbol(ident.getToken().value);
            if (funcSymbol == null) {
                Logger.getLogger().addError(new Error(ident.getToken().lineNum, "c"));
                return dim;
            }
            if (children.size() == 4) {
                ArrayList<Integer> paramsDim = checkFuncRParams(children.get(2));
                if (paramsDim.size() != funcSymbol.getParams().size()) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "d"));
                    return dim;
                }
                for (int i = 0; i < paramsDim.size(); i++) {
                    switch (paramsDim.get(i)) {
                        case -1:
                            Logger.getLogger().addError(new Error(ident.getToken().lineNum, "e"));
                            return dim;
                        case 0:
                            if (!(funcSymbol.getParams().get(i) instanceof VarSymbol)) {
                                Logger.getLogger().addError(new Error(ident.getToken().lineNum, "e"));
                                return dim;
                            }
                            break;
                        case 1:
                            if (!(funcSymbol.getParams().get(i) instanceof OneDimensionArraySymbol)) {
                                Logger.getLogger().addError(new Error(ident.getToken().lineNum, "e"));
                                return dim;
                            }
                            break;
                        case 2:
                            if (!(funcSymbol.getParams().get(i) instanceof TwoDimensionArraySymbol)) {
                                Logger.getLogger().addError(new Error(ident.getToken().lineNum, "e"));
                                return dim;
                            }
                            break;
                    }
                }
            }
            if (funcSymbol.getRetype() == 1) {
                dim = 0;
            }
        } else if (children.get(0) instanceof GrammarUnit unit) {
            dim = switch (unit.getType()) {
                case PrimaryExp -> checkPrimaryExp(unit);
                case UnaryOp -> checkUnaryExp(children.get(1));
                default -> dim;
            };
        }
        return dim;
    }

    private ArrayList<Integer> checkFuncRParams(Node funcRParams) {
        ArrayList<Node> children = funcRParams.getChildren();
        ArrayList<Integer> paramsDim = new ArrayList<>();
        for (Node child : children) {
            if (child instanceof GrammarUnit exp
                    && exp.getType() == GrammarUnit.GrammarUnitType.Exp) {
                paramsDim.add(checkExp(exp));
            }
        }
        return paramsDim;
    }

    private int checkMulExp(Node mulExp) {
        int dim = -1;
        ArrayList<Node> children = mulExp.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case UnaryExp:
                    dim = checkUnaryExp(unit);
                    break;
                case MulExp:
                    dim = 0;
                    checkMulExp(unit);
                    checkUnaryExp(children.get(2));
                    break;
            }
        }
        return dim;
    }

    private int checkAddExp(Node addExp) {
        int dim = -1;
        ArrayList<Node> children = addExp.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case MulExp:
                    dim = checkMulExp(unit);
                    break;
                case AddExp:
                    dim = 0;
                    checkAddExp(unit);
                    checkMulExp(children.get(2));
                    break;
            }
        }
        return dim;
    }

    private void checkRelExp(Node relExp) {
        ArrayList<Node> children = relExp.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case AddExp:
                    checkAddExp(unit);
                    break;
                case RelExp:
                    checkRelExp(unit);
                    checkAddExp(children.get(2));
                    break;
            }
        }
    }

    private void checkEqExp(Node eqExp) {
        ArrayList<Node> children = eqExp.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case RelExp:
                    checkRelExp(unit);
                    break;
                case EqExp:
                    checkEqExp(unit);
                    checkRelExp(children.get(2));
                    break;
            }
        }
    }

    private void checkLAndExp(Node lAndExp) {
        ArrayList<Node> children = lAndExp.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case EqExp:
                    checkEqExp(unit);
                    break;
                case LAndExp:
                    checkLAndExp(unit);
                    checkEqExp(children.get(2));
                    break;
            }
        }
    }

    private void checkLOrExp(Node lOrExp) {
        ArrayList<Node> children = lOrExp.getChildren();
        if (children.get(0) instanceof GrammarUnit unit) {
            switch (unit.getType()) {
                case LAndExp:
                    checkLAndExp(unit);
                    break;
                case LOrExp:
                    checkLOrExp(unit);
                    checkLAndExp(children.get(2));
                    break;
            }
        }
    }

    private int checkConstExp(Node constExp) {
        //todo
        return 0;
    }
}
