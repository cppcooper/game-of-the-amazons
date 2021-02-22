public class Benchmarker {
    private long start = -1;
    private long end = -1;
    private boolean running;
    public void Start(){
        start = System.nanoTime();
        running = true;
    }
    public void Stop(){
        end = System.nanoTime();
        running = false;
    }
    public long Elapsed(){
        if(!running){
            return (end - start)/1000000;
        }
        return (System.nanoTime() - start)/1000000;
    }
    public long TestCode(Runnable code){
        Start();
        code.run();
        Stop();
        return Elapsed();
    }
}
