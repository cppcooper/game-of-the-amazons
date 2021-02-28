package structures;

//Stores indices of a piece and arrow, can be used to represent a structures.Move
public class Move {
    public int start = -1;
    public int piece = -1;
    public int arrow = -1;
    public Move prev_move = null;
    public Move next_move = null;
    public Move(int start, int piece, int arrow){
        this.start = start;
        this.piece = piece;
        this.arrow = arrow;
    }
    public Move(int start, int piece, int arrow, Move prev){
        this(start,piece,arrow);
        prev_move = prev;
    }
    public boolean IsValidFor(LocalState state){
        int start_pos = state.ReadTile(start);
        if(start_pos > 0 && start_pos < 3){
            // return true if both tiles are empty (ie. equal to zero)
            return 0 == (state.ReadTile(piece) | state.ReadTile(arrow));
        }
        return false;
    }
}
