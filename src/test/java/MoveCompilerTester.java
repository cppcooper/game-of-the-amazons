

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
        BoardPiece[] positions = new BoardPiece[100];
        int i = 0;
        for(int x = 1; x < 11; ++x){
            for(int y = 1; y < 11; ++y){
                positions[i++] = new BoardPiece(x,y,1);
            }
        }
        ArrayList<Long> times = new ArrayList<>(25);
        long total = 0;
        for(i = 0; i < 25; ++i){
            times.add(RandomizedMoveCompilerTest(positions));
            System.out.printf("run #%d: %d ns\n",i+1,times.get(i));
            total += times.get(i);
        }
        System.out.printf("Average: %d ns\n", total/times.size());
    }

    long RandomizedMoveCompilerTest(BoardPiece[] pieces){
        RandomGen rng = new RandomGen();
        LocalState board = new LocalState(rng.GetRandomState(),false,false);
        board.SetTile(3,3,1);
        board.SetTile(4,4,1);
        board.SetTile(6,5,1);
        board.SetTile(5,5,1);

        board.SetTile(7,7,2);
        board.SetTile(8,8,2);
        board.SetTile(4,5,2);
        board.SetTile(5,4,2);
        board.FindPieces();
        BoardPiece p = new BoardPiece(4,4,2);
        Benchmarker B = new Benchmarker();
        B.Start();
        MoveCompiler.GetMoveList(board,pieces);
        B.Stop();
        return B.ElapsedNano();
    }
}