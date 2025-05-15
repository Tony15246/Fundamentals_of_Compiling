package midend.value;

public class GlobalVarPointerValue extends Value{
    private final String name;

    public GlobalVarPointerValue(String name, Value value) {
        this.name = name;
        setValue(value);
    }

    public String getType() {
        return getValue().getType() + "*";
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
