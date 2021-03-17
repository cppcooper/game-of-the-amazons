package algorithms.analysis;

import org.apache.commons.math3.util.Pair;
import structures.BoardPiece;
import structures.GameTreeNode;
import structures.LocalState;
import structures.Position;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Heuristics {
	protected static class CountingAlgorithmData {
		public int[] visited = new int[121];
		public Queue<Integer> blankspace = new LinkedList<>();
		public Queue<Integer> blockedspace = new LinkedList<>();
	}

	public static class CountData {
		public int blanks;
		public int blocks;
		public double blocks_heuristic;
	}

	private static ConcurrentLinkedDeque<Pair<LocalState, GameTreeNode>> queue = new ConcurrentLinkedDeque();

	public static void ProcessQueue(){
		while(!Thread.currentThread().isInterrupted()){
			var pair = queue.poll();
			if(pair != null) {
				LocalState board = pair.getFirst();
				GameTreeNode node = pair.getSecond();
				BoardPiece[] pieces = board.GetPrevTurnPieces(); // we'll calculate heuristics for the player who got us here
				float node_heuristic = 0.f;
				float node_aggregate = Float.intBitsToFloat(node.aggregate.get());
				for (int i = 0; i < 4; ++i) {
					int index = pieces[i].pos.CalculateIndex();
					// todo (2): integrate other heuristics (once implemented)
					var heuristic_data = GetCount(board, index);
					node_heuristic += heuristic_data.blanks - heuristic_data.blocks_heuristic;
				}
				node.aggregate.set(Float.floatToIntBits(node_heuristic + node_aggregate));

				GameTreeNode parent = node.super_node;
				float parent_heuristic = Float.intBitsToFloat(parent.aggregate.get());
				node_heuristic = Float.intBitsToFloat(node.aggregate.get());
				float prev_value = 0;
				// todo (1): verify aggregation update logic
				// todo (2): consider changing the weighting of aggregation (currently 1:1 ratio; parent:child)
				while(parent != null){
					float temp = parent_heuristic;
					parent_heuristic -= prev_value;
					parent_heuristic += node_heuristic;
					prev_value = temp;
					node_heuristic = parent_heuristic;
					parent.aggregate.set(Float.floatToIntBits(parent_heuristic));
					parent = parent.super_node;
					parent_heuristic = Float.intBitsToFloat(parent.heuristic.get());
				}
			}
		}
	}

	// todo (4): implement improved heuristics

	public static CountData GetCount(LocalState board, int startingPos) { //countType is either "blank" for blank spaces, or "blocked" for blocked spaces
		CountData counts = new CountData();
		CountingAlgorithmData data = new CountingAlgorithmData();
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
				counts.blocks_heuristic += 10.0/Math.pow(10,max-1);
			}
		}
		return counts;
	}

	protected static void QueueNeighbours(CountingAlgorithmData data, int index, LocalState board){
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
