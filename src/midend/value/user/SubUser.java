package midend.value.user;

import midend.value.Value;

public class SubUser extends User{
    private final Value leftValue;
    private final Value rightValue;
    private final Value outputValue;

    public SubUser(Value leftValue, Value rightValue, Value outputValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.outputValue = outputValue;
    }

    @Override
    public String toString() {
        return outputValue.toString() + " = sub i32 " + leftValue.toString() + ", " + rightValue.toString();
    }
}
