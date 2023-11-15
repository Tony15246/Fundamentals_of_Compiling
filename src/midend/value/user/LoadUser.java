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
        return outputValue.toString() + " = load i32, i32* " + srcValue.toString() + ", align 4";
    }
}
