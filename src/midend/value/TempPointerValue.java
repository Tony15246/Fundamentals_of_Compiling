package midend.value;

public class TempPointerValue extends TempValue{
    public TempPointerValue(int varNumber) {
        super(varNumber);
    }

    @Override
    public String getType() {
        return getValue().getType() + "*";
    }
}
