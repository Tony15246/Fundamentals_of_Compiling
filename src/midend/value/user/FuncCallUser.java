package midend.value.user;

import midend.value.FuncValue;
import midend.value.TempValue;
import midend.value.Value;

import java.util.ArrayList;

public class FuncCallUser extends User{
    private final FuncValue funcValue;
    private final ArrayList<Value> params;	// 参数列表
    private final TempValue retValue;	// 返回值

    public FuncCallUser(FuncValue funcValue, ArrayList<Value> params) {
        this.funcValue = funcValue;
        this.params = params;
        this.retValue = null;
    }

    public FuncCallUser(FuncValue funcValue, ArrayList<Value> params, TempValue retValue) {
        this.funcValue = funcValue;
        this.params = params;
        this.retValue = retValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value param : params) {
            sb.append("i32 ").append(param.toString()).append(", ");
        }
        if (!sb.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        if (retValue == null) {
            return "call void @" + funcValue.getToken().value + "(" + sb + ")";
        } else {
            return retValue + " = call i32 @" + funcValue.getToken().value + "(" + sb + ")";
        }
    }
}
