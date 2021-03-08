

import org.junit.jupiter.api.Test;
import structures.BoardPiece;
import structures.LocalState;
import tools.Benchmarker;
import tools.RandomGen;

import java.util.ArrayList;

class MoveCompilerTester {
    @Test
    void TestScanMoves() {
        RandomGen rng = new RandomGen();
        ArrayList<Integer> arr = rng.GetRandomState();
        try {
            LocalState board = new LocalState(arr,false);
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}