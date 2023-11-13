package midend.value.user;

import midend.value.Value;

public class AllocUser extends User{
    private Value outputValue;

    public AllocUser(Value outputValue){
        this.outputValue = outputValue;
    }

    @Override
    public String toString(){
        return outputValue.toString() + " = alloca i32, align 4";
    }
}
