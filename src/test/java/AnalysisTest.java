

import org.junit.jupiter.api.Test;
import structures.BoardPiece;
import structures.LocalState;
import tools.Benchmarker;

import java.util.ArrayList;
import java.util.Random;

class AnalysisTest {
    @Test
    void TestScanMoves() {
        ArrayList<Integer> arr = new ArrayList<>(121);
        Random RNG = new Random();
        double threshold = RNG.nextDouble() * 0.75;
        for(int i = 0; i < 121; ++i){
            if(RNG.nextDouble() < threshold){
                int v = RNG.nextInt();
                if(v == 1 || v == 2){
                    arr.add(3);
                } else {
                    arr.add(v);
                }
            } else {
                arr.add(0);
            }
        }
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