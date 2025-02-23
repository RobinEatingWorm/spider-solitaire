package spidersolitaire.adapters;

import spidersolitaire.models.Card;

public final class CardViewModel {
    private final int rank;
    private final int suit;
    private final boolean faceUp;

    private final String location;
    private final int locationIndex;
    private final int stackIndex;

    private final boolean columnMovable;

    CardViewModel(Card card, String location, int locationIndex, int stackIndex, boolean columnMovable) {
        this.rank = card.getRank();
        this.suit = card.getSuit();
        this.faceUp = card.getFaceUp();
        this.location = location;
        this.locationIndex = locationIndex;
        this.stackIndex = stackIndex;
        this.columnMovable = columnMovable;
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

    public String getLocation() {
        return this.location;
    }

    public int getLocationIndex() {
        return this.locationIndex;
    }

    public int getStackIndex() {
        return this.stackIndex;
    }

    public boolean getColumnMovable() {
        return this.columnMovable;
    }
}
