package structures;

import java.util.concurrent.atomic.AtomicBoolean;

public class Debug {
    public static AtomicBoolean ZeroEdgesDetected = new AtomicBoolean(false);
    public static AtomicBoolean NoIndexFound = new AtomicBoolean(false);
    public static AtomicBoolean NoParentNodeFound = new AtomicBoolean(false);
    private static boolean debugging_level1 = false;
    private static boolean debugging_level2 = true;
    private static boolean debugging_level3 = false;
    private static boolean debugging_level4 = true;

    public static void DebugBreakPoint(){
        if(ZeroEdgesDetected.get() || NoIndexFound.get() || NoParentNodeFound.get()){
            System.out.println("debug breakpoint reached");
        }
    }
    public static void PrintThreadID(String method_name){
        System.out.printf("Thread: %s - %s\n", Thread.currentThread().getName(),method_name);
    }
    public static void RunLevel1DebugCode(Runnable fn){
        if(debugging_level1){
            fn.run();
        }
    }
    public static void RunLevel2DebugCode(Runnable fn){
        if(debugging_level2){
            fn.run();
        }
    }
    public static void RunLevel3DebugCode(Runnable fn){
        if(debugging_level3){
            fn.run();
        }
    }
    public static void RunLevel4DebugCode(Runnable fn){
        if(debugging_level4){
            fn.run();
        }
    }
}
