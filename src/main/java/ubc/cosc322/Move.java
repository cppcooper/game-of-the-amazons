package ubc.cosc322;

//Stores indices of a piece and arrow, can be used to represent a Move
public class Move {
    public int start = -1;
    public int piece = -1;
    public int arrow = -1;
    public Move prev_move = null;
    public Move next_move = null;
    Move(int start, int piece, int arrow){
        this.start = start;
        this.piece = piece;
        this.arrow = arrow;
    }
    Move(int start, int piece, int arrow, Move prev){
        this(start,piece,arrow);
        prev_move = prev;
    }
}
