package structures;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.function.Function;

public class LocalState {
	private static HashSet<Integer> always_empty = null;
	private final ArrayList<Integer> board;
	private final BoardPiece[] player1 = new BoardPiece[4];
	private final BoardPiece[] player2 = new BoardPiece[4];
	private int move_number = 1;
	private int hash = -1;
	private boolean valid_hash = false;

	public LocalState(LocalState other){
		board = new ArrayList<>(other.board);
		move_number = other.move_number;
		valid_hash = other.valid_hash;
		hash = other.hash;
		for(int i = 0; i < player1.length; ++i){
			player1[i] = new BoardPiece(other.player1[i]);
			player2[i] = new BoardPiece(other.player2[i]);
		}
	}
	public LocalState(ArrayList<Integer> state, boolean find_pieces) throws Exception {
		if(state == null){
			throw new Exception("What did you do!");
		}
		if (always_empty == null) {
			always_empty = new HashSet<>();
			for(int x = 0; x < 11; ++x){
				for(int y = 0; y < 11; ++y){
					if(x == 0 || y == 0){
						always_empty.add(Position.CalculateIndex(x,y));
					}
				}
			}
		}
		board = new ArrayList<>(state);
		if(find_pieces) {
			FindPieces();
		}
	}

	public void DebugPrint(){
		for(int i = 0; i < board.size(); ++i){
			System.out.printf("%d ", board.get(i));
			if((i+1) % 11 == 0){
				System.out.println();
			}
		}
	}

	public void FindPieces(){
		int p1 = 0;
		int p2 = 0;
		int index = 0;
		for (Integer tile : board) {
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

	public final BoardPiece[] GetP1Pieces(){
		return player1;
	}
	public final BoardPiece[] GetP2Pieces(){
		return player2;
	}

	public int ReadTile(int index){
		return board.get(index);
	}
	public int ReadTile(int x, int y){
		return ReadTile(Position.CalculateIndex(x,y));
	}
	public int ReadTile(Position pos){
		return ReadTile(pos.x,pos.y);
	}

	public void SetTile(int index, int value){
		valid_hash = board.set(index, value) == value && valid_hash; // last == new && valid_hash
		// ie. true if already true AND the new value for the tile is the same as what was already there, otherwise false. IDE suggested this form so figured I'd explain it
	}
	public void SetTile(int x, int y, int value){
		SetTile(Position.CalculateIndex(x,y),value);
	}
	public void SetTile(Position pos, int value){
		SetTile(pos.x,pos.y,value);
	}

	public int[] GetNeighbours(int index){
		Position[] neighbours = new Position[8];
		neighbours[0] = new Position(index - 1);
		neighbours[1] = new Position(index + 1);
		neighbours[2] = new Position(index - 12);
		neighbours[3] = new Position(index - 11);
		neighbours[4] = new Position(index - 10);
		neighbours[5] = new Position(index + 10);
		neighbours[6] = new Position(index + 11);
		neighbours[7] = new Position(index + 12);

		int valid_count = 0;
		for(Position p : neighbours){
			if(p.IsValid()){
				valid_count++;
			}
		}
		int[] valid_neighbours = new int[valid_count];
		int i = 0;
		for(Position p : neighbours){
			if(p.IsValid()){
				valid_neighbours[i++] = p.CalculateIndex();
			}
		}
		return valid_neighbours;
	}

	public void MakeMove(Move move, boolean update_pieces) {
		if(move.IsValidFor(this)) {
			int player = ReadTile(move.start);
			if (update_pieces) {
				BoardPiece[] arr = null;
				if (player == 1) {
					arr = player1;
				} else if (player == 2) {
					arr = player2;
				}
				assert arr != null;
				for (BoardPiece p : arr) {
					if (p.pos.CalculateIndex() == move.start) {
						p.pos = new Position(move.piece);
					}
				}
			}
			SetTile(move.piece, player);
			SetTile(move.start, 0);
			SetTile(move.arrow, 3);
			move_number++;
			if(valid_hash){
				valid_hash = false;
			}
		}
	}

	public int GetMoveNumber(){
		return move_number;
	}

	public int hashCode(){
		if(valid_hash){
			return hash;
		}
		BitSet hasher = new BitSet(200);
		int index_count = 0;
		for(int index = 0; index < board.size(); ++index){
			if(!always_empty.contains(index)) {
				for (int bit = 0; bit < 2; ++bit) {
					boolean x = (board.get(index) & bit) != 0;
					hasher.set((index_count++ * 2) + bit, x);
				}
			}
		}
		hash = hasher.hashCode();
		valid_hash = true;
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocalState that = (LocalState) o;
		return board.equals(that.board);
	}
}
