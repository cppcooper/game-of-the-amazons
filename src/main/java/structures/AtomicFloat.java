package structures;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicFloat {
    private AtomicInteger value = new AtomicInteger(Float.floatToIntBits(0.f));
    public float get(){
        return Float.intBitsToFloat(value.get());
    }
    public void set(float new_value){
        value.set(Float.floatToIntBits(new_value));
    }
}
