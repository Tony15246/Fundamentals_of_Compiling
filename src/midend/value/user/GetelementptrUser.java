package midend.value.user;

import midend.value.TempValue;
import midend.value.Value;

import java.util.ArrayList;

public class GetelementptrUser extends User{
    TempValue outPointer;
    Value pointer;
    ArrayList<Value> index;

    public GetelementptrUser(TempValue outPointer, Value pointer, ArrayList<Value> index){
        this.outPointer = outPointer;
        this.pointer = pointer;
        this.index = index;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(outPointer.toString());
        sb.append(" = getelementptr ");
        sb.append(pointer.getValue().getType());
        sb.append(", ");
        sb.append(pointer.getType());
        sb.append(" ");
        sb.append(pointer.toString());
        for (Value value : index) {
            sb.append(", ");
            sb.append(value.getType());
            sb.append(" ");
            sb.append(value);
        }
        return sb.toString();
    }
}
