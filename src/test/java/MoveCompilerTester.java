

import algorithms.analysis.MoveCompiler;
import org.junit.jupiter.api.Test;
import structures.BoardPiece;
import structures.LocalState;
import tools.Benchmarker;
import tools.RandomGen;

import java.util.ArrayList;

class MoveCompilerTester {
    @Test
    void BenchmarkGetMoveList() {
        //This isn't a full picture, as it does not involve building the GameTree
        final int trials = 64000;
        final boolean print_intermediaries = false;
        BenchmarkGetNonPooledMoveList(trials,print_intermediaries);
        BenchmarkGetPooledMoveList(trials,print_intermediaries);
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
            long time = RandomizedMoveCompilerTest(GetRandomBoardPieces(rng),rng,true);
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
            long time = RandomizedMoveCompilerTest(GetRandomBoardPieces(rng),rng,false);
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

    BoardPiece[] GetRandomBoardPieces(RandomGen rng){
        final int N = 4;
        BoardPiece[] positions = new BoardPiece[N];
        var X = rng.GetSequenceShuffled(1,11,N);
        var Y = rng.GetSequenceShuffled(1,11,N);
        for(int i = 0; i < N; ++i){
            positions[i] = new BoardPiece(X.get(i), Y.get(i),1);
        }
        return positions;
    }

    long RandomizedMoveCompilerTest(BoardPiece[] pieces, RandomGen rng, boolean use_pooling){
        LocalState board = new LocalState(rng.GetRandomState(0.35),false,false);
        BoardPiece p = new BoardPiece(4,4,2);
        Benchmarker B = new Benchmarker();
        B.Start();
        MoveCompiler.GetMoveList(board,pieces,use_pooling);
        B.Stop();
        return B.ElapsedNano();
    }
}