import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Compiler {
    public static void main(String[] args) {
        //region 初始化符号表
        Map<String, String> symbolMap = new HashMap<>();
        symbolMap.put("main","MAINTK");
        symbolMap.put("const","CONSTTK");
        symbolMap.put("int","INTTK");
        symbolMap.put("break","BREAKTK");
        symbolMap.put("continue","CONTINUETK");
        symbolMap.put("if","IFTK");
        symbolMap.put("else","ELSETK");
        symbolMap.put("!","NOT");
        symbolMap.put("&&","AND");
        symbolMap.put("||","OR");
        symbolMap.put("for","FORTK");
        symbolMap.put("getint","GETINTTK");
        symbolMap.put("printf","PRINTFTK");
        symbolMap.put("return","RETURNTK");
        symbolMap.put("+","PLUS");
        symbolMap.put("-","MINU");
        symbolMap.put("void","VOIDTK");
        symbolMap.put("*","MULT");
        symbolMap.put("/","DIV");
        symbolMap.put("%","MOD");
        symbolMap.put("<","LSS");
        symbolMap.put("<=","LEQ");
        symbolMap.put(">","GRE");
        symbolMap.put(">=","GEQ");
        symbolMap.put("==","EQL");
        symbolMap.put("!=","NEQ");
        symbolMap.put("=","ASSIGN");
        symbolMap.put(";","SEMICN");
        symbolMap.put(",","COMMA");
        symbolMap.put("(","LPARENT");
        symbolMap.put(")","RPARENT");
        symbolMap.put("[","LBRACK");
        symbolMap.put("]","RBRACK");
        symbolMap.put("{","LBRACE");
        symbolMap.put("}","RBRACE");
        //endregion
        try {
            BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
            FileWriter fw = new FileWriter("output.txt",true);
            String line;
            int lineNum = 0;
            boolean isComment = false;
            while ((line = br.readLine()) != null) {
                char c;
                StringBuilder token = new StringBuilder();
                int num;
                String symbol;
                lineNum++;
                for (int i = 0; i < line.length(); i++) {
                    c = line.charAt(i);
                    //region 处理注释
                    if (isComment) {
                        if (c == '*') {
                            i++;
                            if (i >= line.length())
                                continue;
                            c = line.charAt(i);
                            if (c == '/') {
                                isComment = false;
                            }
                        }
                        continue;
                    }
                    //endregion
                    //region 去空格
                    if (isSpace(c)) {
                        continue;
                    }
                    //endregion
                    //region 处理单词
                    else if (isLetter(c)) {
                        while (isLetterOrDigitOrUnderline(c)) {
                            token.append(c);
                            i++;
                            if (i >= line.length())
                                break;
                            c = line.charAt(i);
                        }
                        i--;
                        symbol = symbolMap.getOrDefault(token.toString(), "IDENFR");
                        System.out.println(symbol + " " + token);
                        fw.write(symbol + " " + token + "\n");
                        fw.flush();
                    }
                    //endregion
                    //region 处理数字
                    else if (isDigit(c)) {
                        while (isDigit(c)) {
                            token.append(c);
                            i++;
                            if (i >= line.length())
                                break;
                            c = line.charAt(i);
                        }
                        i--;
                        num = Integer.parseInt(token.toString());
                        symbol = "INTCON";
                        System.out.println(symbol + " " + num);
                        fw.write(symbol + " " + num + "\n");
                        fw.flush();
                    }
                    //endregion
                    //region 处理注释
                    else if (c == '/') {
                        token.append(c);
                        i++;
                        c = line.charAt(i);
                        if (c == '/') {
                            while (i < line.length()) {
                                i++;
                            }
                        }
                        else if (c == '*') {
                            isComment = true;
                            i++;
                            while (i < line.length()) {
                                c = line.charAt(i);
                                if (c == '*') {
                                    i++;
//                                    if (i >= line.length())
//                                        break;
                                    c = line.charAt(i);
                                    if (c == '/') {
                                        isComment = false;
                                        break;
                                    }
                                }
                                i++;
                            }
                        }
                        else {
                            symbol = symbolMap.get(token.toString());
                            System.out.println(symbol + " " + token);
                            fw.write(symbol + " " + token + "\n");
                            fw.flush();
                            i--;
                        }

                    }
                    //endregion
                    //region 处理字符及字符串
                    else if (c == '\"' || c == '\'' ) {
                        token.append(c);
                        i++;
//                        if (i >= line.length())
//                            break;
                        c = line.charAt(i);
                        while (c != '\"' && c != '\'') {
                            token.append(c);
                            i++;
//                            if (i >= line.length())
//                                break;
                            c = line.charAt(i);
                        }
                        token.append(c);
                        symbol = "STRCON";
                        System.out.println(symbol + " " + token);
                        fw.write(symbol + " " + token + "\n");
                        fw.flush();
                    }
                    //endregion
                    //region 处理双字符分界符
                    else if (c == '!' || c == '&' || c == '|' || c == '<' || c == '>' || c == '=') {
                        token.append(c);
                        i++;
//                        if (i >= line.length())
//                            break;
                        c = line.charAt(i);
                        token.append(c);
                        if (symbolMap.containsKey(token.toString())) {
                            symbol = symbolMap.get(token.toString());
                            System.out.println(symbol + " " + token);
                            fw.write(symbol + " " + token + "\n");
                            fw.flush();
                        }
                        else {
                            symbol = symbolMap.get(token.substring(0, 1));
                            System.out.println(symbol + " " + token.substring(0, 1));
                            fw.write(symbol + " " + token.substring(0, 1) + "\n");
                            fw.flush();
                            i--;
                        }
                    }
                    //endregion
                    //region 处理单字符分界符
                    else {
                        token.append(c);
                        symbol = symbolMap.get(token.toString());
                        System.out.println(symbol + " " + token);
                        fw.write(symbol + " " + token + "\n");
                        fw.flush();
                    }
                    //endregion
                    token = new StringBuilder();
                }
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isLetterOrDigitOrUnderline(char c) {
        return isDigit(c) || isLetter(c) || c == '_';
    }

    public static boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
}
