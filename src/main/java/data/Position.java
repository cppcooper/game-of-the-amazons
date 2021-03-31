package data;

import tools.Tuner;

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
        x = msg_pos.get(1) - Tuner.coord_offset;
        y = msg_pos.get(0) - Tuner.coord_offset;
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
        return Tuner.coord_upper - y;
    }
    public int col(){
        return x+Tuner.coord_offset;
    }
    public void UpdatePosition(int index){
        this.index = index;
        if(index >= 0 && index < Tuner.state_size) {
            y = index / (Tuner.coord_upper);
            x = index - (y * Tuner.coord_upper);
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
        return (y*Tuner.coord_upper)+x;
    }
    static public boolean IsValid(int x, int y){
        return (x >= Tuner.coord_min && y >= Tuner.coord_min) && (x <= Tuner.coord_max && y <= Tuner.coord_max);
    }

    @Override
    public String toString() {
        return String.format("[%c,%2d]: %d", col()+64, row(), CalculateIndex());
    }
}
