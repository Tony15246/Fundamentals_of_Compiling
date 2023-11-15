package midend.value.user;

import midend.value.Value;

import java.util.ArrayList;

public class PrintfUser extends User {
    private final String format;
    private ArrayList<Value> args = new ArrayList<>();

    public PrintfUser(String format) {
        this.format = format;
    }

    public void addArg(Value arg) {
        args.add(arg);
    }

    public void addArgs(ArrayList<Value> args) {
        this.args.addAll(args);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int argsIndex = 0;
        for (int i = 0; i < format.length(); i++) {
            char currentChar = format.charAt(i);
            if (currentChar == '%') {
                i++;
                if (argsIndex < args.size()) {
                    sb.append("call void @putint(i32 ").append(args.get(argsIndex).toString()).append(")\n");
                    argsIndex++;
                } else {
                    throw new RuntimeException("Too few arguments for printf");
                }
            } else if (currentChar == '\\') {
                i++;
                sb.append("call void @putch(i32 10)\n");
            } else {
                sb.append("call void @putch(i32 ").append((int) currentChar).append(")\n");
            }
        }
        return sb.toString();
    }
}
