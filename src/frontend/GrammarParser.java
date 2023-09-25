package frontend;

import java.util.ArrayList;

public class GrammarParser {
    private final ArrayList<Lexer.Token> tokens;
    private int index;

    public GrammarParser(ArrayList<Lexer.Token> tokens) {
        this.tokens = tokens;
        this.index = -1;
    }

    private Lexer.Token next() {
        if (index >= tokens.size()) {
            return new Lexer.Token("error", "error", -1);
        }
        index++;
        return tokens.get(index);
    }

    private Lexer.Token now() {
        return tokens.get(index);
    }

    private Lexer.Token peek() {
        if (index + 1 >= tokens.size()) {
            return new Lexer.Token("error", "error", -1);
        }
        return tokens.get(index + 1);
    }

    private Lexer.Token peek(Integer offset) {
        if (index + offset >= tokens.size()) {
            return new Lexer.Token("error", "error", -1);
        }
        return tokens.get(index + offset);
    }

    private GrammarUnit CompUnit() {
        next();
        GrammarUnit compUnit = new GrammarUnit(GrammarUnit.GrammarUnitType.CompUnit);
        while (peek(3).type != Lexer.Token.TokenType.LPARENT) {
            GrammarUnit decl = Decl();
            compUnit.addChild(decl);
        }
        while (peek(2).type != Lexer.Token.TokenType.MAINTK) {
            GrammarUnit funcDef = FuncDef();
            compUnit.addChild(funcDef);
        }
        GrammarUnit mainFuncDef = MainFuncDef();
        compUnit.addChild(mainFuncDef);
        return compUnit;
    }

    private GrammarUnit ConstDecl() {
        GrammarUnit constDecl = new GrammarUnit(GrammarUnit.GrammarUnitType.ConstDecl);
        if (now().type == Lexer.Token.TokenType.CONSTTK) {
            TerminalSymbol consttk = new TerminalSymbol(now());
            constDecl.addChild(consttk);
        } else {
            //error
        }
        next();
        GrammarUnit bType = BType();
        constDecl.addChild(bType);
        GrammarUnit constDef = ConstDef();
        constDecl.addChild(constDef);
        while (now().type == Lexer.Token.TokenType.COMMA) {
            TerminalSymbol comma = new TerminalSymbol(now());
            constDecl.addChild(comma);
            next();
            constDef = ConstDef();
            constDecl.addChild(constDef);
        }
        if (now().type == Lexer.Token.TokenType.SEMICN) {
            TerminalSymbol semicn = new TerminalSymbol(now());
            constDecl.addChild(semicn);
            next();
        } else {
            //error
        }
        return constDecl;
    }

    private GrammarUnit VarDecl() {
        GrammarUnit varDecl = new GrammarUnit(GrammarUnit.GrammarUnitType.VarDecl);
        GrammarUnit bType = BType();
        varDecl.addChild(bType);
        GrammarUnit varDef = VarDef();
        varDecl.addChild(varDef);
        while (now().type == Lexer.Token.TokenType.COMMA) {
            TerminalSymbol comma = new TerminalSymbol(now());
            varDecl.addChild(comma);
            next();
            varDef = VarDef();
            varDecl.addChild(varDef);
        }
        if (now().type == Lexer.Token.TokenType.SEMICN) {
            TerminalSymbol semicn = new TerminalSymbol(now());
            varDecl.addChild(semicn);
            next();
        } else {
            //error
        }
        return varDecl;
    }

    private GrammarUnit ConstDef() {
        GrammarUnit constDef = new GrammarUnit(GrammarUnit.GrammarUnitType.ConstDef);
        if (now().type == Lexer.Token.TokenType.IDENFR) {
            TerminalSymbol idenfr = new TerminalSymbol(now());
            constDef.addChild(idenfr);
        } else {
            //error
        }
        while (now().type == Lexer.Token.TokenType.LBRACK) {
            TerminalSymbol lBrack = new TerminalSymbol(now());
            constDef.addChild(lBrack);
            next();
            GrammarUnit constExp = ConstExp();
            constDef.addChild(constExp);
            if (now().type == Lexer.Token.TokenType.RBRACK) {
                TerminalSymbol rBrack = new TerminalSymbol(now());
                constDef.addChild(rBrack);
                next();
            } else {
                //error
            }
        }
        if (now().type == Lexer.Token.TokenType.ASSIGN) {
            TerminalSymbol assign = new TerminalSymbol(now());
            constDef.addChild(assign);
            next();
            GrammarUnit constInitVal = ConstInitVal();
            constDef.addChild(constInitVal);
        } else {
            //error
        }
        return constDef;
    }

    private GrammarUnit ConstInitVal() {
        GrammarUnit constInitVal = new GrammarUnit(GrammarUnit.GrammarUnitType.ConstInitVal);
        if (now().type == Lexer.Token.TokenType.LBRACE) {
            TerminalSymbol lBrace = new TerminalSymbol(now());
            constInitVal.addChild(lBrace);
            if (next().type == Lexer.Token.TokenType.RBRACE) {
                TerminalSymbol rBrace = new TerminalSymbol(now());
                constInitVal.addChild(rBrace);
                next();
            } else {
                GrammarUnit constInitValNext = ConstInitVal();
                constInitVal.addChild(constInitValNext);
                while (now().type == Lexer.Token.TokenType.COMMA) {
                    TerminalSymbol comma = new TerminalSymbol(now());
                    constInitVal.addChild(comma);
                    next();
                    constInitValNext = ConstInitVal();
                    constInitVal.addChild(constInitValNext);
                }
                if (now().type == Lexer.Token.TokenType.RBRACE) {
                    TerminalSymbol rBrace = new TerminalSymbol(now());
                    constInitVal.addChild(rBrace);
                    next();
                } else {
                    //error
                }
            }
        } else {
            GrammarUnit constExp = ConstExp();
            constInitVal.addChild(constExp);
        }
        return constInitVal;
    }
    private GrammarUnit VarDef() {
        GrammarUnit varDef = new GrammarUnit(GrammarUnit.GrammarUnitType.VarDef);
        return varDef;
    }

    private GrammarUnit InitVal() {
        GrammarUnit initVal = new GrammarUnit(GrammarUnit.GrammarUnitType.InitVal);
        return initVal;
    }

    private GrammarUnit FuncDef() {
        GrammarUnit funcDef = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncDef);
        GrammarUnit funcType = FuncType();
        funcDef.addChild(funcType);
        if (next().type == Lexer.Token.TokenType.IDENFR) {
            TerminalSymbol idenfr = new TerminalSymbol(now());
            funcDef.addChild(idenfr);
        } else {
            //error
        }
        if (next().type == Lexer.Token.TokenType.LPARENT) {
            TerminalSymbol lParent = new TerminalSymbol(now());
            funcDef.addChild(lParent);
        } else {
            //error
        }
        next();
        if (now().type == Lexer.Token.TokenType.INTTK) {
            GrammarUnit funcFParams = FuncFParams();
            funcDef.addChild(funcFParams);
            if (next().type == Lexer.Token.TokenType.RPARENT) {
                TerminalSymbol rParent = new TerminalSymbol(now());
                funcDef.addChild(rParent);
            } else {
                //error
            }
            next();
            GrammarUnit block = Block();
            funcDef.addChild(block);
        } else if (now().type == Lexer.Token.TokenType.LBRACE) {
            GrammarUnit block = Block();
            funcDef.addChild(block);
        } else {
            //error
        }
        return funcDef;
    }

    private GrammarUnit MainFuncDef() {
        GrammarUnit mainFuncDef = new GrammarUnit(GrammarUnit.GrammarUnitType.MainFuncDef);
        if (now().type == Lexer.Token.TokenType.INTTK) {
            TerminalSymbol inttk = new TerminalSymbol(now());
            mainFuncDef.addChild(inttk);
        } else {
            //error
        }
        if (next().type == Lexer.Token.TokenType.MAINTK) {
            TerminalSymbol maintk = new TerminalSymbol(now());
            mainFuncDef.addChild(maintk);
        } else {
            //error
        }
        if (next().type == Lexer.Token.TokenType.LPARENT) {
            TerminalSymbol lParent = new TerminalSymbol(now());
            mainFuncDef.addChild(lParent);
        } else {
            //error
        }
        if (next().type == Lexer.Token.TokenType.RPARENT) {
            TerminalSymbol rParent = new TerminalSymbol(now());
            mainFuncDef.addChild(rParent);
        } else {
            //error
        }
        next();
        GrammarUnit block = Block();
        mainFuncDef.addChild(block);
        return mainFuncDef;
    }

    private GrammarUnit FuncType() {
        GrammarUnit funcType = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncType);
        return funcType;
    }

    private GrammarUnit FuncFParams() {
        GrammarUnit funcFParams = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncFParams);
        return funcFParams;
    }

    private GrammarUnit FuncFParam() {
        GrammarUnit funcFParam = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncFParam);
        return funcFParam;
    }

    private GrammarUnit Block() {
        GrammarUnit block = new GrammarUnit(GrammarUnit.GrammarUnitType.Block);
        return block;
    }

    private GrammarUnit Stmt() {
        GrammarUnit stmt = new GrammarUnit(GrammarUnit.GrammarUnitType.Stmt);
        return stmt;
    }

    private GrammarUnit ForStmt() {
        GrammarUnit forStmt = new GrammarUnit(GrammarUnit.GrammarUnitType.ForStmt);
        return forStmt;
    }

    private GrammarUnit Exp() {
        GrammarUnit exp = new GrammarUnit(GrammarUnit.GrammarUnitType.Exp);
        return exp;
    }

    private GrammarUnit Cond() {
        GrammarUnit cond = new GrammarUnit(GrammarUnit.GrammarUnitType.Cond);
        return cond;
    }

    private GrammarUnit LVal() {
        GrammarUnit lVal = new GrammarUnit(GrammarUnit.GrammarUnitType.LVal);
        return lVal;
    }

    private GrammarUnit PrimaryExp() {
        GrammarUnit primaryExp = new GrammarUnit(GrammarUnit.GrammarUnitType.PrimaryExp);
        return primaryExp;
    }

    private GrammarUnit Number() {
        GrammarUnit number = new GrammarUnit(GrammarUnit.GrammarUnitType.Number);
        return number;
    }

    private GrammarUnit UnaryExp() {
        GrammarUnit unaryExp = new GrammarUnit(GrammarUnit.GrammarUnitType.UnaryExp);
        return unaryExp;
    }

    private GrammarUnit UnaryOp() {
        GrammarUnit unaryOp = new GrammarUnit(GrammarUnit.GrammarUnitType.UnaryOp);
        return unaryOp;
    }

    private GrammarUnit FuncRParams() {
        GrammarUnit funcRParams = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncRParams);
        return funcRParams;
    }

    private GrammarUnit MulExp() {
        GrammarUnit mulExp = new GrammarUnit(GrammarUnit.GrammarUnitType.MulExp);
        return mulExp;
    }

    private GrammarUnit AddExp() {
        GrammarUnit addExp = new GrammarUnit(GrammarUnit.GrammarUnitType.AddExp);
        return addExp;
    }

    private GrammarUnit RelExp() {
        GrammarUnit relExp = new GrammarUnit(GrammarUnit.GrammarUnitType.RelExp);
        return relExp;
    }

    private GrammarUnit EqExp() {
        GrammarUnit eqExp = new GrammarUnit(GrammarUnit.GrammarUnitType.EqExp);
        return eqExp;
    }

    private GrammarUnit LAndExp() {
        GrammarUnit lAndExp = new GrammarUnit(GrammarUnit.GrammarUnitType.LAndExp);
        return lAndExp;
    }

    private GrammarUnit LOrExp() {
        GrammarUnit lOrExp = new GrammarUnit(GrammarUnit.GrammarUnitType.LOrExp);
        return lOrExp;
    }

    private GrammarUnit ConstExp() {
        GrammarUnit constExp = new GrammarUnit(GrammarUnit.GrammarUnitType.ConstExp);
        return constExp;
    }

    private GrammarUnit BlockItem() {
        GrammarUnit blockItem = new GrammarUnit(GrammarUnit.GrammarUnitType.BlockItem);
        return blockItem;
    }

    private GrammarUnit BType() {
        GrammarUnit bType = new GrammarUnit(GrammarUnit.GrammarUnitType.BType);
        if (now().type == Lexer.Token.TokenType.INTTK) {
            TerminalSymbol inttk = new TerminalSymbol(now());
            bType.addChild(inttk);
            next();
        } else {
            //error
        }
        return bType;
    }

    private GrammarUnit Decl() {
        GrammarUnit decl = new GrammarUnit(GrammarUnit.GrammarUnitType.Decl);
        if (now().type == Lexer.Token.TokenType.CONSTTK) {
            GrammarUnit constDecl = ConstDecl();
            decl.addChild(constDecl);
        } else if (now().type == Lexer.Token.TokenType.INTTK) {
            GrammarUnit varDecl = VarDecl();
            decl.addChild(varDecl);
        } else {
            //error
        }
        return decl;
    }
}
