

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
        RandomGen rng = new RandomGen();
        final int trials = 100;
        long total = 0;
        for(int i = 0; i < trials; ++i){
            long time = RandomizedMoveCompilerTest(GetRandomBoardPieces(rng),rng);
            System.out.printf("run #%d: %d ms\n",i+1,time);
            total += time;
        }
        System.out.printf("Average: %d ms\n", total/trials);
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

    long RandomizedMoveCompilerTest(BoardPiece[] pieces, RandomGen rng){
        LocalState board = new LocalState(rng.GetRandomState(0.35),false,false);
        BoardPiece p = new BoardPiece(4,4,2);
        Benchmarker B = new Benchmarker();
        B.Start();
        MoveCompiler.GetMoveList(board,pieces);
        B.Stop();
        return B.Elapsed();
    }
}