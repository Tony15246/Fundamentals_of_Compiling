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
        sb.append(funcValue.getType());
        sb.append(" @");
        sb.append(funcValue.getName());
        sb.append("(");
        sb.append(funcValue.getParamsString());
        sb.append(") {");
        sb.append("\n");
        for (int i = 0; i < getUsers().size(); i++) {
            User user = getUsers().get(i);
            if (user instanceof BasicBlockUser && user.getUsers().isEmpty()) {
                user.addUser(new BrUser((BasicBlockUser) getUsers().get(i + 1)));
                sb.append(user).append("\n");
            } else {
                sb.append(user.toString()).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
