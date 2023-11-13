package midend.value;

public class TempValue {
    private final int varNumber;            // 当前局部变量的标识符编号。

    public TempValue(int varNumber) {
        this.varNumber = varNumber;
    }

    @Override
    public String toString() {
        return "%" + varNumber;
    }
}
