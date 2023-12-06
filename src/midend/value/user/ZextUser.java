package midend.value.user;

import midend.value.Value;

public class ZextUser extends User{
    private final Value outputValue;
    private final Value inputValue;

    public ZextUser(Value outputValue, Value inputValue) {
        this.outputValue = outputValue;
        this.inputValue = inputValue;
    }

    @Override
    public String toString() {
        return outputValue.toString() + " = zext i1 " + inputValue.toString() + " to i32";
    }
}
