package algorithms.search;

import data.structures.GameState;
import data.structures.GameTreeNode;
import org.junit.jupiter.api.Test;
import tools.Benchmarker;
import main.AICore;

public class MonteCarloTester {
    @Test
    void execution_time(){
        Benchmarker B = new Benchmarker();
        GameState board = new GameState();
        AICore.SetState(board);
        board.FindPieces();
        GameTreeNode sim_root = new GameTreeNode(null,null, board);
        B.Start();
        MonteCarlo.RunSimulation(board, sim_root, true);
        System.out.printf("Monte Carlo simulation took %d ms\n", B.Elapsed());
    }
}
