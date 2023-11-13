package midend;

import frontend.GrammarUnit;
import frontend.Node;
import frontend.TerminalSymbol;
import midend.value.GlobalVarPointerValue;
import midend.value.VarValue;
import midend.value.user.User;

import java.util.ArrayList;

public class Visitor {
    private User rootUser;
    private int tempCount = 0;
    private final Node rootNode;
    private SymbolTable rootTable;
    private SymbolTable currentTable;

    public Visitor(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void visit() {
        rootTable = new SymbolTable();
        currentTable = rootTable;
        ArrayList<Node> children = rootNode.getChildren();
        for (Node child : children) {
            GrammarUnit unit = (GrammarUnit) child;
            switch (unit.getType()) {
                case Decl:
                    Decl(child);
                    break;
                case FuncDef:
//                    FuncDef(child);
                    break;
                case MainFuncDef:
//                    MainFuncDef(child);
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
//                    VarDecl(child);
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
        VarValue globalVar;
        GlobalVarPointerValue pointer;
        for (Node child : children) {
            if (child instanceof GrammarUnit unit
                    && unit.getType() == GrammarUnit.GrammarUnitType.ConstExp) {
                dim++;
            }
        }
        switch (dim) {
            case 0:
                ident = (TerminalSymbol) children.get(0);
                pointer = new GlobalVarPointerValue(ident.getToken().value);
                globalVar = new VarValue(currentTable, ident.getToken(), true, pointer);
                //todo
                break;
            case 1, 2:
                //todo
                break;
        }
    }


}
