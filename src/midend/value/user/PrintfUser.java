package midend.value.user;

import midend.value.Value;

import java.util.ArrayList;

public class PrintfUser extends User{
    private final String format;
    private ArrayList<Value> args = new ArrayList<>();

    public PrintfUser(String format){
        this.format = format;
    }

    public void addArg(Value arg){
        args.add(arg);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < format.length(); i++) {
            char currentChar = format.charAt(i);
            if (currentChar == '%') {
                i++;
                sb.append("call void @putint(i32 ").append(args.remove(0).toString()).append(")");
            } else if (currentChar == '\\') {
                i++;
                sb.append("call void @putch(i32 10)");
            } else {
                sb.append("call void @putch(i32 ").append((int) currentChar).append(")");
            }
        }
        return sb.toString();
    }
}
