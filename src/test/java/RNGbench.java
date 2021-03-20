import org.junit.jupiter.api.Test;
import tools.Benchmarker;
import tools.RandomGen;

public class RNGbench {
    @Test
    void BenchSequenceGenerators() {
        Benchmarker B = new Benchmarker();
        RandomGen rng = new RandomGen();
        final int N = 10000;
        System.out.printf("Simple Seq: %7d ns\n", AverageRuntime(B, N, () -> rng.GetSimpleSequence(0, 1000)));
        System.out.printf("Random Seq: %7d ns\n", AverageRuntime(B, N, () -> rng.GetRandomSequenceShuffled(0, 1000, 1000)));
        System.out.println("Distinct Value Sequences");
        System.out.printf("Looped Seq: %7d ns\n", AverageRuntime(B, N, () -> rng.GetDistinctSequenceShuffled(0, 1000, 1000)));
        System.out.printf("Stream Seq: %7d ns\n", AverageRuntime(B, N, () -> rng.DEPRECATED_GetDistinctSequenceShuffled(0, 1000, 1000)));
    }

    long AverageRuntime(Benchmarker B, int N, Runnable fn){
        long total = 0;
        for(int i = 0; i < N; ++i){
            B.Start();
            fn.run();
            B.Stop();
            total += B.ElapsedNano();
        }
        return total / N;
    }
}
