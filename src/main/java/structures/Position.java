package structures;

public class Position {
    public int x;
    public int y;
    public Position(Position other){
        this(other.x,other.y);
    }
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Position(int index){
        if(index > 0 && index < 121) {
            x = index / 11;
            y = index - (x * 11);
        } else {
            x = -1;
            y = -1;
        }
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
        return CalculateIndex(x,y);
    }
    public boolean IsValid(){
        return IsValid(x,y);
    }
    static public int CalculateIndex(int x, int y){
        return (x*11)+y;
    }
    static public boolean IsValid(int x, int y){
        return (x > 0 && y > 0) && (x < 11 && y < 11);
    }
}
