import java.util.ArrayList;
import structures.LocalState;
import algorithms.analysis.Heuristics;

import org.junit.jupiter.api.Test;

//todo: refactor test class to use Heuristics class's code (directly)
public class search_test {

	// These are the methods that the Heuristics class uses. It's kinda useful if you 
	// want to see what's going on with the board when you use the methods, but delete this if you want.

	//todo: revise to not have main() but instead use junit test. See AnalysisTest for an example
	public static void main(String[] args) throws Exception {

		// 0 is open space
		// 1 is player 1 pieces
		// 2 is player 2 pieces
		// 3 is blocked space

		int[] board = {
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
		//todo: convert board to ArrayList<Integer> then construct LocalState with it

		//Creates new LocalState, sets LocalState board to board shown above
		ArrayList<Integer> newBoard = new ArrayList<>();
		for (int val: board) {
			newBoard.add(val);
		}
		LocalState state = new LocalState(newBoard, true);

		// This prints out the board
		int count1 = 0;
		for (int x = 0; x < board.length; x++) {
			int val = state.ReadTile(x);
			System.out.print(val + " ");
			count1++;
			if (count1 == 11) {
				System.out.println();
				count1 = 0;
			}
		}


		int index = 72; //position 6,6

		Heuristics.CountData info = Heuristics.GetCount(state, index);

		System.out.println(info.blank);
		System.out.println(info.blocked);
		System.out.println(info.block_heuristic);

	}
}
