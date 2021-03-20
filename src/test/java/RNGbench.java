import org.junit.jupiter.api.Test;
import tools.Benchmarker;
import tools.RandomGen;

public class RNGbench {
    @Test
    void BenchSequenceGenerators() {
        Benchmarker B = new Benchmarker();
        RandomGen rng = new RandomGen();
        final int N = 10000;
        System.out.printf("Random Seq: %7d ns\n", B.AverageRuntime(B, N, () -> rng.GetRandomSequenceShuffled(0, 1000, 1000)));
        System.out.println("Distinct Value Sequences");
        System.out.printf("Number Seq: %7d ns\n", B.AverageRuntime(B, N, () -> rng.GetNumberSequence(0, 1000)));
        System.out.printf("Looped Seq: %7d ns\n", B.AverageRuntime(B, N, () -> rng.GetDistinctSequenceShuffled(0, 1000, 1000)));
        System.out.printf("Stream Seq: %7d ns\n", B.AverageRuntime(B, N, () -> rng.DEPRECATED_GetDistinctSequenceShuffled(0, 1000, 1000)));
    }


}
