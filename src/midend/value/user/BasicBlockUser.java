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
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(":\n");
        for (User user : getUsers()) {
            sb.append(user.toString()).append("\n");
        }
        return sb.toString();
    }
}
