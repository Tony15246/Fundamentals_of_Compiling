package midend.value;

public class IntConstValue extends Value{
    private final int value;

    public IntConstValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
