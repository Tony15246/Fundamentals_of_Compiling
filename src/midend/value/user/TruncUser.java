package midend.value.user;

import midend.value.Value;

public class TruncUser extends User{
    private final Value outputValue;
    private final Value inputValue;

    public TruncUser(Value outputValue, Value inputValue) {
        this.outputValue = outputValue;
        this.inputValue = inputValue;
    }

    @Override
    public String toString() {
        return outputValue.toString() + " = trunc i32 " + inputValue.toString() + " to i1";
    }
}
