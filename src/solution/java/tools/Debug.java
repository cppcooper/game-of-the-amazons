package tools;

import java.util.concurrent.atomic.AtomicBoolean;

public class Debug {
    public static AtomicBoolean ZeroEdgesDetected = new AtomicBoolean(false);
    public static AtomicBoolean NoIndexFound = new AtomicBoolean(false);
    public static AtomicBoolean NoParentNodeFound = new AtomicBoolean(false);
    private static boolean debug_info_L1 = true;
    private static boolean debug_info_L2 = true;
    private static boolean debug_info_L3 = false;
    private static boolean debug_verbose_L1 = false;
    private static boolean debug_verbose_L2 = false;
    private static boolean debug_verbose_L3 = false;

    public static void DebugBreakPoint(){
        if(ZeroEdgesDetected.get() || NoIndexFound.get() || NoParentNodeFound.get()){
            System.out.println("debug breakpoint reached");
        }
    }
    public static void PrintThreadID(String method_name){
        System.out.printf("Thread: %s - %s\n", Thread.currentThread().getName(),method_name);
    }
    public static void RunInfoL1DebugCode(Runnable fn){
        if(debug_info_L1){
            fn.run();
        }
    }
    public static void RunInfoL2DebugCode(Runnable fn){
        if(debug_info_L2){
            fn.run();
        }
    }
    public static void RunInfoL3DebugCode(Runnable fn){
        if(debug_info_L3){
            fn.run();
        }
    }
    public static void RunVerboseL1DebugCode(Runnable fn){
        if(debug_verbose_L1){
            fn.run();
        }
    }
    public static void RunVerboseL2DebugCode(Runnable fn){
        if(debug_verbose_L2){
            fn.run();
        }
    }
    public static void RunVerboseL3DebugCode(Runnable fn){
        if(debug_verbose_L3){
            fn.run();
        }
    }

    final public static int[] no_moves_state = {
            3, 3, 0, 0, 3, 0, 3, 0, 3, 3,
            3, 0, 3, 3, 3, 3, 3, 0, 3, 3,
            3, 3, 3, 2, 3, 3, 2, 3, 3, 2,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 0, 3, 3, 3, 0, 3, 3,
            3, 1, 3, 0, 0, 3, 1, 3, 3, 3,
            3, 3, 3, 3, 0, 3, 3, 0, 3, 2,
            3, 0, 3, 0, 0, 0, 0, 3, 3, 3,
            3, 3, 0, 3, 3, 3, 3, 3, 3, 1,
            0, 3, 3, 3, 3, 1, 3, 0, 3, 3};

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

    final public static int[] test_state_alpha_debug = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
            0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0,
            0, 1, 3, 0, 0, 0, 0, 0, 3, 0, 0,
            0, 3, 3, 3, 3, 3, 3, 0, 3, 0, 0,
            0, 0, 3, 0, 0, 0, 3, 0, 3, 0, 0,
            0, 0, 3, 0, 1, 0, 3, 1, 3, 0, 0};

    final public static int[] test_state_black_good_opening = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 0, 0, 3, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0};
    final public static int[] test_state_white_good_opening = {
            //this is upside down compared to the GUI
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 2, 0, 0, 3, 0, 0, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0};

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
