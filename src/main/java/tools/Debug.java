package tools;

import java.util.concurrent.atomic.AtomicBoolean;

public class Debug {
    public static AtomicBoolean ZeroEdgesDetected = new AtomicBoolean(false);
    public static AtomicBoolean NoIndexFound = new AtomicBoolean(false);
    public static AtomicBoolean NoParentNodeFound = new AtomicBoolean(false);
    private static boolean debugging_level1 = false;
    private static boolean debugging_level2 = true;
    private static boolean debugging_level3 = true;
    private static boolean debugging_level4 = false;

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

    final public static int[] late_state = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 3, 1, 3, 2, 3, 0, 3, 3, 3,
            0, 3, 3, 0, 0, 0, 3, 0, 0, 3, 3,
            0, 3, 3, 0, 2, 0, 3, 0, 0, 3, 3,
            0, 3, 0, 3, 3, 0, 2, 0, 0, 3, 3,
            0, 3, 0, 0, 3, 3, 0, 0, 0, 3, 3,
            0, 3, 3, 0, 0, 0, 3, 0, 3, 3, 3,
            0, 3, 3, 1, 0, 0, 3, 0, 0, 3, 3,
            0, 3, 3, 3, 3, 2, 3, 1, 0, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};

    final public static int[] test_state_black_winning = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 0, 1, 3, 1, 2, 3, 3, 3, 3,
            0, 3, 0, 1, 3, 1, 2, 3, 3, 3, 3,
            0, 3, 0, 0, 3, 0, 0, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 2, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 2, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
    final public static int[] test_state_black_clear_advantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 3, 2, 0, 0, 2, 0, 0, 0,
            0, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0,
            0, 2, 0, 0, 3, 0, 0, 3, 0, 3, 2,
            0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 0,
            0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 1,
            0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final public static int[] test_state_black_advantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
            0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 3, 0, 0, 3, 0, 0, 2,
            0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
    final public static int[] test_state_black_disadvantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 3, 1, 0, 0, 0, 1, 3, 0, 1,
            0, 3, 0, 0, 0, 3, 0, 0, 3, 0, 0,
            0, 3, 3, 2, 0, 0, 0, 0, 0, 3, 3,
            0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0,
            0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final public static int[] test_state_black_clear_disadvantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 3,
            0, 0, 3, 3, 0, 3, 0, 0, 3, 3, 0,
            0, 1, 3, 0, 3, 3, 0, 3, 0, 3, 1,
            0, 3, 0, 0, 0, 0, 2, 0, 0, 3, 0,
            0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 2,
            0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    final public static int[] test_state_white_winning = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 0, 2, 3, 1, 2, 3, 3, 3, 3,
            0, 3, 0, 2, 3, 1, 2, 3, 3, 3, 3,
            0, 3, 0, 0, 3, 0, 0, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
    final public static int[] test_state_white_clear_advantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 3, 1, 0, 0, 1, 0, 0, 0,
            0, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0,
            0, 1, 0, 0, 3, 0, 0, 3, 0, 3, 1,
            0, 0, 0, 0, 0, 0, 2, 0, 0, 3, 0,
            0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 2,
            0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final public static int[] test_state_white_advantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 3, 0, 0, 3, 0, 0, 1,
            0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0,
            0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0};
    final public static int[] test_state_white_disadvantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 3, 2, 0, 0, 0, 2, 3, 0, 2,
            0, 3, 0, 0, 0, 3, 0, 0, 3, 0, 0,
            0, 3, 3, 1, 0, 0, 0, 0, 0, 3, 3,
            0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0,
            0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final public static int[] test_state_white_clear_disadvantage = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 3,
            0, 0, 3, 3, 0, 3, 0, 0, 3, 3, 0,
            0, 2, 3, 0, 3, 3, 0, 3, 0, 3, 2,
            0, 3, 0, 0, 0, 0, 1, 0, 0, 3, 0,
            0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 1,
            0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
}
