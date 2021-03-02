package structures;

public class BoardPiece {
    public Position pos;
    public int player; //3 if non-player or 0 if not a piece

    public BoardPiece(BoardPiece other){
        pos = new Position(other.pos);
        player = other.player;
    }
    public BoardPiece(int index, int player){
        pos = new Position(index);
        this.player = player;
    }
    public BoardPiece(int x, int y, int player){
        pos = new Position(x,y);
        this.player = player;
    }
}
