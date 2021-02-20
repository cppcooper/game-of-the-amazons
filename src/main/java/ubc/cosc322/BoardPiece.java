package ubc.cosc322;

public class BoardPiece {
    public int x;
    public int y;
    public int player;

    public BoardPiece(int index, int player){
        x = index / 11;
        y = index - (x * 11);
        this.player = player;
    }
    public BoardPiece(int x, int y, int player){
        this.x = x;
        this.y = y;
        this.player = player;
    }
    public boolean equals(BoardPiece other){
        if(x == other.x && y == other.y){
            return true;
        }
        return false;
    }
    public boolean equals(int x, int y){
        if(this.x == x && this.y == y){
            return true;
        }
        return false;
    }
}
