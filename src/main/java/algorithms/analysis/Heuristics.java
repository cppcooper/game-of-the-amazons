package algorithms.analysis;

import java.util.LinkedList;
import java.util.Queue;

public class Heuristics {

	public static int GetCount(int[] board, int index, String countType) { //countType is either "blank" for blank spaces, or "blocked" for blocked spaces
		int x = index / 11;
		int y = index - (x * 11);

		int blankcount = 0; //count of blank spaces
		int blockedcount = 0; //count of blocked off spaces
		int blockedtotal = 0; //count of blocked off spaces + multipliers

		Queue<Integer> blankspace = new LinkedList<Integer>();
		Queue<Integer> blockedspace = new LinkedList<Integer>();
		int[] visited = new int[121];

		visited[index] = index;

		GetNearbySpaces(blankspace, blockedspace, visited, board, x, y);

		if (!blankspace.isEmpty()) {
			while (!blankspace.isEmpty()) {
				int value = blankspace.poll();
				int qx = value / 11;
				int qy = value - (qx * 11);
				blankcount++;
				GetNearbySpaces(blankspace, blockedspace, visited, board, qx, qy);
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
		
		if (countType.equals("blank")) {
			return blankcount;
		}else if (countType.equals("blocked")) {
			return blockedtotal;
		}else {
			return -1;
		}
	}



	public static void GetNearbySpaces(Queue<Integer> blankspace, Queue<Integer> blockedspace, int[] visited, int[] board, int x, int y){

		int value = 0;

		// North
		if (x-1 > 0 && y > 0 && x-1 < 11 && y < 11) {
			value = (x-1)*11+y;
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}
		// Northeast
		if (x-1 > 0 && y+1 > 0 && x-1 < 11 && y+1 < 11) {
			value = (x-1)*11+(y+1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// East
		if (x > 0 && y+1 > 0 && x < 11 && y+1 < 11) {
			value = x*11+(y+1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// Southeast
		if (x+1 > 0 && y+1 > 0 && x+1 < 11 && y+1 < 11) {
			value = (x+1)*11+(y+1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// South
		if (x+1 > 0 && y > 0 && x+1 < 11 && y < 11) {
			value = (x+1)*11+y;
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// Southwest
		if (x+1 > 0 && y-1 > 0 && x+1 < 11 && y-1 < 11) {
			value = (x+1)*11+(y-1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// West
		if(x > 0 && y-1 > 0 && x < 11 && y-1 < 11) {
			value = x*11+(y-1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// NorthWest
		if (x-1 > 0 && y-1 > 0 && x-1 < 11 && y-1 < 11) {
			value = (x-1)*11+(y-1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}
	}



	public static void AddToQueue(Queue<Integer> blankspace, Queue<Integer> blockedspace, int[] visited, int[] board, int index) {
		if (visited[index] == 0) {
			visited[index] = index;
			if(board[index] == 0) {
				blankspace.offer(index);
			}else {
				blockedspace.offer(index);
			}
		}
	}

}
