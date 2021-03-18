package structures;

public class Move {
    public int start = -1;
    public int next = -1;
    public int arrow = -1;
    public Move(){}
    public Move(int start, int next, int arrow){
        this.start = start;
        this.next = next;
        this.arrow = arrow;
    }
    public boolean IsValidFor(LocalState state){
        int start_pos = state.ReadTile(start);
        if(start_pos > 0 && start_pos < 3){
            // return true if both tiles are empty (ie. equal to zero)
            return 0 == (state.ReadTile(next) | state.ReadTile(arrow));
        }
        return false;
    }
}
