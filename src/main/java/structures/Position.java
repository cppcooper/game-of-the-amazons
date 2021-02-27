package structures;

public class Position {
    public int x;
    public int y;
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Position(int index){
        this.x = index / 11;
        this.y = index - (x * 11);
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
    public int CalculateIndex(){
        return (x*11)+y;
    }
    static public int CalculateIndex(int x, int y){
        return (x*11)+y;
    }
}
