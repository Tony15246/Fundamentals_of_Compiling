package midend.value.user;

import midend.value.Value;

public class RetUser extends User {
    private final Value retValue;

    public RetUser() {
        this.retValue = null;
    }

    public RetUser(Value retValue) {
        this.retValue = retValue;
    }

    @Override
    public String toString() {
        if (retValue == null) {
            return "ret void";
        } else {
            return "ret i32 " + retValue;
        }
    }
}
