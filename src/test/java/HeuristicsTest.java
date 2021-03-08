import java.util.ArrayList;
import java.util.Arrays;

import structures.LocalState;
import algorithms.analysis.Heuristics;

//todo: refactor test class to use Heuristics class's code (directly)
public class HeuristicsTest {

	// These are the methods that the Heuristics class uses. It's kinda useful if you 
	// want to see what's going on with the board when you use the methods, but delete this if you want.

	//todo: revise to not have main() but instead use junit test. See AnalysisTest for an example
	public static void main(String[] args) throws Exception {

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
		LocalState state = new LocalState(new ArrayList<Integer>(Arrays.asList(board)), true);
		state.DebugPrint();

		int index = 72; //position 6,6

		Heuristics.CountData info = Heuristics.GetCount(state, index);

		System.out.println(info.blanks);
		System.out.println(info.blocks);
		System.out.println(info.blocks_heuristic);

	}
}
