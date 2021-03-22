package algorithms.analysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import structures.LocalState;
import structures.Position;


public class HeuristicsTest {

	@Test
	void TestHeuristics() {
		// 0 is open space
		// 1 is player 1 pieces
		// 2 is player 2 pieces
		// 3 is blocked space

		int[] state = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 2, 3, 0, 2, 0, 0, 0,
				0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0,
				0, 0, 3, 0, 0, 3, 3, 3, 3, 0, 0,
				0, 2, 0, 3, 0, 0, 0, 0, 3, 3, 2,
				0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 3,
				0, 0, 0, 0, 3, 0, 1, 0, 0, 3, 3,
				0, 3, 0, 3, 0, 0, 0, 3, 3, 3, 3,
				0, 1, 3, 3, 0, 0, 0, 3, 0, 0, 1,
				0, 0, 0, 0, 3, 0, 0, 3, 0, 0, 0,
				0, 0, 0, 1, 3, 3, 0, 3, 0, 0, 0};

		LocalState board = new LocalState(state);
		board.DebugPrint();

		int index = Position.CalculateIndex(6,6); //player 1's central position
		Heuristics.CountData info = Heuristics.GetCount(board, index);
		int[] positions = {index};
		int[][] first_degree_territory = MoveCompiler.GetOpenPositions(board,positions);
		int first_degree = 0;
		for(int x : first_degree_territory[0]){
			if(x < 0){
				break;
			}
			first_degree++;
		}
		System.out.println("Single piece counting heuristics..");
		System.out.printf("empty (1st degree): %d\n", first_degree);
		System.out.printf("empty: %d\n",info.our_empty);
		System.out.printf("nonempty: %d\n", info.our_nonempty);
		//System.out.printf("raw empty heuristic: %.1f\n", info.empty_heuristic);
		System.out.printf("raw nonempty heuristic: %.1f\n", info.our_nonempty_weighted);
		System.out.println("#################\nScaled heuristics");
		//System.out.printf("empty: %.3f\nnonempty: %.3f\n", info.empty_heuristic/161, info.nonempty_heuristic/80);

		assertEquals(42, info.our_empty);
		assertEquals(32, info.our_nonempty);
		//assertEquals(true, Precision.equals(15.475,info.nonempty_heuristic,0.001));
	}
}
