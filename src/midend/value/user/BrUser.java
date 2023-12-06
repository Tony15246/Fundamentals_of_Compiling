package midend.value.user;

import midend.value.Value;

public class BrUser {
    private final Value cond;
    private final BasicBlockUser ifTrue;
    private final BasicBlockUser ifFalse;
    private final BasicBlockUser dest;

    public BrUser(BasicBlockUser dest) {
        this.cond = null;
        this.ifTrue = null;
        this.ifFalse = null;
        this.dest = dest;
    }

    public BrUser(Value cond, BasicBlockUser ifTrue, BasicBlockUser ifFalse) {
        this.cond = cond;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
        this.dest = null;
    }

    @Override
    public String toString() {
        if (dest != null) {
            return "br label %" + dest.getLabel();
        } else if (cond != null && ifTrue != null && ifFalse != null) {
            return "br i1 " + cond + ", label %" + ifTrue.getLabel() + ", label %" + ifFalse.getLabel();
        } else {
            throw new RuntimeException("Invalid br instruction");
        }
    }
}
