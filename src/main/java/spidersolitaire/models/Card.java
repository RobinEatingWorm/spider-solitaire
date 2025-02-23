package spidersolitaire.models;

public final class Card {
    private final int rank;
    private final int suit;
    private boolean faceUp;

    Card(int rank, int suit, boolean faceUp) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = faceUp;
    }

    public int getRank() {
        return this.rank;
    }

    public int getSuit() {
        return this.suit;
    }

    public boolean getFaceUp() {
        return this.faceUp;
    }

    void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }
}
