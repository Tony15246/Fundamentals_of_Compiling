package midend.value.user;

import midend.value.Value;

public class IcmpUser extends User{
    Value outputValue;
    Value leftValue;
    Value rightValue;
    String cond;

    public IcmpUser(Value outputValue, Value leftValue, Value rightValue, String cond) {
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
