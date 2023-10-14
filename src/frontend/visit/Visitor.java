package frontend.visit;

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
                repeat = currentTable.getSymbol(ident.getToken().value);
                if (repeat != null && !(repeat instanceof FuncSymbol && repeat.getTable() != currentTable)) {
                    Logger.getLogger().addError(new Error(ident.getToken().lineNum, "b"));
                    return;
                }
                currentTable.addSymbol(new VarSymbol(currentTable, ident.getToken(), true));
                break;
            case 1:
                ident = (TerminalSymbol) children.get(0);
                repeat = currentTable.getSymbol(ident.getToken().value);
                if (repeat != null && !(repeat instanceof FuncSymbol && repeat.getTable() != currentTable)) {
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
                repeat = currentTable.getSymbol(ident.getToken().value);
                if (repeat != null && !(repeat instanceof FuncSymbol && repeat.getTable() != currentTable)) {
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

    public void checkFuncDef(Node funcDef) {
        //todo
    }

    public void checkMainFuncDef(Node mainFuncDef) {
        //todo
    }

    public void checkVarDecl(Node varDecl) {
        //todo
    }

    public int checkConstExp(Node constExp) {
        //todo
        return 0;
    }
}
