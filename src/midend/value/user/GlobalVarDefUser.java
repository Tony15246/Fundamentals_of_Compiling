package midend.value.user;

import midend.value.Value;
import midend.value.VarValue;

public class GlobalVarDefUser extends User{
    private final Value globalVar;

    public GlobalVarDefUser(Value globalVar) {
        this.globalVar = globalVar;
    }

    @Override
    public String toString() {
        if (globalVar instanceof VarValue varValue) {
            if (varValue.isConst()) {
                return varValue.getPointer().toString() + " = constant i32 " + varValue.getValue();
            } else {
                return varValue.getPointer().toString() + " = global i32 " + varValue.getValue();
            }
        } else {
            throw new RuntimeException("no support for array yet");
        }
    }
}
