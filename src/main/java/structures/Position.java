package structures;

import java.util.ArrayList;

public class Position {
    public int x;
    public int y;
    private int index = -1;
    public Position(Position other){
        this(other.x,other.y);
    }
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Position(ArrayList<Integer> msg_pos){
        // msg = (row,col)
        x = msg_pos.get(1);
        y = msg_pos.get(0);
    }
    public Position(int index){
        UpdatePosition(index);
    }
    public boolean equals(Position other){
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
    public int row(){
        return y;
    }
    public int col(){
        return x;
    }
    public void UpdatePosition(int index){
        this.index = index;
        if(index > 0 && index < 121) {
            y = index / 11;
            x = index - (y * 11);
        } else {
            x = -1;
            y = -1;
        }
    }
    public int CalculateIndex(){
        if(index < 0) {
            index = CalculateIndex(x, y);
        }
        return index;
    }
    public boolean IsValid(){
        return IsValid(x,y);
    }

    static public int CalculateIndex(int x, int y){
        return (y*11)+x;
    }
    static public boolean IsValid(int x, int y){
        return (x > 0 && y > 0) && (x < 11 && y < 11);
    }
}
