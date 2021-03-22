package structures;

import java.util.concurrent.atomic.AtomicBoolean;

public class Debug {
    public static AtomicBoolean ZeroEdgesDetected = new AtomicBoolean(false);
    public static AtomicBoolean NoIndexFound = new AtomicBoolean(false);
    public static AtomicBoolean NoParentNodeFound = new AtomicBoolean(false);
    private static boolean debugging = true;

    public static void DebugBreakPoint(){
        if(ZeroEdgesDetected.get() || NoIndexFound.get() || NoParentNodeFound.get()){
            System.out.println("debug breakpoint reached");
        }
    }
    public static void RunDebugCode(Runnable fn){
        if(debugging){
            fn.run();
        }
    }
}
