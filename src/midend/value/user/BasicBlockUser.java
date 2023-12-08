package midend.value.user;

public class BasicBlockUser extends User{
    private int label;

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    @Override
    public String toString() {
        boolean hasBr = false;
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(":\n");
        for (User user : getUsers()) {
            if (user instanceof BrUser) {
                if (!hasBr) {
                    sb.append(user).append("\n");
                    hasBr = true;
                }
            } else {
                sb.append(user.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
