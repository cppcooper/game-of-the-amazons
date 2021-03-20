package tools;

import org.junit.jupiter.api.Test;

class RandomGenTest {
    static RandomGen rng = new RandomGen();

    @Test
    void DEPRECATED_GetDistinctSequenceShuffled() {
        rng.DEPRECATED_GetDistinctSequenceShuffled(0,1000,1001);
        System.out.println("1 test done");
        rng.DEPRECATED_GetDistinctSequenceShuffled(0,1000,100);
        System.out.println("1 test done");
        rng.DEPRECATED_GetDistinctSequenceShuffled(0,100,1000);
        System.out.println("1 test done");
    }

    @Test
    void getSimpleSequence() {
        rng.GetNumberSequence(0,1000);
        System.out.println("1 test done");
    }

    @Test
    void getRandomSequenceShuffled() {
        rng.GetRandomSequenceShuffled(0,1000,1001);
        System.out.println("1 test done");
        rng.GetRandomSequenceShuffled(0,1000,100);
        System.out.println("1 test done");
        rng.GetRandomSequenceShuffled(0,100,1000);
        System.out.println("1 test done");
    }

    @Test
    void getDistinctSequenceShuffled() {
        rng.GetDistinctSequenceShuffled(0,1000,1001);
        System.out.println("1 test done");
        rng.GetDistinctSequenceShuffled(0,1000,100);
        System.out.println("1 test done");
        rng.GetDistinctSequenceShuffled(0,100,1000);
        System.out.println("1 test done");
    }
}