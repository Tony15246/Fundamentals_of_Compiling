package midend.value.user;

import midend.value.Value;

public class StoreUser extends User{
    private final Value srcValue;
    private final Value dstValue;

    public StoreUser(Value srcValue, Value dstValue){
        this.srcValue = srcValue;
        this.dstValue = dstValue;
    }

    @Override
    public String toString(){
        //todo: 这里只考虑了int类型的参数
        return "store i32 " + srcValue.toString() + ", i32* " + dstValue.toString();
    }
}
