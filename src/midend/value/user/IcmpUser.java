package midend.value.user;

import midend.value.Value;

public class IcmpUser extends User{
    private final Value outputValue;
    private final Value leftValue;
    private final Value rightValue;
    private final String cond;

    public IcmpUser(Value outputValue, String cond, Value leftValue, Value rightValue) {
        this.outputValue = outputValue;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.cond = cond;
    }

    @Override
    public String toString() {
        return outputValue.toString() + " = icmp " + cond + " i32 " + leftValue.toString() + ", " + rightValue.toString();
    }
}
