package tools;

public class Benchmarker {
    private long start = -1;
    private long end = -1;
    private boolean running;
    public void start(){
        start = System.nanoTime();
        running = true;
    }
    public void stop(){
        end = System.nanoTime();
        running = false;
    }
    public long elapsed(){
        if(!running){
            return (end - start)/1000000;
        }
        return (System.nanoTime() - start)/1000000;
    }
    public long elapsedNano(){
        if(!running){
            return (end - start);
        }
        return (System.nanoTime() - start);
    }

    public long averageRuntime(Benchmarker B, int N, Runnable fn){
        long total = 0;
        for(int i = 0; i < N; ++i){
            B.start();
            fn.run();
            B.stop();
            total += B.elapsedNano();
        }
        return total / N;
    }
}
