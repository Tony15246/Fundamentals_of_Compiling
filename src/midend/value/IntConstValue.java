package midend.value;

public class IntConstValue extends Value{
    private final int value;

    public IntConstValue(int value) {
        this.value = value;
        setType("i32");
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
