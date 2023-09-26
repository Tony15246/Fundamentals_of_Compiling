package frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private BufferedReader br;
    private char c;
    private int lineNum;

    public Lexer(BufferedReader br) {
        this.br = br;
        lineNum = 1;
    }

    public static class Token {
        public String symbol;
        public String value;
        public int lineNum;
        public TokenType type;
        public Token(String symbol, String value, int lineNum) {
            this.type = TokenType.valueOf(symbol);
            this.symbol = symbol;
            this.value = value;
            this.lineNum = lineNum;
        }

        public Token(String symbol, int value, int lineNum) {
            this.type = TokenType.valueOf(symbol);
            this.symbol = symbol;
            this.value = String.valueOf(value);
            this.lineNum = lineNum;
        }

        @Override
        public String toString() {
            return symbol + " " + value;
        }

        public enum TokenType {
            IDENFR,
            INTCON,
            STRCON,
            MAINTK,
            CONSTTK,
            INTTK,
            BREAKTK,
            CONTINUETK,
            IFTK,
            ELSETK,
            NOT,
            AND,
            OR,
            FORTK,
            GETINTTK,
            PRINTFTK,
            RETURNTK,
            PLUS,
            MINU,
            VOIDTK,
            MULT,
            DIV,
            MOD,
            LSS,
            LEQ,
            GRE,
            GEQ,
            EQL,
            NEQ,
            ASSIGN,
            SEMICN,
            COMMA,
            LPARENT,
            RPARENT,
            LBRACK,
            RBRACK,
            LBRACE,
            RBRACE,
            END,
        }
    }

    private final Map<String, String> symbolMap = new HashMap<>() {
        {
            put("main", "MAINTK");
            put("const", "CONSTTK");
            put("int", "INTTK");
            put("break", "BREAKTK");
            put("continue", "CONTINUETK");
            put("if", "IFTK");
            put("else", "ELSETK");
            put("for", "FORTK");
            put("getint", "GETINTTK");
            put("printf", "PRINTFTK");
            put("return", "RETURNTK");
            put("void", "VOIDTK");
        }
    };

    private boolean getChar() throws IOException {
        int ch;
        br.mark(1);
        ch = br.read();
        if (ch == -1) {
            return false;
        }
        c = (char) ch;
        if (c == '\n') {
            lineNum++;
        }
        return true;
    }

    public ArrayList<Token> getSymbol() throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        String symbol;
        int num;
        while (getChar()) {
            //region 处理单词
            if (isLetter(c)) {
                while (isLetterOrDigitOrUnderline(c)) {
                    token.append(c);
                    getChar();
                }
                br.reset();
                symbol = symbolMap.getOrDefault(token.toString(), "IDENFR");
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            //endregion
            //region 处理数字
            else if (isDigit(c)) {
                while (isDigit(c)) {
                    token.append(c);
                    getChar();
                }
                br.reset();
                num = Integer.parseInt(token.toString());
                symbol = Token.TokenType.INTCON.name();
                tokens.add(new Token(symbol, num, lineNum));
                token = new StringBuilder();
            }
            //endregion
            //region 处理注释
            else if (c == '/') {
                token.append(c);
                getChar();
                if (c == '/') {
                    while (c != '\n') {
                        getChar();
                    }
                    token = new StringBuilder();
                }
                else if (c == '*') {
                    while (getChar()) {
                        if (c == '*') {
                            getChar();
                            if (c == '/') {
                                break;
                            }
                        }
                    }
                    token = new StringBuilder();
                }
                else {
                    br.reset();
                    symbol = Token.TokenType.DIV.name();
                    tokens.add(new Token(symbol, token.toString(), lineNum));
                    token = new StringBuilder();
                }
            }
            //endregion
            //region 处理字符及字符串
            else if (c == '\"' || c == '\'' ) {
                token.append(c);
                getChar();
                while (c != '\"' && c != '\'') {
                    token.append(c);
                    getChar();
                }
                token.append(c);
                symbol = Token.TokenType.STRCON.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            //endregion
            //region 处理双字符分界符
            else if (c == '!') {
                token.append(c);
                getChar();
                if (c == '=') {
                    token.append(c);
                    symbol = Token.TokenType.NEQ.name();
                }
                else {
                    br.reset();
                    symbol = Token.TokenType.NOT.name();
                }
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '&') {
                token.append(c);
                getChar();
                if (c == '&') {
                    token.append(c);
                    symbol = Token.TokenType.AND.name();
                }
                else {
                    return null;
                }
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '|') {
                token.append(c);
                getChar();
                if (c == '|') {
                    token.append(c);
                    symbol = Token.TokenType.OR.name();
                }
                else {
                    return null;
                }
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '<') {
                token.append(c);
                getChar();
                if (c == '=') {
                    token.append(c);
                    symbol = Token.TokenType.LEQ.name();
                }
                else {
                    br.reset();
                    symbol = Token.TokenType.LSS.name();
                }
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '>') {
                token.append(c);
                getChar();
                if (c == '=') {
                    token.append(c);
                    symbol = Token.TokenType.GEQ.name();
                }
                else {
                    br.reset();
                    symbol = Token.TokenType.GRE.name();
                }
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '=') {
                token.append(c);
                getChar();
                if (c == '=') {
                    token.append(c);
                    symbol = Token.TokenType.EQL.name();
                }
                else {
                    br.reset();
                    symbol = Token.TokenType.ASSIGN.name();
                }
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            //endregion
            //region 处理单字符分界符
            else if (c == '+') {
                token.append(c);
                symbol = Token.TokenType.PLUS.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '-') {
                token.append(c);
                symbol = Token.TokenType.MINU.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '*') {
                token.append(c);
                symbol = Token.TokenType.MULT.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '%') {
                token.append(c);
                symbol = Token.TokenType.MOD.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == ';') {
                token.append(c);
                symbol = Token.TokenType.SEMICN.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == ',') {
                token.append(c);
                symbol = Token.TokenType.COMMA.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '(') {
                token.append(c);
                symbol = Token.TokenType.LPARENT.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == ')') {
                token.append(c);
                symbol = Token.TokenType.RPARENT.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '[') {
                token.append(c);
                symbol = Token.TokenType.LBRACK.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == ']') {
                token.append(c);
                symbol = Token.TokenType.RBRACK.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '{') {
                token.append(c);
                symbol = Token.TokenType.LBRACE.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            else if (c == '}') {
                token.append(c);
                symbol = Token.TokenType.RBRACE.name();
                tokens.add(new Token(symbol, token.toString(), lineNum));
                token = new StringBuilder();
            }
            //endregion
        }
        return tokens;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isLetterOrDigitOrUnderline(char c) {
        return isDigit(c) || isLetter(c) || c == '_';
    }
}
