package frontend;

import java.util.ArrayList;

public class GrammarParser {
    private final ArrayList<Lexer.Token> tokens;
    private ArrayList<String> result = new ArrayList<>();
    private int index;

    public ArrayList<String> getResult() {
        GrammarUnit compUnit = CompUnit();
        traverse(compUnit);
        return result;
    }

    public GrammarParser(ArrayList<Lexer.Token> tokens) {
        this.tokens = tokens;
        this.index = -1;
    }

    private Lexer.Token next() {
        if (index + 1 >= tokens.size()) {
            return new Lexer.Token("END", "END", -1);
        }
        index++;
        return tokens.get(index);
    }

    private Lexer.Token now() {
        return tokens.get(index);
    }

    private Lexer.Token peek() {
        if (index + 1 >= tokens.size()) {
            return new Lexer.Token("END", "END", -1);
        }
        return tokens.get(index + 1);
    }

    private Lexer.Token peek(Integer offset) {
        if (index + offset >= tokens.size()) {
            return new Lexer.Token("error", "error", -1);
        }
        return tokens.get(index + offset);
    }

    private boolean isLVal() {
        int i = 0;
        while (peek(i).type != Lexer.Token.TokenType.SEMICN) {
            if (peek(i).type == Lexer.Token.TokenType.ASSIGN) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void traverse(Node root) {
        if (root instanceof GrammarUnit grammarUnit) {
            for (Node child : grammarUnit.getChildren()) {
                traverse(child);
            }
            if (grammarUnit.getType() != GrammarUnit.GrammarUnitType.BlockItem
                    && grammarUnit.getType() != GrammarUnit.GrammarUnitType.Decl
                    && grammarUnit.getType() != GrammarUnit.GrammarUnitType.BType) {
                result.add(grammarUnit.toString());
            }
        } else if (root instanceof TerminalSymbol terminalSymbol) {
            result.add(terminalSymbol.getToken().toString());
        }
    }

    private GrammarUnit CompUnit() {
        next();
        GrammarUnit compUnit = new GrammarUnit(GrammarUnit.GrammarUnitType.CompUnit);
        while (peek(2).type != Lexer.Token.TokenType.LPARENT) {
            GrammarUnit decl = Decl();
            compUnit.addChild(decl);
        }
        while (peek().type != Lexer.Token.TokenType.MAINTK) {
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
            throw new RuntimeException("ConstDecl error");
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
            throw new RuntimeException("ConstDecl error");
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
            throw new RuntimeException("VarDecl error");
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
            throw new RuntimeException("ConstDef error");
        }
        next();
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
                throw new RuntimeException("ConstDef error");
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
            throw new RuntimeException("ConstDef error");
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
                    throw new RuntimeException("ConstInitVal error");
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
        if (now().type == Lexer.Token.TokenType.IDENFR) {
            TerminalSymbol idenfr = new TerminalSymbol(now());
            varDef.addChild(idenfr);
        } else {
            //error
            throw new RuntimeException("VarDef error");
        }
        next();
        while (now().type == Lexer.Token.TokenType.LBRACK) {
            TerminalSymbol lBrack = new TerminalSymbol(now());
            varDef.addChild(lBrack);
            next();
            GrammarUnit constExp = ConstExp();
            varDef.addChild(constExp);
            if (now().type == Lexer.Token.TokenType.RBRACK) {
                TerminalSymbol rBrack = new TerminalSymbol(now());
                varDef.addChild(rBrack);
                next();
            } else {
                //error
                throw new RuntimeException("VarDef error");
            }
        }
        if (now().type == Lexer.Token.TokenType.ASSIGN) {
            TerminalSymbol assign = new TerminalSymbol(now());
            varDef.addChild(assign);
            next();
            GrammarUnit initVal = InitVal();
            varDef.addChild(initVal);
        }
        return varDef;
    }

    private GrammarUnit InitVal() {
        GrammarUnit initVal = new GrammarUnit(GrammarUnit.GrammarUnitType.InitVal);
        if (now().type == Lexer.Token.TokenType.LBRACE) {
            TerminalSymbol lBrace = new TerminalSymbol(now());
            initVal.addChild(lBrace);
            if (next().type == Lexer.Token.TokenType.RBRACE) {
                TerminalSymbol rBrace = new TerminalSymbol(now());
                initVal.addChild(rBrace);
                next();
            } else {
                GrammarUnit initValNext = InitVal();
                initVal.addChild(initValNext);
                while (now().type == Lexer.Token.TokenType.COMMA) {
                    TerminalSymbol comma = new TerminalSymbol(now());
                    initVal.addChild(comma);
                    next();
                    initValNext = InitVal();
                    initVal.addChild(initValNext);
                }
                if (now().type == Lexer.Token.TokenType.RBRACE) {
                    TerminalSymbol rBrace = new TerminalSymbol(now());
                    initVal.addChild(rBrace);
                    next();
                } else {
                    //error
                    throw new RuntimeException("InitVal error");
                }
            }
        } else {
            GrammarUnit exp = Exp();
            initVal.addChild(exp);
        }
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
            throw new RuntimeException("FuncDef error");
        }
        if (next().type == Lexer.Token.TokenType.LPARENT) {
            TerminalSymbol lParent = new TerminalSymbol(now());
            funcDef.addChild(lParent);
        } else {
            //error
            throw new RuntimeException("FuncDef error");
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
                throw new RuntimeException("FuncDef error");
            }
            next();
            GrammarUnit block = Block();
            funcDef.addChild(block);
        } else if (now().type == Lexer.Token.TokenType.LBRACE) {
            GrammarUnit block = Block();
            funcDef.addChild(block);
        } else {
            //error
            throw new RuntimeException("FuncDef error");
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
            throw new RuntimeException("MainFuncDef error");
        }
        if (next().type == Lexer.Token.TokenType.MAINTK) {
            TerminalSymbol maintk = new TerminalSymbol(now());
            mainFuncDef.addChild(maintk);
        } else {
            //error
            throw new RuntimeException("MainFuncDef error");
        }
        if (next().type == Lexer.Token.TokenType.LPARENT) {
            TerminalSymbol lParent = new TerminalSymbol(now());
            mainFuncDef.addChild(lParent);
        } else {
            //error
            throw new RuntimeException("MainFuncDef error");
        }
        if (next().type == Lexer.Token.TokenType.RPARENT) {
            TerminalSymbol rParent = new TerminalSymbol(now());
            mainFuncDef.addChild(rParent);
        } else {
            //error
            throw new RuntimeException("MainFuncDef error");
        }
        next();
        GrammarUnit block = Block();
        mainFuncDef.addChild(block);
        return mainFuncDef;
    }

    private GrammarUnit FuncType() {
        GrammarUnit funcType = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncType);
        if (now().type == Lexer.Token.TokenType.INTTK) {
            TerminalSymbol inttk = new TerminalSymbol(now());
            funcType.addChild(inttk);
            next();
        } else if (now().type == Lexer.Token.TokenType.VOIDTK) {
            TerminalSymbol voidtk = new TerminalSymbol(now());
            funcType.addChild(voidtk);
            next();
        } else {
            //error
            throw new RuntimeException("FuncType error");
        }
        return funcType;
    }

    private GrammarUnit FuncFParams() {
        GrammarUnit funcFParams = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncFParams);
        GrammarUnit funcFParam = FuncFParam();
        funcFParams.addChild(funcFParam);
        while (now().type == Lexer.Token.TokenType.COMMA) {
            TerminalSymbol comma = new TerminalSymbol(now());
            funcFParams.addChild(comma);
            next();
            funcFParam = FuncFParam();
            funcFParams.addChild(funcFParam);
        }
        return funcFParams;
    }

    private GrammarUnit FuncFParam() {
        GrammarUnit funcFParam = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncFParam);
        GrammarUnit bType = BType();
        funcFParam.addChild(bType);
        if (now().type == Lexer.Token.TokenType.IDENFR) {
            TerminalSymbol idenfr = new TerminalSymbol(now());
            funcFParam.addChild(idenfr);
        } else {
            //error
            throw new RuntimeException("FuncFParam error");
        }
        if (next().type == Lexer.Token.TokenType.LBRACK) {
            TerminalSymbol lBrack = new TerminalSymbol(now());
            funcFParam.addChild(lBrack);
            if (next().type == Lexer.Token.TokenType.RBRACK) {
                TerminalSymbol rBrack = new TerminalSymbol(now());
                funcFParam.addChild(rBrack);
            } else {
                //error
                throw new RuntimeException("FuncFParam error");
            }
            next();
            while (now().type == Lexer.Token.TokenType.LBRACK) {
                lBrack = new TerminalSymbol(now());
                funcFParam.addChild(lBrack);
                next();
                GrammarUnit constExp = ConstExp();
                funcFParam.addChild(constExp);
                if (now().type == Lexer.Token.TokenType.RBRACK) {
                    TerminalSymbol rBrack = new TerminalSymbol(now());
                    funcFParam.addChild(rBrack);
                    next();
                } else {
                    //error
                    throw new RuntimeException("FuncFParam error");
                }
            }
        }
        return funcFParam;
    }

    private GrammarUnit Block() {
        GrammarUnit block = new GrammarUnit(GrammarUnit.GrammarUnitType.Block);
        if (now().type == Lexer.Token.TokenType.LBRACE) {
            TerminalSymbol lBrace = new TerminalSymbol(now());
            block.addChild(lBrace);
        } else {
            //error
            throw new RuntimeException("Block error");
        }
        next();
        while (now().type != Lexer.Token.TokenType.RBRACE) {
            GrammarUnit blockItem = BlockItem();
            block.addChild(blockItem);
        }
        if (now().type == Lexer.Token.TokenType.RBRACE) {
            TerminalSymbol rBrace = new TerminalSymbol(now());
            block.addChild(rBrace);
            next();
        } else {
            //error
            throw new RuntimeException("Block error");
        }
        return block;
    }

    private GrammarUnit Stmt() {
        GrammarUnit stmt = new GrammarUnit(GrammarUnit.GrammarUnitType.Stmt);
        if (now().type == Lexer.Token.TokenType.IFTK) {
            TerminalSymbol iftk = new TerminalSymbol(now());
            stmt.addChild(iftk);
            if (next().type == Lexer.Token.TokenType.LPARENT) {
                TerminalSymbol lParent = new TerminalSymbol(now());
                stmt.addChild(lParent);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            next();
            GrammarUnit cond = Cond();
            stmt.addChild(cond);
            if (now().type == Lexer.Token.TokenType.RPARENT) {
                TerminalSymbol rParent = new TerminalSymbol(now());
                stmt.addChild(rParent);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            next();
            GrammarUnit stmt1 = Stmt();
            stmt.addChild(stmt1);
            if (now().type == Lexer.Token.TokenType.ELSETK) {
                TerminalSymbol elsetk = new TerminalSymbol(now());
                stmt.addChild(elsetk);
                next();
                GrammarUnit stmt2 = Stmt();
                stmt.addChild(stmt2);
            }
        } else if (now().type == Lexer.Token.TokenType.FORTK) {
            TerminalSymbol fortk = new TerminalSymbol(now());
            stmt.addChild(fortk);
            if (next().type == Lexer.Token.TokenType.LPARENT) {
                TerminalSymbol lParent = new TerminalSymbol(now());
                stmt.addChild(lParent);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            if (next().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
            } else {
                GrammarUnit forStmt1 = ForStmt();
                stmt.addChild(forStmt1);
                if (now().type == Lexer.Token.TokenType.SEMICN) {
                    TerminalSymbol semicn = new TerminalSymbol(now());
                    stmt.addChild(semicn);
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            }
            if (next().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
            } else {
                GrammarUnit cond = Cond();
                stmt.addChild(cond);
                if (now().type == Lexer.Token.TokenType.SEMICN) {
                    TerminalSymbol semicn = new TerminalSymbol(now());
                    stmt.addChild(semicn);
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            }
            if (next().type == Lexer.Token.TokenType.RPARENT) {
                TerminalSymbol rParent = new TerminalSymbol(now());
                stmt.addChild(rParent);
            } else {
                GrammarUnit forStmt2 = ForStmt();
                stmt.addChild(forStmt2);
                if (now().type == Lexer.Token.TokenType.RPARENT) {
                    TerminalSymbol rParent = new TerminalSymbol(now());
                    stmt.addChild(rParent);
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            }
            next();
            GrammarUnit stmt1 = Stmt();
            stmt.addChild(stmt1);
        } else if (now().type == Lexer.Token.TokenType.BREAKTK) {
            TerminalSymbol breaktk = new TerminalSymbol(now());
            stmt.addChild(breaktk);
            if (next().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
                next();
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
        } else if (now().type == Lexer.Token.TokenType.CONTINUETK) {
            TerminalSymbol continuetk = new TerminalSymbol(now());
            stmt.addChild(continuetk);
            if (next().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
                next();
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
        } else if (now().type == Lexer.Token.TokenType.RETURNTK) {
            TerminalSymbol returntk = new TerminalSymbol(now());
            stmt.addChild(returntk);
            if (next().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
                next();
            } else {
                GrammarUnit exp = Exp();
                stmt.addChild(exp);
                if (now().type == Lexer.Token.TokenType.SEMICN) {
                    TerminalSymbol semicn = new TerminalSymbol(now());
                    stmt.addChild(semicn);
                    next();
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            }
        } else if (now().type == Lexer.Token.TokenType.PRINTFTK) {
            TerminalSymbol printftk = new TerminalSymbol(now());
            stmt.addChild(printftk);
            if (next().type == Lexer.Token.TokenType.LPARENT) {
                TerminalSymbol lParent = new TerminalSymbol(now());
                stmt.addChild(lParent);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            if (next().type == Lexer.Token.TokenType.STRCON) {
                TerminalSymbol strcon = new TerminalSymbol(now());
                stmt.addChild(strcon);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            next();
            while (now().type == Lexer.Token.TokenType.COMMA) {
                TerminalSymbol comma = new TerminalSymbol(now());
                stmt.addChild(comma);
                next();
                GrammarUnit exp = Exp();
                stmt.addChild(exp);
            }
            if (now().type == Lexer.Token.TokenType.RPARENT) {
                TerminalSymbol rParent = new TerminalSymbol(now());
                stmt.addChild(rParent);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            if (next().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
                next();
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
        } else if (now().type == Lexer.Token.TokenType.LBRACE) {
            GrammarUnit block = Block();
            stmt.addChild(block);
        } else if (isLVal()) {
            GrammarUnit lVal = LVal();
            stmt.addChild(lVal);
            if (now().type == Lexer.Token.TokenType.ASSIGN) {
                TerminalSymbol assign = new TerminalSymbol(now());
                stmt.addChild(assign);
            } else {
                //error
                throw new RuntimeException("Stmt error");
            }
            if (next().type == Lexer.Token.TokenType.GETINTTK) {
                TerminalSymbol getinttk = new TerminalSymbol(now());
                stmt.addChild(getinttk);
                if (next().type == Lexer.Token.TokenType.LPARENT) {
                    TerminalSymbol lParent = new TerminalSymbol(now());
                    stmt.addChild(lParent);
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
                if (next().type == Lexer.Token.TokenType.RPARENT) {
                    TerminalSymbol rParent = new TerminalSymbol(now());
                    stmt.addChild(rParent);
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
                if (next().type == Lexer.Token.TokenType.SEMICN) {
                    TerminalSymbol semicn = new TerminalSymbol(now());
                    stmt.addChild(semicn);
                    next();
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            } else {
                GrammarUnit exp = Exp();
                stmt.addChild(exp);
                if (now().type == Lexer.Token.TokenType.SEMICN) {
                    TerminalSymbol semicn = new TerminalSymbol(now());
                    stmt.addChild(semicn);
                    next();
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            }
        } else {
            if (now().type == Lexer.Token.TokenType.SEMICN) {
                TerminalSymbol semicn = new TerminalSymbol(now());
                stmt.addChild(semicn);
                next();
            } else {
                GrammarUnit exp = Exp();
                stmt.addChild(exp);
                if (now().type == Lexer.Token.TokenType.SEMICN) {
                    TerminalSymbol semicn = new TerminalSymbol(now());
                    stmt.addChild(semicn);
                    next();
                } else {
                    //error
                    throw new RuntimeException("Stmt error");
                }
            }
        }
        return stmt;
    }

    private GrammarUnit ForStmt() {
        GrammarUnit forStmt = new GrammarUnit(GrammarUnit.GrammarUnitType.ForStmt);
        GrammarUnit lVal = LVal();
        forStmt.addChild(lVal);
        if (now().type == Lexer.Token.TokenType.ASSIGN) {
            TerminalSymbol assign = new TerminalSymbol(now());
            forStmt.addChild(assign);
        } else {
            //error
            throw new RuntimeException("ForStmt error");
        }
        next();
        GrammarUnit exp = Exp();
        forStmt.addChild(exp);
        return forStmt;
    }

    private GrammarUnit Exp() {
        GrammarUnit exp = new GrammarUnit(GrammarUnit.GrammarUnitType.Exp);
        GrammarUnit addExp = AddExp();
        exp.addChild(addExp);
        return exp;
    }

    private GrammarUnit Cond() {
        GrammarUnit cond = new GrammarUnit(GrammarUnit.GrammarUnitType.Cond);
        GrammarUnit lOrExp = LOrExp();
        cond.addChild(lOrExp);
        return cond;
    }

    private GrammarUnit LVal() {
        GrammarUnit lVal = new GrammarUnit(GrammarUnit.GrammarUnitType.LVal);
        if (now().type == Lexer.Token.TokenType.IDENFR) {
            TerminalSymbol idenfr = new TerminalSymbol(now());
            lVal.addChild(idenfr);
        } else {
            //error
            throw new RuntimeException("LVal error");
        }
        while (next().type == Lexer.Token.TokenType.LBRACK) {
            TerminalSymbol lBrack = new TerminalSymbol(now());
            lVal.addChild(lBrack);
            next();
            GrammarUnit exp = Exp();
            lVal.addChild(exp);
            if (now().type == Lexer.Token.TokenType.RBRACK) {
                TerminalSymbol rBrack = new TerminalSymbol(now());
                lVal.addChild(rBrack);
            } else {
                //error
                throw new RuntimeException("LVal error");
            }
        }
        return lVal;
    }

    private GrammarUnit PrimaryExp() {
        GrammarUnit primaryExp = new GrammarUnit(GrammarUnit.GrammarUnitType.PrimaryExp);
        if (now().type == Lexer.Token.TokenType.LPARENT) {
            TerminalSymbol lParent = new TerminalSymbol(now());
            primaryExp.addChild(lParent);
            next();
            GrammarUnit exp = Exp();
            primaryExp.addChild(exp);
            if (now().type == Lexer.Token.TokenType.RPARENT) {
                TerminalSymbol rParent = new TerminalSymbol(now());
                primaryExp.addChild(rParent);
            } else {
                //error
                throw new RuntimeException("PrimaryExp error");
            }
        } else if (now().type == Lexer.Token.TokenType.INTCON) {
            GrammarUnit number = Number();
            primaryExp.addChild(number);
        } else {
            GrammarUnit lVal = LVal();
            primaryExp.addChild(lVal);
        }
        return primaryExp;
    }

    private GrammarUnit Number() {
        GrammarUnit number = new GrammarUnit(GrammarUnit.GrammarUnitType.Number);
        if (now().type == Lexer.Token.TokenType.INTCON) {
            TerminalSymbol intcon = new TerminalSymbol(now());
            number.addChild(intcon);
            next();
        } else {
            //error
            throw new RuntimeException("Number error");
        }
        return number;
    }

    private GrammarUnit UnaryExp() {
        GrammarUnit unaryExp = new GrammarUnit(GrammarUnit.GrammarUnitType.UnaryExp);
        if (now().type == Lexer.Token.TokenType.PLUS || now().type == Lexer.Token.TokenType.MINU || now().type == Lexer.Token.TokenType.NOT) {
            GrammarUnit unaryOp = UnaryOp();
            unaryExp.addChild(unaryOp);
            GrammarUnit unaryExpNext = UnaryExp();
            unaryExp.addChild(unaryExpNext);
        } else if (now().type == Lexer.Token.TokenType.IDENFR && peek().type == Lexer.Token.TokenType.LPARENT) {
            TerminalSymbol idenfr = new TerminalSymbol(now());
            unaryExp.addChild(idenfr);
            TerminalSymbol lParent = new TerminalSymbol(next());
            unaryExp.addChild(lParent);
            if (next().type == Lexer.Token.TokenType.RPARENT) {
                TerminalSymbol rParent = new TerminalSymbol(now());
                unaryExp.addChild(rParent);
            } else {
                GrammarUnit funcRParams = FuncRParams();
                unaryExp.addChild(funcRParams);
                if (now().type == Lexer.Token.TokenType.RPARENT) {
                    TerminalSymbol rParent = new TerminalSymbol(now());
                    unaryExp.addChild(rParent);
                } else {
                    //error
                    throw new RuntimeException("UnaryExp error");
                }
            }
        } else {
            GrammarUnit primaryExp = PrimaryExp();
            unaryExp.addChild(primaryExp);
        }
        return unaryExp;
    }

    private GrammarUnit UnaryOp() {
        GrammarUnit unaryOp = new GrammarUnit(GrammarUnit.GrammarUnitType.UnaryOp);
        if (now().type == Lexer.Token.TokenType.PLUS) {
            TerminalSymbol plus = new TerminalSymbol(now());
            unaryOp.addChild(plus);
            next();
        } else if (now().type == Lexer.Token.TokenType.MINU) {
            TerminalSymbol minu = new TerminalSymbol(now());
            unaryOp.addChild(minu);
            next();
        } else if (now().type == Lexer.Token.TokenType.NOT) {
            TerminalSymbol not = new TerminalSymbol(now());
            unaryOp.addChild(not);
            next();
        } else {
            //error
            throw new RuntimeException("UnaryOp error");
        }
        return unaryOp;
    }

    private GrammarUnit FuncRParams() {
        GrammarUnit funcRParams = new GrammarUnit(GrammarUnit.GrammarUnitType.FuncRParams);
        GrammarUnit exp = Exp();
        funcRParams.addChild(exp);
        while (now().type == Lexer.Token.TokenType.COMMA) {
            TerminalSymbol comma = new TerminalSymbol(now());
            funcRParams.addChild(comma);
            next();
            exp = Exp();
            funcRParams.addChild(exp);
        }
        return funcRParams;
    }

    private GrammarUnit MulExp() {
        GrammarUnit mulExp = new GrammarUnit(GrammarUnit.GrammarUnitType.MulExp);
        GrammarUnit unaryExp = UnaryExp();
        mulExp.addChild(unaryExp);
        while (now().type == Lexer.Token.TokenType.MULT || now().type == Lexer.Token.TokenType.DIV || now().type == Lexer.Token.TokenType.MOD) {
            GrammarUnit temp = new GrammarUnit(GrammarUnit.GrammarUnitType.MulExp);
            temp.addChild(mulExp);
            mulExp = temp;
            TerminalSymbol mulOp = new TerminalSymbol(now());
            mulExp.addChild(mulOp);
            next();
            unaryExp = UnaryExp();
            mulExp.addChild(unaryExp);
        }
        return mulExp;
    }

    private GrammarUnit AddExp() {
        GrammarUnit addExp = new GrammarUnit(GrammarUnit.GrammarUnitType.AddExp);
        GrammarUnit mulExp = MulExp();
        addExp.addChild(mulExp);
        while (now().type == Lexer.Token.TokenType.PLUS || now().type == Lexer.Token.TokenType.MINU) {
            GrammarUnit temp = new GrammarUnit(GrammarUnit.GrammarUnitType.AddExp);
            temp.addChild(addExp);
            addExp = temp;
            TerminalSymbol addOp = new TerminalSymbol(now());
            addExp.addChild(addOp);
            next();
            mulExp = MulExp();
            addExp.addChild(mulExp);
        }
        return addExp;
    }

    private GrammarUnit RelExp() {
        GrammarUnit relExp = new GrammarUnit(GrammarUnit.GrammarUnitType.RelExp);
        GrammarUnit addExp = AddExp();
        relExp.addChild(addExp);
        while (now().type == Lexer.Token.TokenType.LSS || now().type == Lexer.Token.TokenType.LEQ || now().type == Lexer.Token.TokenType.GRE || now().type == Lexer.Token.TokenType.GEQ) {
            GrammarUnit temp = new GrammarUnit(GrammarUnit.GrammarUnitType.RelExp);
            temp.addChild(relExp);
            relExp = temp;
            TerminalSymbol relOp = new TerminalSymbol(now());
            relExp.addChild(relOp);
            next();
            addExp = AddExp();
            relExp.addChild(addExp);
        }
        return relExp;
    }

    private GrammarUnit EqExp() {
        GrammarUnit eqExp = new GrammarUnit(GrammarUnit.GrammarUnitType.EqExp);
        GrammarUnit relExp = RelExp();
        eqExp.addChild(relExp);
        while (now().type == Lexer.Token.TokenType.EQL || now().type == Lexer.Token.TokenType.NEQ) {
            GrammarUnit temp = new GrammarUnit(GrammarUnit.GrammarUnitType.EqExp);
            temp.addChild(eqExp);
            eqExp = temp;
            TerminalSymbol eqOp = new TerminalSymbol(now());
            eqExp.addChild(eqOp);
            next();
            relExp = RelExp();
            eqExp.addChild(relExp);
        }
        return eqExp;
    }

    private GrammarUnit LAndExp() {
        GrammarUnit lAndExp = new GrammarUnit(GrammarUnit.GrammarUnitType.LAndExp);
        GrammarUnit eqExp = EqExp();
        lAndExp.addChild(eqExp);
        while (now().type == Lexer.Token.TokenType.AND) {
            GrammarUnit temp = new GrammarUnit(GrammarUnit.GrammarUnitType.LAndExp);
            temp.addChild(lAndExp);
            lAndExp = temp;
            TerminalSymbol and = new TerminalSymbol(now());
            lAndExp.addChild(and);
            next();
            eqExp = EqExp();
            lAndExp.addChild(eqExp);
        }
        return lAndExp;
    }

    private GrammarUnit LOrExp() {
        GrammarUnit lOrExp = new GrammarUnit(GrammarUnit.GrammarUnitType.LOrExp);
        GrammarUnit lAndExp = LAndExp();
        lOrExp.addChild(lAndExp);
        while (now().type == Lexer.Token.TokenType.OR) {
            GrammarUnit temp = new GrammarUnit(GrammarUnit.GrammarUnitType.LOrExp);
            temp.addChild(lOrExp);
            lOrExp = temp;
            TerminalSymbol or = new TerminalSymbol(now());
            lOrExp.addChild(or);
            next();
            lAndExp = LAndExp();
            lOrExp.addChild(lAndExp);
        }
        return lOrExp;
    }

    private GrammarUnit ConstExp() {
        GrammarUnit constExp = new GrammarUnit(GrammarUnit.GrammarUnitType.ConstExp);
        GrammarUnit addExp = AddExp();
        constExp.addChild(addExp);
        return constExp;
    }

    private GrammarUnit BlockItem() {
        GrammarUnit blockItem = new GrammarUnit(GrammarUnit.GrammarUnitType.BlockItem);
        if (now().type == Lexer.Token.TokenType.INTTK || now().type == Lexer.Token.TokenType.CONSTTK) {
            GrammarUnit decl = Decl();
            blockItem.addChild(decl);
        } else {
            GrammarUnit stmt = Stmt();
            blockItem.addChild(stmt);
        }
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
            throw new RuntimeException("BType error");
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
            throw new RuntimeException("Decl error");
        }
        return decl;
    }
}
