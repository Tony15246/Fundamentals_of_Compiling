package midend.value.user;

import midend.value.Value;

public class GetintUser extends User{
    private final Value outputValue;

    public GetintUser(Value outputValue){
        this.outputValue = outputValue;
    }

    @Override
    public String toString(){
        return outputValue.toString() + " = call i32 @getint()";
    }
}
