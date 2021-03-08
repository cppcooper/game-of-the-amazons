package algorithms.analysis;

import structures.LocalState;
import structures.Position;

import java.util.LinkedList;
import java.util.Queue;

public class Heuristics {
	protected static class CountingData {
		public int[] visited = new int[121];
		public Queue<Integer> blankspace = new LinkedList<>();
		public Queue<Integer> blockedspace = new LinkedList<>();
	}

	public static class CountValues {
		public int blanks;
		public int blocks;
		public double blocks_heuristic;
	}

	public static CountValues GetCount(LocalState board, int startingPos) { //countType is either "blank" for blank spaces, or "blocked" for blocked spaces
		CountValues counts = new CountValues();
		CountingData data = new CountingData();
		Position start = new Position(startingPos);

		data.visited[startingPos] = startingPos;
		QueueNeighbours(data, startingPos, board);

		if (!data.blankspace.isEmpty()) {
			while (!data.blankspace.isEmpty()) {
				int value = data.blankspace.poll();
				counts.blanks++;
				QueueNeighbours(data, value, board);
			}
		}

		if (!data.blockedspace.isEmpty()) {
			while (!data.blockedspace.isEmpty()) {
				int value = data.blockedspace.poll();
				Position current = new Position(value);
				counts.blocks++;

				//calculate simple distance
				int max = Math.max(Math.abs(start.x - current.x),  Math.abs(start.y - current.y));

				switch(max){
					case 1:
						counts.blocks_heuristic += 10;
						break;
					case 2:
						counts.blocks_heuristic += 1;
						break;
					case 3:
						counts.blocks_heuristic += 0.1;
						break;
					default:
						counts.blocks_heuristic += 0.01;
						break;
				}

			}
		}

		return counts;

	}

	protected static void QueueNeighbours(CountingData data, int index, LocalState board){
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
				if (data.visited[neighbour] == 0) {
					data.visited[neighbour] = neighbour;
					if (board.ReadTile(neighbour) == 0) {
						data.blankspace.offer(neighbour);
					} else {
						data.blockedspace.offer(neighbour);
					}
				}
			}
		}
	}
}
