package midend.value.user;

import midend.value.VarValue;

public class GlobalVarDefUser extends User{
    private final VarValue globalVar;

    public GlobalVarDefUser(VarValue globalVar) {
        this.globalVar = globalVar;
    }

    @Override
    public String toString() {
        if (globalVar.isConst()) {
            return globalVar.getPointer().toString() + " = constant i32 " + globalVar.getValue() + ", align 4";
        } else {
            return globalVar.getPointer().toString() + " = global i32 " + globalVar.getValue() + ", align 4";
        }
    }
}
