package spidersolitaire.models;


public class Card {
    private final int rank;
    private final int suit;
    private boolean faceUp;
    private boolean movable;

    Card(int rank, int suit, boolean faceUp) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = true;
        this.movable = false;
    }

    public int getRank() {
        return this.rank;
    }

    public int getSuit() {
        return this.suit;
    }

    public boolean isFaceUp() {
        return this.faceUp;
    }

    public boolean isMovable() {
        return this.movable;
    }

    void setMoveable(boolean movable) {
        this.movable = movable;
    }

    void turn() {
        this.faceUp = !this.faceUp;
    }
}
