package midend.value.user;

import midend.value.TempValue;
import midend.value.Value;

public class AllocUser extends User{
    private final TempValue pointer;

    public AllocUser(TempValue pointer){
        this.pointer = pointer;
    }

    @Override
    public String toString(){
        return pointer.toString() + " = alloca " + pointer.getValue().getType();
    }
}
