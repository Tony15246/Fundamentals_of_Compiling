package midend.value.user;

import midend.value.Value;

public class StoreUser extends User {
    private final Value srcValue;
    private final Value dstValue;

    public StoreUser(Value srcValue, Value dstValue) {
        this.srcValue = srcValue;
        this.dstValue = dstValue;
    }

    @Override
    public String toString() {
        return "store " + srcValue.getType() + " " + srcValue.toString() + ", " + dstValue.getType() + " " + dstValue.toString();
    }
}
