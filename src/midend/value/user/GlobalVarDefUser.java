package midend.value.user;

import midend.value.OneDimensionArrayValue;
import midend.value.TwoDimensionArrayValue;
import midend.value.Value;
import midend.value.VarValue;

public class GlobalVarDefUser extends User{
    private final Value globalVar;

    public GlobalVarDefUser(Value globalVar) {
        this.globalVar = globalVar;
    }

    @Override
    public String toString() {
        switch (globalVar) {
            case VarValue varValue -> {
                if (varValue.isConst()) {
                    return varValue.getPointer().toString() + " = constant i32 " + varValue.getValue();
                } else {
                    return varValue.getPointer().toString() + " = global i32 " + varValue.getValue();
                }
            }
            case OneDimensionArrayValue oneDimensionArrayValue -> {
                if (oneDimensionArrayValue.isConst()) {
                    return oneDimensionArrayValue.getPointer().toString() + " = constant " + oneDimensionArrayValue.getValuesString();
                } else {
                    return oneDimensionArrayValue.getPointer().toString() + " = global " + oneDimensionArrayValue.getValuesString();
                }
            }
            case TwoDimensionArrayValue twoDimensionArrayValue -> {
                if (twoDimensionArrayValue.isConst()) {
                    return twoDimensionArrayValue.getPointer().toString() + " = constant " + twoDimensionArrayValue.getValuesString();
                } else {
                    return twoDimensionArrayValue.getPointer().toString() + " = global " + twoDimensionArrayValue.getValuesString();
                }
            }
            default -> throw new RuntimeException("unknown global var type or null");
        }
    }
}
