package data.pod;

import data.structures.GameState;

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
    public boolean IsValidFor(GameState state){
        if(!(start < 0)) {
            int start_pos = state.ReadTile(start);
            if (start_pos > 0 && start_pos < 3) {
                // return true if both tiles are empty (ie. equal to zero)
                boolean next_empty = state.ReadTile(next) == 0;
                boolean arrow_valid = arrow == start || state.ReadTile(arrow) == 0;
                return next_empty && arrow_valid;
            }
        }
        return false;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return start == move.start && next == move.next && arrow == move.arrow;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + next;
        result = 31 * result + arrow;
        return result;
    }

    @Override
    public String toString() {
        Position s = new Position(start);
        Position n = new Position(next);
        Position a = new Position(arrow);
        return String.format("start: %s\nnext:  %s\narrow: %s\n",s,n,a);
    }
}
