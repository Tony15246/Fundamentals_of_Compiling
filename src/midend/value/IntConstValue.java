package midend.value;

public class IntConstValue extends Value{
    private final int number;

    public IntConstValue(int number) {
        this.number = number;
        setType("i32");
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }
}
