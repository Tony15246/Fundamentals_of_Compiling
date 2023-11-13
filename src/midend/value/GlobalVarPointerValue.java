package midend.value;

public class GlobalVarPointerValue extends Value{
    private final String name;

    public GlobalVarPointerValue(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
