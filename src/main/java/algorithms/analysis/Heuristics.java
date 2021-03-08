package algorithms.analysis;

import structures.LocalState;
import structures.Position;

import java.util.LinkedList;
import java.util.Queue;

public class Heuristics {

	public static class CountData {
		public int blank;
		public int blocked;
		public int block_heuristic;
	}

	private static Queue<Integer> blankspace = new LinkedList<>();
	private static Queue<Integer> blockedspace = new LinkedList<>();
	private static int[] visited = new int[121];

	//todo: refactor int[] board => LocalState board
	public static CountData GetCount(LocalState board, int startingPos) { //countType is either "blank" for blank spaces, or "blocked" for blocked spaces

		CountData counts = new CountData();
		int x = startingPos / 11;
		int y = startingPos - (x * 11);

		visited[startingPos] = startingPos;
		ProcessNeighbours(startingPos, board);

		if (!blankspace.isEmpty()) {
			while (!blankspace.isEmpty()) {
				int value = blankspace.poll();
				counts.blank++;
				ProcessNeighbours(value, board);
			}
		}

		if (!blockedspace.isEmpty()) {
			while (!blockedspace.isEmpty()) {
				int value = blockedspace.poll();
				int qx = value / 11;
				int qy = value - (qx * 11);
				counts.blocked++;

				int dx = Math.abs(x - qx);
				int dy = Math.abs(y - qy);
				int max = Math.max(dx,  dy);

				if (max == 1) {
					counts.block_heuristic += 100*counts.blocked;
				}else if (max == 2){
					counts.block_heuristic += 10*counts.blocked;
				}else {
					counts.block_heuristic += 1*counts.blocked;
				}

			}
		}

		return counts;

	}

	//todo: refactor method to utilize MoveCompiler class, you'll likely want ScanAllDirections or possibly the other. If you need to operate on the tiles as you iterate, then we can add another variant of those methods and use lambda's to accomplish the end goal
	// todo: Lambda????
	// merge GetNeightbours function into this one
	protected static void ProcessNeighbours(int index, LocalState board){
		Position[] neighbours = new Position[8];
		neighbours[0] = new Position(index - 1);
		neighbours[1] = new Position(index + 1);
		neighbours[2] = new Position(index - 12);
		neighbours[3] = new Position(index - 11);
		neighbours[4] = new Position(index - 10);
		neighbours[5] = new Position(index + 10);
		neighbours[6] = new Position(index + 11);
		neighbours[7] = new Position(index + 12);

		for(Position p : neighbours) {
			if (p.IsValid()) {
				int neighbour = p.CalculateIndex();
				if (visited[neighbour] == 0) {
					visited[neighbour] = neighbour;
					if (board.ReadTile(neighbour) == 0) {
						blankspace.offer(neighbour);
					} else {
						blockedspace.offer(neighbour);
					}
				}
			}
		}
	}
}
