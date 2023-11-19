package midend.value.user;

import midend.value.VarValue;

public class GlobalVarDefUser extends User{
    private final VarValue globalVar;

    public GlobalVarDefUser(VarValue globalVar) {
        this.globalVar = globalVar;
    }

    @Override
    public String toString() {
        //todo: 这里只考虑了int类型的参数
        if (globalVar.isConst()) {
            return globalVar.getPointer().toString() + " = constant i32 " + globalVar.getValue();
        } else {
            return globalVar.getPointer().toString() + " = global i32 " + globalVar.getValue();
        }
    }
}
