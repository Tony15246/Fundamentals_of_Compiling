package midend.value;

public class GlobalVarPointerValue extends Value{
    private final String name;
    private final Value value;

    public GlobalVarPointerValue(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    public String getType() {
        return value.getType() + "*";
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
