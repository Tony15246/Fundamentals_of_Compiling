package midend.value;

public class TempPointerValue extends TempValue{
    public TempPointerValue(int varNumber) {
        super(varNumber);
    }

    @Override
    public String getType() {
        if (getValue() != null) {
            return getValue().getType() + "*";
        } else {
            return super.getType();
        }
    }
}
