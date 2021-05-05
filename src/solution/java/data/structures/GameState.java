package data.structures;

import algorithms.search.MoveCompiler;
import data.pod.BoardPiece;
import data.pod.Move;
import data.pod.Position;
import tools.Tuner;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.function.Function;

public class GameState {
	private ArrayList<Integer> board;
	private Move last_move = null;
	private int round_number = 1;
	private int hash = -1;
	private boolean valid_hash = false;
	public static int[] start = { //this is upside down compared to the GUI
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0};
	public GameState(){
		this(start);
	}
	public GameState(int[] state){
		board = new ArrayList<>(Tuner.state_size);
		if(state.length > Tuner.state_size) {
			Function<Integer,Integer> convert = (index) -> {
				int y = index / 11;
				int x = index - (y * 11);
				y--;x--;
				if(x < 0 || y < 0){
					return -1;
				}
				return Position.calculateIndex(x,y);
			};
			for (int i = 0; i < state.length; ++i) {
				int index = convert.apply(i);
				if(index >= 0) {
					board.add(state[i]);
				}
			}
		} else {
			for (int i = 0; i < state.length; ++i) {
				board.add(state[i]);
			}
		}
		TransformState();
	}
	public GameState(GameState other){
		board = new ArrayList<>(other.board);
		last_move = other.last_move;
		round_number = other.round_number;
		hash = other.hash;
		valid_hash = other.valid_hash;
	}
	private void TransformState(){
		ArrayList<Integer> transformed_state = new ArrayList<>(board.size());
		for(int y = Tuner.coord_max; y >= Tuner.coord_min; --y){
			for(int x = Tuner.coord_min; x <= Tuner.coord_max; ++x){
				transformed_state.add(board.get(Position.calculateIndex(x,y)));
			}
		}
		board = transformed_state;
	}
	public void DebugPrint(){
		final char[] col = {' ','a','b','c','d','e','f','g','h','i','j'};
		System.out.print("    ");
		for(int i = 0; i < 10; ++i){
			System.out.printf("%2d ", i);
		}
		System.out.print("\n    ------------------------------\n");
		for(int y = 0; y <= Tuner.coord_upper; ++y){
			for(int x = -Tuner.coord_offset; x <= Tuner.coord_max; ++x){
				if(y != 10){
					if(x != -Tuner.coord_offset){
						if(x == Tuner.coord_max){
							System.out.printf("%2d | %2d", board.get(Position.calculateIndex(x,y)), Tuner.coord_upper - y);
						} else {
							System.out.printf("%2d ", board.get(Position.calculateIndex(x,y)));
						}
					} else {
						System.out.printf("%2d |", y);
					}
				} else {
					if(x == -Tuner.coord_offset){
						System.out.print("    ------------------------------\n    ");
					} else {
						System.out.printf(" %c ", col[x+Tuner.coord_offset]);
					}
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	public boolean apply(MoveSequence moves){
		return moves.applyOnto(this);
	}
	public boolean apply(Move move) {
		//if the move is valid for this state
		if (move.IsValidFor(this)) {
			int player = readTile(move.start);
			setTile(move.next, player);
			setTile(move.start, 0);
			setTile(move.arrow, 3);
			round_number++;
			last_move = move;
			valid_hash = false;
			return true;
		}
		return false;
	}

	public BoardPiece[] findPieces(int type){
		ArrayList<Integer> indices = new ArrayList<>();
		for(int idx : MoveCompiler.GetAllValidPositions()){
			if(readTile(idx) == type){
				indices.add(idx);
			}
		}
		BoardPiece[] pieces = new BoardPiece[indices.size()];
		int i = 0;
		for(int idx : indices){
			pieces[i++] = new BoardPiece(idx, type);
		}
		return pieces;
	}
	public int getRoundNum(){
		return round_number;
	}
	public int readTile(int index){
		return board.get(index);
	}
	public void setTile(int index, int value){
		valid_hash = valid_hash && board.set(index, value) == value; // last == new && valid_hash
		// ie. true if already true AND the new value for the tile is the same as what was already there, otherwise false. IDE suggested this form so figured I'd explain it
	}
	public boolean isTileEmpty(int index){
		return readTile(index) == 0;
	}
	public boolean isPlayerTile(int index){
		return isTileEmpty(index) && readTile(index) != 3;
	}

	public int hashCode(){
		if(valid_hash){
			return hash;
		}
		BitSet hasher = new BitSet(200);
		int index_count = 0;
		for(int index = 0; index < board.size(); ++index){
			for (int bit = 0; bit < 2; ++bit) {
				boolean x = (board.get(index) & bit) != 0;
				hasher.set((index_count++ * 2) + bit, x);
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
		GameState that = (GameState) o;
		return board.equals(that.board) && ((last_move == null && that.last_move == null) || (last_move != null && last_move.equals(that.last_move)));
	}
}
