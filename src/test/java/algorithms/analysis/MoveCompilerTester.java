package algorithms.analysis;

import org.junit.jupiter.api.Test;
import data.BoardPiece;
import data.GameState;
import data.MovePool;
import data.Position;
import tools.Benchmarker;
import tools.RandomGen;

class MoveCompilerTester {
    @Test
    void BenchmarkGetMoveList() {
        //This isn't a full picture, as it does not involve building the GameTree
        final int trials = 64000;
        final boolean print_intermediaries = false;
        Benchmarker B = new Benchmarker();
        B.Start();
        MovePool.generate_pool();
        B.Stop();
        System.out.printf("Pool generation took: %d ms", B.Elapsed());
        BenchmarkGetPooledMoveList(trials,print_intermediaries);
        BenchmarkGetNonPooledMoveList(trials,print_intermediaries);
        double x = -1.0;
        for(int i = 0; i < (trials + trials); ++i){
            x += i;
            x = x / (x-1);
        }
        System.out.println(x);
        BenchmarkGetNonPooledMoveList(100,print_intermediaries);
        BenchmarkGetPooledMoveList(100,print_intermediaries);
        BenchmarkGetPooledMoveList(trials,print_intermediaries);
        BenchmarkGetNonPooledMoveList(trials,print_intermediaries);
    }

    void BenchmarkGetPooledMoveList(int trials, boolean print_runs){
        long total = 0;
        RandomGen rng = new RandomGen();
        System.out.printf("\npooling with %d randomized trials\n===================\n", trials);
        for(int i = 0; i < trials; ++i){
            rng.setSeed(i);
            long time = RandomizedMoveCompilerTest(rng.GetRandomPositions(4),rng,true);
            if(print_runs) {
                System.out.printf("(%d) %.1g ns, ", i + 1, (float) time);
            }
            total += time;
        }
        if(print_runs){
            System.out.println();
        }
        System.out.printf("Average: %.2g ns\n", (float)(total/trials));
    }

    void BenchmarkGetNonPooledMoveList(int trials, boolean print_runs){
        long total = 0;
        RandomGen rng = new RandomGen();
        System.out.printf("\nnon-pooling with %d randomized trials\n===================\n", trials);
        for(int i = 0; i < trials; ++i){
            rng.setSeed(i);
            long time = RandomizedMoveCompilerTest(rng.GetRandomPositions(4),rng,false);
            if(print_runs) {
                System.out.printf("(%d) %.1g ns, ", i + 1, (float) time);
            }
            total += time;
        }
        if(print_runs){
            System.out.println();
        }
        System.out.printf("Average: %.2g ns\n", (float)(total/trials));
    }

    long RandomizedMoveCompilerTest(Position[] positions, RandomGen rng, boolean use_pooling){
        GameState board = new GameState(rng.GetRandomState(0.35),false,false);
        BoardPiece[] pieces = new BoardPiece[4];
        for(int i = 0; i < 4; ++i){
            pieces[i] = new BoardPiece(positions[i].CalculateIndex(),1);
        }

        Benchmarker B = new Benchmarker();
        B.Start();
        MoveCompiler.GetMoveList(board,pieces,use_pooling);
        B.Stop();
        return B.ElapsedNano();
    }
}