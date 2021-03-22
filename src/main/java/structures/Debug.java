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
