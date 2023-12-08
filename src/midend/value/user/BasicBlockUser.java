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
        boolean hasJump = false;
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(":\n");
        for (User user : getUsers()) {
            if (hasJump) {
                break;
            } else {
                sb.append(user.toString()).append("\n");
                if (user instanceof BrUser || user instanceof RetUser) {
                    hasJump = true;
                }
            }
        }
        return sb.toString();
    }
}
