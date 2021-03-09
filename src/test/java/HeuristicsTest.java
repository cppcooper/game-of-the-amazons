import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import structures.LocalState;
import algorithms.analysis.Heuristics;


public class HeuristicsTest {

	@Test
	void TestHeuristics() {
		// 0 is open space
		// 1 is player 1 pieces
		// 2 is player 2 pieces
		// 3 is blocked space

		Integer[] board = {
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


		//Creates new LocalState, sets LocalState board to board shown above
		LocalState state = new LocalState(new ArrayList<Integer>(Arrays.asList(board)), true, false);
		state.DebugPrint();

		int index = 72; //position 6,6
		
		try {
			
			//Creates new LocalState, sets LocalState board to board shown above
			LocalState state = new LocalState(new ArrayList<Integer>(Arrays.asList(board)), true);
			state.DebugPrint();
			Heuristics.CountData info = Heuristics.GetCount(state, index);

			System.out.println(info.blanks);
			System.out.println(info.blocks);
			System.out.println(info.blocks_heuristic);
			
			assertEquals(42, info.blanks);
			assertEquals(32, info.blocks);
			assertEquals(15.529999999999992, info.blocks_heuristic);
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
