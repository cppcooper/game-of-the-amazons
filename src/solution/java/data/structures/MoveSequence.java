package data.structures;

import data.pod.Move;

import java.util.Collection;
import java.util.HashSet;

public class MoveSequence {
    private final Node head;
    private final Node tail;

    public MoveSequence(Collection<Move> moves){
        Node first = null;
        Node last = null;
        for(Move m : moves){
            Node n = new Node(m,last);
            last = n;
            if(first == null){
                first = n;
            }
        }
        head = first;
        tail = last;
    }

    protected boolean applyOnto(GameState board){
        Node c = head;
        while(c != null){
            if(!board.apply(c.move)){
                return false;
            }
            c = c.next;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        MoveSequence that = (MoveSequence) o;
        return head.equals(that.head);
    }

    @Override
    public int hashCode() {
        HashSet<Node> hasher = new HashSet<>();
        Node current = head;
        while(current != null){
            hasher.add(current);
            current = current.next;
        }
        return hasher.hashCode();
    }

    private class Node{
        Move move;
        Node next;
        Node prev;

        Node(Move move, Node prev) {
            this.move = move;
            this.prev = prev;
            if(prev != null) {
                prev.next = this;
            }
        }

        @Override
        public boolean equals(Object o) {
            Node other = (Node)o;
            if(move.equals(other.move)){
                if(next != null){
                    return next.equals(other.next);
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return move.hashCode();
        }
    }
}
