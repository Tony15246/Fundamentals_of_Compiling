package midend.value.user;

import midend.value.Value;

public class LoadUser extends User{
    private final Value srcValue;
    private final Value outputValue;

    public LoadUser(Value srcValue, Value outputValue){
        this.srcValue = srcValue;
        this.outputValue = outputValue;
    }

    @Override
    public String toString(){
        //todo: 这里只考虑了int类型的参数
        return outputValue.toString() + " = load i32, i32* " + srcValue.toString();
    }
}
