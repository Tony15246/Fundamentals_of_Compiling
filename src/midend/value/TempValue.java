package midend.value;

public class TempValue extends Value {
    private final int varNumber;            // 当前局部变量的标识符编号。

    public TempValue(int varNumber) {
        this.varNumber = varNumber;
    }

    public String getType() {
        if (getValue() != null) {
            return getValue().getType();
        } else {
            return super.getType();
        }
    }

    @Override
    public String toString() {
        return "%" + varNumber;
    }
}
