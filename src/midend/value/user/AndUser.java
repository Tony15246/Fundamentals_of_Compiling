package midend.value.user;

import midend.value.Value;

public class AndUser extends User{
    private final Value outputValue;
    private final Value leftValue;
    private final Value rightValue;

    public AndUser(Value outputValue, Value leftValue, Value rightValue) {
        this.outputValue = outputValue;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    @Override
    public String toString() {
        return outputValue.toString() + " = and i1 " + leftValue.toString() + ", " + rightValue.toString();
    }
}
