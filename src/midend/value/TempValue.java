package midend.value;

public class TempValue extends Value {
    private final int varNumber;            // 当前局部变量的标识符编号。
    private Value value;                    // 当前局部变量指向的变量

    public TempValue(int varNumber) {
        this.varNumber = varNumber;
    }

    public String getType() {
        if (value != null) {
            return value.getType();
        } else {
            return super.getType();
        }
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "%" + varNumber;
    }
}
