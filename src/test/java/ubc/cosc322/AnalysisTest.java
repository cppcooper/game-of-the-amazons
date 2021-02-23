package ubc.cosc322;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

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

            //System.out.println("Threaded: " + B.TestCode(()->Analysis.ScanMovesThreaded(board,p.pos)) + " ns"); //exception caught inside function
            System.out.println("Non threaded: " + B.TestCode(()->Analysis.ScanMoves(board,p.pos)) + " ns");

            System.out.println("Threaded: " + B.TestCode(() -> {
                try {
                    Analysis.GetMoveListThreaded(board, board.GetP1Pieces());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }) + " ns");
            System.out.println("Non threaded: " + B.TestCode(() -> Analysis.GetMoveList(board, board.GetP1Pieces())) + " ns");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}