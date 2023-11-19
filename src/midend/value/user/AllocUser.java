package midend.value.user;

import midend.value.Value;

public class AllocUser extends User{
    private Value outputValue;

    public AllocUser(Value outputValue){
        this.outputValue = outputValue;
    }

    @Override
    public String toString(){
        //todo: 这里只考虑了int类型的参数
        return outputValue.toString() + " = alloca i32";
    }
}
