package structures;

import java.util.concurrent.atomic.AtomicBoolean;

public class Debug {
    public static AtomicBoolean ZeroEdgesDetected = new AtomicBoolean(false);
    public static AtomicBoolean NoIndexFound = new AtomicBoolean(false);
    public static AtomicBoolean NoParentNodeFound = new AtomicBoolean(false);
    private static boolean debugging_level1 = false;
    private static boolean debugging_level2 = true;
    private static boolean debugging_level3 = false;

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
    public static int[] GetAllPositions(){
        int[] positions = new int[100];
        int j = 0;
        for(int x = 1; x < 11; ++x){
            for(int y = 1; y < 11; ++y){
                Position p = new Position(x,y);
                if(p.IsValid()){
                    positions[j++] = p.CalculateIndex();
                }
            }
        }
        return positions;
    }
}
