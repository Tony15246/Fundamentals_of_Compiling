package midend.value.user;

import midend.value.FuncValue;

public class FuncDefUser extends User {
    private final FuncValue funcValue;

    public FuncDefUser(FuncValue funcValue) {
        this.funcValue = funcValue;
    }

    public FuncValue getFuncValue() {
        return funcValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define ");
        sb.append(funcValue.getRetypeString());
        sb.append(" @");
        sb.append(funcValue.getName());
        sb.append("(");
        sb.append(funcValue.getParamsString());
        sb.append(") {");
        sb.append("\n");
        for (User user : getUsers()) {
            sb.append(user.toString());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
