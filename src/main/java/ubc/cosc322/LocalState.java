package ubc.cosc322;

import java.util.ArrayList;

public class LocalState {
	private ArrayList<Integer> board;
	private BoardPiece[] player1 = new BoardPiece[4];
	private BoardPiece[] player2 = new BoardPiece[4];

	public LocalState(ArrayList<Integer> state, boolean find_pieces) throws Exception {
		if(state == null){
			throw new Exception("What did you do!");
		}
		board = state;
		if(find_pieces) {
			int p1 = 0;
			int p2 = 0;
			int index = 0;
			for (Integer tile : state) {
				switch (tile) {
				case 1:
					player1[p1++] = new BoardPiece(index,1);
					break;
				case 2:
					player2[p2++] = new BoardPiece(index,2);
					break;
				}
				index++;
			}
		}
	}
	public final BoardPiece[] GetP1Pieces(){
		return player1;
	}
	public final BoardPiece[] GetP2Pieces(){
		return player2;
	}
	public int ReadTile(int x, int y){
		int index = (x*11)+y;
		return board.get(index);
	}
	public int ReadTile(BoardPiece pos){
		int index = (pos.x*11)+pos.y;
		return board.get(index);
	}
	public void SetTile(BoardPiece pos, int value){
		int index = (pos.x*11)+pos.y;
		board.set(index,value);
	}
	public void MakeMove(BoardPiece piece, BoardPiece new_pos, BoardPiece arrow_pos) throws Exception {
		if(ReadTile(piece) != piece.player || ReadTile(new_pos) != 0 || ReadTile(arrow_pos) != 0){
			throw new Exception("Invalid move");
		}
		SetTile(piece,0);
		SetTile(new_pos,piece.player);
		SetTile(arrow_pos,3);
	}

	public ArrayList<Integer> moveList(BoardPiece piece) {
		ArrayList<Integer> moves = new ArrayList<>();
		int x = piece.x;
		int y = piece.y;
		int index;
		
		int tileValue = 0;
		// North
		while (x-1 > 0 && y > 0 && x-1 < 11 && y < 11 && tileValue == 0) {
			x--;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}

		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// Northeast
		while (x-1 > 0 && y+1 > 0 && x-1 < 11 && y+1 < 11 && tileValue == 0) {
			x--;
			y++;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}

		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// East
		while (x > 0 && y+1 > 0 && x < 11 && y+1 < 11 && tileValue == 0) {
			y++;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}

		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// Southeast
		while (x+1 > 0 && y+1 > 0 && x+1 < 11 && y+1 < 11 && tileValue == 0) {
			x++;
			y++;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}


		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// South
		while (x+1 > 0 && y > 0 && x+1 < 11 && y < 11 && tileValue == 0) {
			x++;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}

		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// Southwest
		while (x+1 > 0 && y-1 > 0 && x+1 < 11 && y-1 < 11 && tileValue == 0) {
			x++;
			y--;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}

		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// West
		while (x > 0 && y-1 > 0 && x < 11 && y-1 < 11 && tileValue == 0) {
			y--;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}

		tileValue = 0;
		x = piece.x;
		y = piece.y;
		// NorthWest
		while (x-1 > 0 && y-1 > 0 && x-1 < 11 && y-1 < 11 && tileValue == 0) {
			x--;
			y--;
			tileValue = ReadTile(x,y);
			if (tileValue == 0) {
				index = (x*11)+y;
				moves.add(index);
			}
		}
		
		return moves;
	}
}
