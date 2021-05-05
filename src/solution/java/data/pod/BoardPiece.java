package data.pod;

public class BoardPiece extends Position {
    public int player; //3 if non-player or 0 if not a piece

    public BoardPiece(BoardPiece other){
        super(other);
        player = other.player;
    }
    public BoardPiece(Position other) {
        super(other);
    }
    public BoardPiece(int index, int player) {
        super(index);
        this.player = player;
    }
    public BoardPiece(int x, int y, int player) {
        super(x, y);
        this.player = player;
    }
    public static int[] GetIndices(BoardPiece[] pieces){
        int[] indices = new int[pieces.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = pieces[i].getIndex();
        }
        return indices;
    }

}
