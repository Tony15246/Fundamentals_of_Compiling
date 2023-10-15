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

    public void checkDecl(Node decl) {
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

    public void checkConstDecl(Node constDecl) {
        ArrayList<Node> children = constDecl.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit){
                checkConstDef(child);
            }
        }
    }

    public void checkConstDef(Node constDef) {
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
                int value = checkConstExp(children.get(2));
                currentTable.addSymbol(new VarSymbol(currentTable, ident.getToken(), true, value));
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

    public ArrayList<Integer> checkConstInitVal(Node constInitVal) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Node> children = constInitVal.getChildren();
        if (children.get(0) instanceof GrammarUnit) {
            values.add(checkConstExp(children.get(0)));
        }
        else {
            for (Node child : children) {
                if (child instanceof GrammarUnit unit
                        && unit.getType() == GrammarUnit.GrammarUnitType.ConstInitVal) {
                    values.addAll(checkConstInitVal(child));
                }
            }
        }
        return values;
    }

    public void checkVarDecl(Node varDecl) {
        ArrayList<Node> children = varDecl.getChildren();
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.VarDef) {
                checkVarDef(child);
            }
        }
    }

    public void checkVarDef(Node varDef) {
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
                break;
        }
    }

    public void checkFuncDef(Node funcDef) {
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
        }
        checkBlock(children.get(5));
        currentTable = currentTable.getPre();
    }

    public void checkMainFuncDef(Node mainFuncDef) {
        ArrayList<Node> children = mainFuncDef.getChildren();
        SymbolTable main = new SymbolTable();
        main.setFuncSymbol(new FuncSymbol(main, null, 2));
        currentTable.addNext(main);
        currentTable = main;
        checkBlock(children.get(4));
        currentTable = currentTable.getPre();
    }

    public int checkFuncType(Node funcType) {
        int retype;
        TerminalSymbol type = (TerminalSymbol) funcType.getChildren().get(0);
        retype = switch (type.getToken().value) {
            case "void" -> 0;
            case "int" -> 1;
            default -> -1;
        };
        return retype;
    }

    public ArrayList<Symbol> checkFuncFParams(Node funcFParams) {
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

    public Symbol checkFuncFParam(Node funcFParam) {
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

    public void checkBlock(Node block) {
        ArrayList<Node> children = block.getChildren();
        int count = 0;
        for (Node child : children) {
            if (child instanceof GrammarUnit) {
                checkBlockItem(child);
                count++;
            }
        }
        TerminalSymbol rbrace = (TerminalSymbol) children.get(-1);
        FuncSymbol funcSymbol = currentTable.getFuncSymbol();
        if (funcSymbol != null) {
            if (funcSymbol.getRetype() != 0 && count == 0) {
                Logger.getLogger().addError(new Error(rbrace.getToken().lineNum, "g"));
            } else if (funcSymbol.getRetype() == 0 && count > 0) {
                GrammarUnit lastBlockItem = (GrammarUnit) children.get(-2);
                TerminalSymbol returnSymbol = null;
                for (Node lastBlockItemChild : lastBlockItem.getChildren()) {
                    if (lastBlockItemChild instanceof TerminalSymbol terminalSymbol
                            && terminalSymbol.getToken().type == Lexer.Token.TokenType.RETURNTK) {
                        returnSymbol = terminalSymbol;
                    }
                }
                if (returnSymbol != null && returnSymbol != lastBlockItem.getChildren().get(-2)) {
                    Logger.getLogger().addError(new Error(returnSymbol.getToken().lineNum, "f"));
                }
            } else if (funcSymbol.getRetype() != 0 && count > 0) {
                GrammarUnit lastBlockItem = (GrammarUnit) children.get(-2);
                boolean error = true;
                for (Node lastBlockItemChild : lastBlockItem.getChildren()) {
                    if (lastBlockItemChild instanceof TerminalSymbol returnSymbol
                            && returnSymbol.getToken().type == Lexer.Token.TokenType.RETURNTK) {
                        error = false;
                    }
                }
                if (error) {
                    Logger.getLogger().addError(new Error(rbrace.getToken().lineNum, "g"));
                }
            }
        }
    }

    public void checkBlockItem(Node blockItem) {
        //todo
    }

    public int checkConstExp(Node constExp) {
        //todo
        return 0;
    }
}
