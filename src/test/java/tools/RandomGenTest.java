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

    @Test
    void probability_test(){
        final int trials = 1000000;
        int[] counts = new int[3];
        double[] p_values = new double[10];
        RandomGen rng = new RandomGen();
        for(int i = 0; i < trials; ++i){
            switch(rng.get_random_policy(90)){
                case MOBILITY:
                    counts[0]++;
                    break;
                case TERRITORY:
                    counts[1]++;
                    break;
                case ALL_HEURISTICS:
                    counts[2]++;
                    break;
                case WINNER_LOSER:
                    counts[3]++;
                    break;
                case DO_NOTHING:
                    counts[4]++;
                    break;
            }
        }
        for(int i = 0; i < counts.length; ++i){
            System.out.printf("counts[%d] = %d\n", i, counts[i]);
            p_values[i] = (double)counts[i] / trials;
        }
        for(int i = 0; i < counts.length; ++i) {
            System.out.printf("p_value[%d] = %.2f\n", i, p_values[i]);
        }
    }
}