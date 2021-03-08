package algorithms.analysis;

import structures.LocalState;
import java.util.LinkedList;
import java.util.Queue;

public class Heuristics {

	public static class Data {
		public int blankCount;
		public int blockedCount;
		public int blockedTotal;

		public Data() {

		}
	}

	//todo: refactor int[] board => LocalState board
	public static Data GetCount(LocalState board, int startingPos) { //countType is either "blank" for blank spaces, or "blocked" for blocked spaces

		Data information = new Data();
		int x = startingPos / 11;
		int y = startingPos - (x * 11);

		int blankcount = 0; //count of blank spaces
		int blockedcount = 0; //count of blocked off spaces
		int blockedtotal = 0; //count of blocked off spaces + multipliers

		Queue<Integer> blankspace = new LinkedList<Integer>();
		Queue<Integer> blockedspace = new LinkedList<Integer>();
		int[] visited = new int[121];

		visited[startingPos] = startingPos;

		GetNearbySpaces(blankspace, blockedspace, visited, board, startingPos);

		if (!blankspace.isEmpty()) {
			while (!blankspace.isEmpty()) {
				int value = blankspace.poll();
				blankcount++;
				GetNearbySpaces(blankspace, blockedspace, visited, board, value);
			}
		}

		if (!blockedspace.isEmpty()) {
			while (!blockedspace.isEmpty()) {
				int value = blockedspace.poll();
				int qx = value / 11;
				int qy = value - (qx * 11);
				blockedcount++;

				int dx = Math.abs(x - qx);
				int dy = Math.abs(y - qy);
				int max = Math.max(dx,  dy);

				if (max == 1) {
					blockedtotal += 100*blockedcount;
				}else if (max == 2){
					blockedtotal += 10*blockedcount;
				}else {
					blockedtotal += 1*blockedcount;
				}

			}
		}

		information.blankCount = blankcount;
		information.blockedCount = blockedcount;
		information.blockedTotal = blockedtotal;

		return information;

	}

	//todo: refactor method to utilize MoveCompiler class, you'll likely want ScanAllDirections or possibly the other. If you need to operate on the tiles as you iterate, then we can add another variant of those methods and use lambda's to accomplish the end goal
	// todo: Lambda????
	// merge GetNeightbours function into this one
	public static void GetNearbySpaces(Queue<Integer> blankspace, Queue<Integer> blockedspace, int[] visited, LocalState board, int startingpos){

		int[] neighbours = board.GetNeighbours(startingpos);
		for (int pos: neighbours) {
			if (visited[pos] == 0) {
				visited[pos] = pos;
				int tileValue = board.ReadTile(pos);
				if(tileValue == 0) {
					blankspace.offer(pos);
				}else {
					blockedspace.offer(pos);
				}
			}
		}
	}
}
