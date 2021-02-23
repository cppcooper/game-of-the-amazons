package ubc.cosc322;

public class BoardPiece {
    public Position pos;
    public int player; //3 if non-player or 0 if not a piece

    public BoardPiece(int index, int player){
        int x = index / 11;
        int y = index - (x * 11);
        pos = new Position(x,y);
        this.player = player;
    }
    public BoardPiece(int x, int y, int player){
        pos = new Position(x,y);
        this.player = player;
    }
}
